package com.ezzenix.rendering.chunk;

import com.ezzenix.Client;
import com.ezzenix.blocks.Block;
import com.ezzenix.blocks.BlockState;
import com.ezzenix.blocks.Blocks;
import com.ezzenix.blocks.PlantBlock;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.enums.Direction;
import com.ezzenix.math.BlockPos;
import com.ezzenix.rendering.Renderer;
import com.ezzenix.rendering.util.BufferBuilder;
import com.ezzenix.rendering.util.RenderLayer;
import com.ezzenix.rendering.util.VertexBuffer;
import com.ezzenix.rendering.util.VertexFormat;
import com.ezzenix.world.World;
import com.ezzenix.world.chunk.Chunk;
import com.google.common.collect.Maps;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL30.GL_FLOAT;

public class ChunkBuilder {
	private static final ExecutorService executorService = Executors.newFixedThreadPool(1);

	private static final VertexFormat VERTEX_FORMAT = new VertexFormat(GL_FLOAT, 3, GL_FLOAT, 2, GL_FLOAT, 1); // pos, uv, ao

	private static boolean isBuilding;

	private static final Map<RenderLayer, BufferBuilder> builders = RenderLayer.BLOCK_LAYERS.stream()
		.collect(Collectors.toMap(layer -> layer, layer -> new BufferBuilder(layer.getExpectedBufferSize())));

	public static BuiltChunk getNextChunk() {
		int shortestDistance = Integer.MAX_VALUE;
		BuiltChunk builtChunk = null;
		for (BuiltChunk v : Renderer.getWorldRenderer().getBuiltChunkStorage().getBuiltChunks()) {
			if (v.needsRebuild && v.getChunk().hasGenerated) {
				int distance = v.getChunk().getPos().distanceTo(Client.getCamera().getChunkPos());
				if (distance < shortestDistance) {
					shortestDistance = distance;
					builtChunk = v;
				}
			}
		}
		return builtChunk;
	}

