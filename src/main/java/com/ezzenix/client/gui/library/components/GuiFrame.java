package com.ezzenix.client.gui.library.components;

import com.ezzenix.client.gui.library.GuiUtil;
import com.ezzenix.engine.opengl.Mesh;
import com.ezzenix.engine.opengl.Shader;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class GuiFrame extends GuiComponent {
	private static Shader FRAME_SHADER = new Shader("gui/frame.vert", "gui/frame.frag");

	public Vector3f color = new Vector3f(1f, 1f, 1f);
	public float transparency = 0;

	public GuiFrame() {
		super();
	}

	@Override
	public void render() {
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		FRAME_SHADER.bind();

		this.mesh.render();

		glUseProgram(0);

		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
	}

	@Override
	public void rebuild() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			if (this.mesh != null) {
				this.mesh.dispose();
				this.mesh = null;
			}

			List<Float> vertexList = new ArrayList<>();

			Vector2f position = this.getAbsolutePosition();
			Vector2f size = this.getAbsoluteSize();
			position = this.computeTopLeftCorner(position, size);

			Vector2f vertTopLeft = GuiUtil.toNormalizedDeviceCoordinates(position.x, position.y);
			Vector2f vertBottomLeft = GuiUtil.toNormalizedDeviceCoordinates(position.x, position.y + size.y);
			Vector2f vertBottomRight = GuiUtil.toNormalizedDeviceCoordinates(position.x + size.x, position.y + size.y);
			Vector2f vertTopRight = GuiUtil.toNormalizedDeviceCoordinates(position.x + size.x, position.y);

			addVertex(vertexList, vertTopLeft);
			addVertex(vertexList, vertBottomLeft);
			addVertex(vertexList, vertBottomRight);
			addVertex(vertexList, vertTopRight);
			addVertex(vertexList, vertTopLeft);
			addVertex(vertexList, vertBottomRight);

			FloatBuffer vertexBuffer = Mesh.convertToBuffer(vertexList, stack);

			this.mesh = new Mesh(vertexBuffer, vertexList.size() / 6);

			int stride = 6 * Float.BYTES;
			glVertexAttribPointer(0, 2, GL_FLOAT, false, stride, 0);
			glEnableVertexAttribArray(0);
			glVertexAttribPointer(1, 4, GL_FLOAT, false, stride, 2 * Float.BYTES);
			glEnableVertexAttribArray(1);

			this.mesh.unbind();
		}
	}

	private void addVertex(List<Float> vertexList, Vector2f position) {
		vertexList.add(position.x);
		vertexList.add(position.y);
		vertexList.add(color.x);
		vertexList.add(color.y);
		vertexList.add(color.z);
		vertexList.add(1f - transparency);
	}
}
