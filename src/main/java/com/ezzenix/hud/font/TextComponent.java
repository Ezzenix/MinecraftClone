package com.ezzenix.hud.font;

import com.ezzenix.Game;
import com.ezzenix.engine.gui.FontRenderer;
import com.ezzenix.engine.gui.GuiUtil;
import com.ezzenix.engine.opengl.Mesh;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class TextComponent {
	FontRenderer fontRenderer;
	String text;
	int x, y;
	Mesh mesh;

	public TextComponent(FontRenderer fontRenderer, String text, int x, int y) {
		this.fontRenderer = fontRenderer;
		this.x = x;
		this.y = y;
		this.setText(text);
	}

	public void setText(String text) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			if (this.text != null && this.text.equals(text)) return;
			this.text = text;

			if (this.mesh != null) {
				this.mesh.dispose();
				this.mesh = null;
			}

			List<Float> vertexList = new ArrayList<>();

			int offsetX = 0;
			for (int i = 0; i < text.length(); i++) {
				char c = text.charAt(i);

				FontRenderer.Glyph glyph = this.fontRenderer.getGlyph(c);
				if (glyph == null) continue;

				Vector2f[] uvCoords = glyph.uvCoords;
				Vector2f vertTopLeft = GuiUtil.toNormalizedDeviceCoordinates(offsetX + this.x, this.y);
				Vector2f vertBottomLeft = GuiUtil.toNormalizedDeviceCoordinates(offsetX + this.x, this.y + glyph.height);
				Vector2f vertBottomRight = GuiUtil.toNormalizedDeviceCoordinates(offsetX + this.x + glyph.width, this.y + glyph.height);
				Vector2f vertTopRight = GuiUtil.toNormalizedDeviceCoordinates(offsetX + this.x + glyph.width, this.y);

				addVertex(vertexList, vertTopLeft, uvCoords[1]);
				addVertex(vertexList, vertBottomLeft, uvCoords[0]);
				addVertex(vertexList, vertBottomRight, uvCoords[3]);
				addVertex(vertexList, vertTopRight, uvCoords[2]);
				addVertex(vertexList, vertTopLeft, uvCoords[1]);
				addVertex(vertexList, vertBottomRight, uvCoords[3]);

				offsetX += glyph.width;
			}

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

	void addVertex(List<Float> vertexList, Vector2f position, Vector2f uvCoord) {
		vertexList.add(position.x);
		vertexList.add(position.y);
		vertexList.add(uvCoord.x);
		vertexList.add(uvCoord.y);
	}

	public void render() {
		if (this.mesh == null) return;

		//glBindTexture(GL_TEXTURE_2D, this.fontRenderer.getAtlasTextureId());
		this.mesh.render();
	}
}
