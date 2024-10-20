package com.ezzenix.rendering.chunkbuilder;

import com.ezzenix.Client;
import com.ezzenix.blocks.Block;
import com.ezzenix.blocks.BlockState;
import com.ezzenix.blocks.Blocks;
import com.ezzenix.blocks.PlantBlock;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.enums.Direction;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.ChunkPos;
import com.ezzenix.rendering.util.RenderLayer;
import com.ezzenix.rendering.util.VertexBuffer;
import com.ezzenix.rendering.util.VertexFormat;
import com.ezzenix.util.BufferBuilder;
import com.ezzenix.world.World;
import com.ezzenix.world.chunk.Chunk;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.primitives.Floats;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.opengl.GL30.GL_FLOAT;

public class ChunkBuilder {
	private static final PriorityBlockingQueue<RebuildTask> taskQueue = Queues.newPriorityBlockingQueue();
	private static final ExecutorService executorService = Executors.newFixedThreadPool(1);

	private static final VertexFormat VERTEX_FORMAT = new VertexFormat(GL_FLOAT, 3, GL_FLOAT, 2, GL_FLOAT, 1); // pos, uv, ao

	public static void pollQueue() {
		RebuildTask task = taskQueue.poll();
		if (task == null) return;

		CompletableFuture<Map<RenderLayer, BufferBuilder>> future = CompletableFuture.supplyAsync(task::run, executorService);
		future.thenAccept(builders -> {
			if (builders == null) return;
			Scheduler.recordMainThreadCall(() -> {
				for (RenderLayer layer : task.builtChunk.buffers.keySet()) {
					BufferBuilder builder = builders.get(layer);
					if (builder != null) {
						VertexBuffer buffer = task.builtChunk.buffers.get(layer);
						buffer.upload(builder.end());
					}
				}
				task.builtChunk.currentRebuildTask = null;
			});
		}).exceptionally(ex -> {
			ex.printStackTrace();
			return null;
		});
	}

	public static class BuiltChunk {
		private final Chunk chunk;
		private final Matrix4f translationMatrix;
		RebuildTask currentRebuildTask;

		public final Map<RenderLayer, VertexBuffer> buffers;

		public BuiltChunk(Chunk chunk) {
			this.chunk = chunk;
			this.translationMatrix = new Matrix4f().translate(new Vector3f(chunk.getPos().x * Chunk.CHUNK_WIDTH, 0, chunk.getPos().z * Chunk.CHUNK_WIDTH));

			this.buffers = Maps.newHashMap();
			for (RenderLayer layer : RenderLayer.BLOCK_LAYERS) {
				buffers.put(layer, new VertexBuffer(VERTEX_FORMAT, VertexBuffer.Usage.DYNAMIC));
			}
		}

		public void rebuild() {
			if (this.currentRebuildTask != null) {
				return;
			}
			RebuildTask task = new RebuildTask(this);
			taskQueue.offer(task);
			this.currentRebuildTask = task;
		}

		public Chunk getChunk() {
			return this.chunk;
		}

		public Matrix4f getTranslationMatrix() {
			return translationMatrix;
		}

		public void dispose() {
			for (VertexBuffer vertexBuffer : this.buffers.values()) {
				vertexBuffer.close();
			}
		}
	}

	private static class RebuildTask implements Comparable<RebuildTask> {
		private final BuiltChunk builtChunk;
		private final AtomicBoolean cancelled = new AtomicBoolean(false);

		public RebuildTask(BuiltChunk builtChunk) {
			this.builtChunk = builtChunk;
		}

		public Map<RenderLayer, BufferBuilder> run() {
			if (cancelled.get()) return null;

			Map<RenderLayer, BufferBuilder> builders = rebuildChunk(this.builtChunk);

			if (cancelled.get()) return null;
			return builders;
		}

		public void cancel() {
			this.cancelled.set(true);
		}

		public float getDistance() {
			return this.builtChunk.getChunk().getPos().distanceTo(new ChunkPos(Client.getCamera().getPosition()));
		}

		@Override
		public int compareTo(@NotNull ChunkBuilder.RebuildTask rebuildTask) {
			return Floats.compare(this.getDistance(), rebuildTask.getDistance());
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

	public static Map<RenderLayer, BufferBuilder> rebuildChunk(BuiltChunk builtChunk) {
		Chunk chunk = builtChunk.getChunk();
		World world = chunk.getWorld();
		BlockPos chunkBlockPos = new BlockPos(chunk.getWorldPos());

		Map<RenderLayer, BufferBuilder> builders = Maps.newHashMap();

		//System.out.println("rebuilding chunk " + chunk.getPos());

		long start = System.currentTimeMillis();

		for (int x = 0; x < 16; x++) {
			for (int y = 0; y <= Chunk.CHUNK_HEIGHT; y++) {
				for (int z = 0; z < 16; z++) {

					BlockPos blockPos = new BlockPos(chunkBlockPos.x + x, y, chunkBlockPos.z + z);
					BlockState blockState = world.getBlockState(blockPos);
					Block block = blockState.getBlock();
					if (block == Blocks.AIR) continue;

					RenderLayer layer = block.getRenderLayer();
					BufferBuilder builder = builders.computeIfAbsent(layer, v -> new BufferBuilder(400000, VERTEX_FORMAT));

					if (block instanceof PlantBlock) {
						Vector3f midPos = new Vector3f(x + 0.5f, y, z + 0.5f);
						Vector2f[] uv = block.getTexture().getSideUV();
						float flowerSize = 0.9f;
						for (float deg = 45; deg <= (45 + 90 * 4); deg += 90) {
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

						Vector3f v0 = new Vector3f(x, y, z).add(unitCubeFace[0]);
						Vector3f v1 = new Vector3f(x, y, z).add(unitCubeFace[1]);
						Vector3f v2 = new Vector3f(x, y, z).add(unitCubeFace[2]);
						Vector3f v3 = new Vector3f(x, y, z).add(unitCubeFace[3]);

						addQuad(builder, v0, v1, v2, v3, uv, ao);
					}
				}
			}
		}

		return builders;
	}
}
