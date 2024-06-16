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
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

public class VertexBuffer {
	private final Usage usage;
	private final Shader shader;
	private int vertexBufferId;
	//private int indexBufferId;
	private int vertexArrayId;

	private int uploadedVertexCount;

	private int vertexCount;
	//private final DynamicByteBuffer byteBuffer;
	private List<Float> vertexList;

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

		this.vertexCount = 0;
		//this.byteBuffer = new DynamicByteBuffer(0);
		this.vertexList = new ArrayList<>();
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
		if (this.uploadedVertexCount == 0)
			throw new IllegalStateException("Attempt to call .draw() with nothing uploaded");

		shader.bind();
		this.bind();
		glDrawArrays(GL_TRIANGLES, 0, this.uploadedVertexCount);
		this.unbind();
	}

	public void upload() {
		if (vertexCount == 0)
			throw new RuntimeException("Attempt to upload VertexBuffer with 0 vertices");

		this.uploadedVertexCount = this.vertexCount;

		this.bind();

		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buffer = stack.mallocFloat(this.vertexList.size());
			for (float v : this.vertexList) {
				buffer.put(v);
			}
			buffer.flip();

			glBindBuffer(GL_ARRAY_BUFFER, this.vertexBufferId);
			glBufferData(GL_ARRAY_BUFFER, buffer, this.usage.id);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
		}

		this.reset();
	}

	public void reset() {
		//this.byteBuffer.clear();
		this.vertexList.clear();
		this.vertexCount = 0;
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
		this.vertexList.add(x);
		this.vertexList.add(y);
		this.vertexList.add(z);
		return this;
	}

	public VertexBuffer vertex(float x, float y) {
		Vector2f ndc = GuiUtil.toNormalizedDeviceCoordinates(x, y);
		this.vertexList.add(ndc.x);
		this.vertexList.add(ndc.y);
		return this;
	}

	public VertexBuffer color(float r, float g, float b) {
		this.vertexList.add(r);
		this.vertexList.add(g);
		this.vertexList.add(b);
		return this;
	}

	public VertexBuffer color(float r, float g, float b, float a) {
		this.vertexList.add(r);
		this.vertexList.add(g);
		this.vertexList.add(b);
		this.vertexList.add(a);
		return this;
	}

	public VertexBuffer texture(float u, float v) {
		this.vertexList.add(u);
		this.vertexList.add(v);
		return this;
	}

	public VertexBuffer texture(Vector2f uv) {
		this.vertexList.add(uv.x);
		this.vertexList.add(uv.y);
		return this;
	}

	public void next() {
		this.vertexCount += 1;
	}
}
