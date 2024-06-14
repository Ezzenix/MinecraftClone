package com.ezzenix.client.gui.library.components;

import com.ezzenix.client.gui.library.GuiUtil;
import com.ezzenix.engine.opengl.Mesh;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.engine.opengl.Texture;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import java.io.File;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class GuiImage extends GuiComponent {
	private static Shader IMAGE_SHADER = new Shader("gui/image.vert", "gui/image.frag");

	public Texture texture;
	public Vector2f[] uvCoords = new Vector2f[]{
		new Vector2f(0, 0),
		new Vector2f(0, 1),
		new Vector2f(1, 1),
		new Vector2f(1, 0)
	};

	public GuiImage() {
		super();

		try {
			this.texture = new Texture(ImageIO.read(new File("src/main/resources/icon.png")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void render() {
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		texture.bind();
		IMAGE_SHADER.bind();

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

			addVertex(vertexList, vertTopLeft, uvCoords[0]);
			addVertex(vertexList, vertBottomLeft, uvCoords[1]);
			addVertex(vertexList, vertBottomRight, uvCoords[2]);
			addVertex(vertexList, vertTopRight, uvCoords[3]);
			addVertex(vertexList, vertTopLeft, uvCoords[0]);
			addVertex(vertexList, vertBottomRight, uvCoords[2]);

			FloatBuffer vertexBuffer = Mesh.convertToBuffer(vertexList, stack);

			this.mesh = new Mesh(vertexBuffer, vertexList.size() / 4);

			int stride = 4 * Float.BYTES;
			glVertexAttribPointer(0, 2, GL_FLOAT, false, stride, 0);
			glEnableVertexAttribArray(0);
			glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 2 * Float.BYTES);
			glEnableVertexAttribArray(1);

			this.mesh.unbind();
		}
	}

	private void addVertex(List<Float> vertexList, Vector2f position, Vector2f uvCoord) {
		vertexList.add(position.x);
		vertexList.add(position.y);
		vertexList.add(uvCoord.x);
		vertexList.add(uvCoord.y);
	}
}
