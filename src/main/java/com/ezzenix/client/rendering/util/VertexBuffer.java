package com.ezzenix.client.rendering.util;

import com.ezzenix.client.gui.library.GuiUtil;
import com.ezzenix.engine.opengl.Shader;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

public class VertexBuffer {
	private final Usage usage;
	private final Shader shader;
	private int vertexBufferId;
	//private int indexBufferId;
	private int vertexArrayId;
	private int vertexCount;

	private int vertexBuilderCount;
	private final List<Float> vertexBuilderList;

	public enum Usage {
		STATIC(GL_STATIC_DRAW),
		DYNAMIC(GL_DYNAMIC_DRAW);
		final int id;
		Usage(int id) {
			this.id = id;
		}
	}

	public VertexBuffer(Shader shader, VertexFormat vertexFormat, Usage usage) {
		this.shader = shader;
		this.usage = usage;

		this.vertexBufferId = glGenBuffers();
		//this.indexBufferId = glGenBuffers();
		this.vertexArrayId = glGenVertexArrays();

		glBindVertexArray(this.vertexArrayId);
		glBindBuffer(GL_ARRAY_BUFFER, this.vertexBufferId);

		vertexFormat.use();

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);

		this.vertexBuilderCount = 0;
		this.vertexBuilderList = new ArrayList<>();
	}

	public void bind() {
		glBindVertexArray(this.vertexArrayId);
	}

	public void unbind() {
		glBindVertexArray(0);
	}

	public void uploadAndDraw() {
		this.upload();
		this.draw();
	}

	public void draw() {
		if (this.vertexCount == 0)
			throw new IllegalStateException("Attempt to call .draw() with nothing uploaded");

		shader.bind();
		this.bind();
		glDrawArrays(GL_TRIANGLES, 0, this.vertexCount);
		this.unbind();
	}

	public void upload() {
		if (vertexBuilderCount == 0)
			throw new RuntimeException("Attempt to upload VertexBuffer with 0 vertices");

		this.vertexCount = this.vertexBuilderCount;

		FloatBuffer buffer = BufferUtils.createFloatBuffer(this.vertexBuilderList.size());

		for (Float v : this.vertexBuilderList) {
			buffer.put(v);
		}
		buffer.flip();

		this.bind();
		glBindBuffer(GL_ARRAY_BUFFER, this.vertexBufferId);
		glBufferData(GL_ARRAY_BUFFER, buffer, this.usage.id);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		this.reset();
	}

	public void close() {
		if (this.vertexBufferId >= 0) {
			glDeleteBuffers(this.vertexBufferId);
			this.vertexBufferId = -1;
		}
		//if (this.indexBufferId >= 0) {
		//	glDeleteBuffers(this.indexBufferId);
		//	this.indexBufferId = -1;
		//}
		if (this.vertexArrayId >= 0) {
			glDeleteVertexArrays(this.vertexArrayId);
			this.vertexArrayId = -1;
		}
	}

	public VertexBuffer vertex(float x, float y, float z) {
		this.vertexBuilderList.add(x);
		this.vertexBuilderList.add(y);
		this.vertexBuilderList.add(z);
		return this;
	}

	public VertexBuffer vertex(float x, float y) {
		Vector2f ndc = GuiUtil.toNormalizedDeviceCoordinates(x, y);
		this.vertexBuilderList.add(ndc.x);
		this.vertexBuilderList.add(ndc.y);
		return this;
	}

	public VertexBuffer color(float r, float g, float b) {
		this.vertexBuilderList.add(r);
		this.vertexBuilderList.add(g);
		this.vertexBuilderList.add(b);
		return this;
	}

	public VertexBuffer color(float r, float g, float b, float a) {
		this.vertexBuilderList.add(r);
		this.vertexBuilderList.add(g);
		this.vertexBuilderList.add(b);
		this.vertexBuilderList.add(a);
		return this;
	}

	public VertexBuffer texture(float u, float v) {
		this.vertexBuilderList.add(u);
		this.vertexBuilderList.add(v);
		return this;
	}

	public VertexBuffer texture(Vector2f uv) {
		this.vertexBuilderList.add(uv.x);
		this.vertexBuilderList.add(uv.y);
		return this;
	}

	public void next() {
		this.vertexBuilderCount += 1;
	}

	public void reset() {
		this.vertexBuilderList.clear();
		this.vertexBuilderCount = 0;
	}
}
