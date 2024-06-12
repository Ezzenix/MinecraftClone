package com.ezzenix.engine.gui.components;

import com.ezzenix.engine.gui.FontRenderer;
import com.ezzenix.engine.gui.GuiUtil;
import com.ezzenix.engine.gui.UDim2;
import com.ezzenix.engine.opengl.Mesh;
import com.ezzenix.engine.opengl.Shader;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class GuiText extends GuiComponent {
	private static final int BASE_FONT_SIZE = 18;
	private static final FontRenderer FONT_RENDERER = new FontRenderer(new Font("Arial", Font.PLAIN, BASE_FONT_SIZE));
	private static Shader TEXT_SHADER = new Shader("gui/text.vert", "gui/text.frag");

	public String text = "Placeholder";
	public int fontSize = 18;
	public Vector3f color = new Vector3f(1f, 1f, 1f);
	public boolean textCentered = false;
	public boolean textScaled = false;

	public GuiText() {
		super();
	}

	@Override
	public void render() {
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		FONT_RENDERER.getAtlasTexture().bind();
		TEXT_SHADER.bind();

		this.mesh.render();

		glUseProgram(0);

		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
	}

	@Override
	public void rebuild() {
		Vector2f position = this.getAbsolutePosition();
		Vector2f size = this.getAbsoluteSize();
		position = this.computeTopLeftCorner(position, size);

		Vector2f textBounds = FONT_RENDERER.getTextBounds(text, this.fontSize);
		float fontSize = this.fontSize;

		if (textScaled) {
			fontSize *= (size.y / textBounds.y);
			textBounds = FONT_RENDERER.getTextBounds(text, (int) fontSize);
		}

		if (textCentered) {
			position.add(size.x / 2 - textBounds.x / 2, size.y / 2 - textBounds.y / 2);
		}

		float TEXT_SCALE = fontSize / BASE_FONT_SIZE;

		try (MemoryStack stack = MemoryStack.stackPush()) {
			if (this.mesh != null) {
				this.mesh.dispose();
				this.mesh = null;
			}

			List<Float> vertexList = new ArrayList<>();

			int offsetX = 0;
			for (int i = 0; i < text.length(); i++) {
				char c = text.charAt(i);

				FontRenderer.Glyph glyph = FONT_RENDERER.getGlyph(c);
				if (glyph == null) continue;

				Vector2f[] uvCoords = glyph.uvCoords;
				Vector2f vertTopLeft = GuiUtil.toNormalizedDeviceCoordinates(offsetX + position.x, position.y);
				Vector2f vertBottomLeft = GuiUtil.toNormalizedDeviceCoordinates(offsetX + position.x, position.y + glyph.height * TEXT_SCALE);
				Vector2f vertBottomRight = GuiUtil.toNormalizedDeviceCoordinates(offsetX + position.x + glyph.width * TEXT_SCALE, position.y + glyph.height * TEXT_SCALE);
				Vector2f vertTopRight = GuiUtil.toNormalizedDeviceCoordinates(offsetX + position.x + glyph.width * TEXT_SCALE, position.y);

				addVertex(vertexList, vertTopLeft, uvCoords[1]);
				addVertex(vertexList, vertBottomLeft, uvCoords[0]);
				addVertex(vertexList, vertBottomRight, uvCoords[3]);
				addVertex(vertexList, vertTopRight, uvCoords[2]);
				addVertex(vertexList, vertTopLeft, uvCoords[1]);
				addVertex(vertexList, vertBottomRight, uvCoords[3]);

				offsetX += (int) (glyph.width * TEXT_SCALE);
			}

			FloatBuffer vertexBuffer = Mesh.convertToBuffer(vertexList, stack);

			this.mesh = new Mesh(vertexBuffer, vertexList.size() / 4);

			int stride = 7 * Float.BYTES;
			glVertexAttribPointer(0, 2, GL_FLOAT, false, stride, 0);
			glEnableVertexAttribArray(0);
			glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 2 * Float.BYTES);
			glEnableVertexAttribArray(1);
			glVertexAttribPointer(2, 3, GL_FLOAT, false, stride, 4 * Float.BYTES);
			glEnableVertexAttribArray(2);

			this.mesh.unbind();
		}
	}

	private void addVertex(List<Float> vertexList, Vector2f position, Vector2f uvCoord) {
		vertexList.add(position.x);
		vertexList.add(position.y);
		vertexList.add(uvCoord.x);
		vertexList.add(uvCoord.y);
		vertexList.add(color.x);
		vertexList.add(color.y);
		vertexList.add(color.z);
	}
}
