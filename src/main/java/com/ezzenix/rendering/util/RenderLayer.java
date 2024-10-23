package com.ezzenix.rendering.util;

import com.ezzenix.Client;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.engine.opengl.Texture;
import com.ezzenix.rendering.RenderSystem;
import com.ezzenix.rendering.Renderer;
import com.ezzenix.util.Identifier;
import com.google.common.collect.ImmutableList;
import org.checkerframework.common.value.qual.IntRange;

import java.util.Collection;
import java.util.stream.IntStream;

import static org.lwjgl.opengl.GL30.*;

public class RenderLayer {
	private static VertexFormat FORMAT = new VertexFormat(GL_FLOAT, 3, GL_FLOAT, 2, GL_FLOAT, 1, GL_INT, 1);
	public static RenderLayer SOLID = new RenderLayer(new Shader("solid")).format(FORMAT).cull().depth(GL_LESS).setExpectedBufferSize(400000);
	public static RenderLayer FOLIAGE = new RenderLayer(new Shader("foliage")).format(FORMAT).blend(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA).depth(GL_LESS).depthMask(false).setExpectedBufferSize(400000);
	public static RenderLayer WATER = new RenderLayer(new Shader("water")).format(FORMAT).blend(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA).depth(GL_LESS).depthMask(false).setExpectedBufferSize(400000);

	private static final Shader BLOCK_OVERLAY_SHADER = new Shader("block_overlay");
	public static RenderLayer[] BREAK_OVERLAYS = IntStream.range(0, 10).mapToObj(i -> {
		Texture t = Client.getTextureManager().getTexture(Identifier.of("break_overlay/stage_" + i));
		return new RenderLayer(BLOCK_OVERLAY_SHADER).format(VertexFormat.POSITION_UV).cull().depth(GL_LESS).blend(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA).setTexture(t).setExpectedBufferSize(5000);
	}).toArray(RenderLayer[]::new);

	public static Collection<RenderLayer> BLOCK_LAYERS = ImmutableList.of(SOLID, FOLIAGE, WATER);

	private final Shader shader;
	private Texture texture;
	private boolean cull_face = false;
	private int depth_func = GL_NONE;
	private boolean depthMask = true;
	private boolean colorMask = true;
	private int blendFactorS = GL_NONE;
	private int blendFactorD = GL_NONE;
	private VertexFormat vertexFormat;
	private int expectedBufferSize = 256;

	public RenderLayer(Shader shader) {
		this.shader = shader;
	}

	public RenderLayer format(VertexFormat format) {
		this.vertexFormat = format;
		return this;
	}

	public RenderLayer cull() {
		this.cull_face = true;
		return this;
	}

	public RenderLayer depth(int func) {
		this.depth_func = func;
		return this;
	}

	public RenderLayer blend(int sfactor, int dfactor) {
		this.blendFactorS = sfactor;
		this.blendFactorD = dfactor;
		return this;
	}

	public RenderLayer depthMask(boolean mask) {
		this.depthMask = mask;
		return this;
	}

	public RenderLayer colorMask(boolean mask) {
		this.colorMask = mask;
		return this;
	}

	public RenderLayer setTexture(Texture texture) {
		this.texture = texture;
		return this;
	}

	public RenderLayer setExpectedBufferSize(int size) {
		this.expectedBufferSize = size;
		return this;
	}

	public int getExpectedBufferSize() {
		return this.expectedBufferSize;
	}

	public Shader getShader() {
		return this.shader;
	}

	public VertexFormat getVertexFormat() {
		return this.vertexFormat;
	}

	public void apply() {
		if (this.texture != null) {
			getShader().setTexture(0, this.texture);
		}
		getShader().bind();
		getShader().setUniforms();
		if (this.cull_face) {
			RenderSystem.enableCulling();
		}
		if (this.depth_func != GL_NONE) {
			RenderSystem.enableDepthTest();
			glDepthFunc(this.depth_func);
		}
		if (!this.depthMask) {
			glDepthMask(false);
		}
		if (!this.colorMask) {
			glColorMask(false, false, false, false);
		}
		if (this.blendFactorS != GL_NONE && this.blendFactorD != GL_NONE) {
			RenderSystem.enableBlend();
			glBlendFunc(this.blendFactorS, this.blendFactorD);
		}
	}

	public void unapply() {
		getShader().unbind();
		if (this.cull_face) {
			RenderSystem.disableCulling();
		}
		if (this.depth_func != GL_NONE) {
			RenderSystem.disableDepthTest();
			glDepthFunc(GL_LESS);
		}
		if (!this.depthMask) {
			glDepthMask(true);
		}
		if (!this.colorMask) {
			glColorMask(true, true, true, true);
		}
		if (this.blendFactorS != GL_NONE && this.blendFactorD != GL_NONE) {
			RenderSystem.disableBlend();
			glBlendFunc(this.blendFactorS, this.blendFactorD);
		}
	}

	public void draw(VertexBuffer vertexBuffer) {
		this.apply();
		vertexBuffer.draw();
		this.unapply();
	}
}