	public static void pollQueue() {
		if (isBuilding) return;

		BuiltChunk builtChunk = getNextChunk();
		if (builtChunk == null) return;
		builtChunk.needsRebuild = false;

		isBuilding = true;

		//System.out.println("rebuilding " + builtChunk.getChunk().getPos());

		for (BufferBuilder builder : builders.values()) {
			builder.clear();
		}

		CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> rebuildChunk(builtChunk), executorService);
		future.thenAccept(success -> {
			if (success) {
				Scheduler.recordMainThreadCall(() -> {
					for (RenderLayer layer : RenderLayer.BLOCK_LAYERS) {
						BufferBuilder builder = builders.get(layer);
						VertexBuffer buffer = builtChunk.buffers.get(layer);
						buffer.upload(builder);
					}

					isBuilding = false;
				});
			}
		}).exceptionally(ex -> {
			Client.LOGGER.warn(ex);
			return null;
		});
	}

	public static class BuiltChunk {
		private final Chunk chunk;
		private final Matrix4f translationMatrix;
		private boolean needsRebuild;

		public final Map<RenderLayer, VertexBuffer> buffers;


		public BuiltChunk(Chunk chunk) {
			this.chunk = chunk;
			this.translationMatrix = new Matrix4f().translate(new Vector3f(chunk.getPos().x * Chunk.CHUNK_WIDTH, 0, chunk.getPos().z * Chunk.CHUNK_WIDTH));

			this.needsRebuild = true;

			this.buffers = Maps.newHashMap();
			for (RenderLayer layer : RenderLayer.BLOCK_LAYERS) {
				buffers.put(layer, new VertexBuffer(VERTEX_FORMAT, VertexBuffer.Usage.DYNAMIC));
			}
		}

		public void scheduleRebuild() {
			this.needsRebuild = true;
		}

		public Chunk getChunk() {
			return this.chunk;
		}

		public Matrix4f getTranslationMatrix() {
			return translationMatrix;
		}

		public void clear() {
			for (VertexBuffer vertexBuffer : this.buffers.values()) {
				vertexBuffer.clear();
			}
		}

		public void delete() {
			for (VertexBuffer vertexBuffer : this.buffers.values()) {
				vertexBuffer.close();
			}
		}
	}


	// building
	private static void applyOffsetRotation(Direction direction, Vector3i offset) {
		switch (direction) {
			case DOWN -> offset.set(-offset.x, -offset.y, -offset.z);
			case NORTH -> offset.set(offset.x, offset.z, -offset.y);
			case SOUTH -> offset.set(-offset.x, -offset.z, offset.y);
			case EAST -> offset.set(offset.y, -offset.x, offset.z);
			case WEST -> offset.set(-offset.y, offset.x, -offset.z);
		}
	}

	private static int isBlockAt(World world, BlockPos blockPos, Direction direction, Vector3i offset) {
		applyOffsetRotation(direction, offset);
		blockPos = blockPos.add(offset.x, offset.y, offset.z);
		BlockState blockState = world.getBlockState(blockPos);
		return (blockState.getBlock() == Blocks.AIR || !blockState.getBlock().isSolid()) ? 0 : 1;
	}

	private static float solveAO(int side1, int side2, int corner) {
		if (side1 == 1 && side2 == 1) {
			return 1;
		}
		return 1 - ((float) (3 - (side1 + side2 + corner)) / 3);
	}

	private static float[] calculateAO(World world, BlockPos blockPos, Direction direction) {
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock().isTransparent()) return new float[]{0, 0, 0, 0};

		int W = isBlockAt(world, blockPos, direction, new Vector3i(-1, 1, 0));
		int NW = isBlockAt(world, blockPos, direction, new Vector3i(-1, 1, -1));
		int N = isBlockAt(world, blockPos, direction, new Vector3i(0, 1, -1));
		int NE = isBlockAt(world, blockPos, direction, new Vector3i(1, 1, -1));
		int E = isBlockAt(world, blockPos, direction, new Vector3i(1, 1, 0));
		int SE = isBlockAt(world, blockPos, direction, new Vector3i(1, 1, 1));
		int S = isBlockAt(world, blockPos, direction, new Vector3i(0, 1, 1));
		int SW = isBlockAt(world, blockPos, direction, new Vector3i(-1, 1, 1));

		float ao1 = solveAO(W, N, NW);
		float ao2 = solveAO(W, S, SW);
		float ao3 = solveAO(E, S, SE);
		float ao4 = solveAO(E, N, NE);

		return switch (direction) {
			case UP -> new float[]{ao1, ao2, ao3, ao4};
			case DOWN, SOUTH -> new float[]{ao4, ao3, ao2, ao1};
			case NORTH -> new float[]{ao3, ao4, ao1, ao2};
			case EAST -> new float[]{ao2, ao3, ao4, ao1};
			case WEST -> new float[]{ao3, ao2, ao1, ao4};
		};
	}

	private static void addQuad(BufferBuilder builder, Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3, Vector2f[] uv, float[] ao) {
		builder.vertex(v0.x, v0.y, v0.z).texture(uv[0]).putFloat(ao[0]).next();
		builder.vertex(v1.x, v1.y, v1.z).texture(uv[1]).putFloat(ao[1]).next();
		builder.vertex(v2.x, v2.y, v2.z).texture(uv[2]).putFloat(ao[2]).next();

		builder.vertex(v2.x, v2.y, v2.z).texture(uv[2]).putFloat(ao[2]).next();
		builder.vertex(v3.x, v3.y, v3.z).texture(uv[3]).putFloat(ao[3]).next();
		builder.vertex(v0.x, v0.y, v0.z).texture(uv[0]).putFloat(ao[0]).next();
	}

	public static boolean rebuildChunk(BuiltChunk builtChunk) {
		Chunk chunk = builtChunk.getChunk();
		World world = chunk.getWorld();
		BlockPos chunkBlockPos = new BlockPos(chunk.getWorldPos());

		for (int x = 0; x < 16; x++) {
			for (int y = 0; y <= Chunk.CHUNK_HEIGHT; y++) {
				for (int z = 0; z < 16; z++) {

					BlockPos blockPos = new BlockPos(chunkBlockPos.x + x, y, chunkBlockPos.z + z);
					BlockState blockState = world.getBlockState(blockPos);
					Block block = blockState.getBlock();
					if (block == Blocks.AIR) continue;

					RenderLayer layer = block.getRenderLayer();
					BufferBuilder builder = builders.get(layer);

					if (block instanceof PlantBlock) {
						Vector3f midPos = new Vector3f(x + 0.5f, y, z + 0.5f);
						Vector2f[] uv = block.getTexture().getSideUV();
						float flowerSize = 0.9f;
						for (float deg = 45; deg <= (45 + 90 * 2); deg += 90) {
							Vector3f lookVector = new Vector3f((float) -Math.cos(Math.toRadians(deg)), 0.0f, (float) -Math.sin(Math.toRadians(deg)));
							lookVector.mul((float) Math.pow(flowerSize, 4));
							addQuad(builder,
								new Vector3f(midPos).add(-lookVector.x, flowerSize, -lookVector.z),
								new Vector3f(midPos).add(-lookVector.x, 0, -lookVector.z),
								new Vector3f(midPos).add(lookVector.x, 0, lookVector.z),
								new Vector3f(midPos).add(lookVector.x, flowerSize, lookVector.z),
								uv, new float[]{0, 0, 0, 0}
							);
						}
						continue;
					}

					for (Direction direction : Direction.values()) {

						BlockPos otherBlockPos = blockPos.add(direction.getNormal().x, direction.getNormal().y, direction.getNormal().z);
						BlockState otherBlockState = world.getBlockState(otherBlockPos);
						if (!block.shouldRenderFace(blockState, otherBlockState)) continue;

						Vector3f[] unitCubeFace = UnitCube.getFace(direction);
						Vector2f[] uv = switch (direction) {
							case UP -> block.getTexture().getTopUV();
							case DOWN -> block.getTexture().getBottomUV();
							default -> block.getTexture().getSideUV();
						};
						float[] ao = calculateAO(world, blockPos, direction);

						Vector3f mid = new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f);
						Vector3f v0 = new Vector3f(mid).add(unitCubeFace[0]);
						Vector3f v1 = new Vector3f(mid).add(unitCubeFace[1]);
						Vector3f v2 = new Vector3f(mid).add(unitCubeFace[2]);
						Vector3f v3 = new Vector3f(mid).add(unitCubeFace[3]);

						addQuad(builder, v0, v1, v2, v3, uv, ao);
					}
				}
			}
		}

		return true;
	}
}
