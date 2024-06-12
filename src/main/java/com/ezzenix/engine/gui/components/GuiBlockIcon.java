package com.ezzenix.engine.gui.components;

import com.ezzenix.engine.gui.GuiUtil;
import com.ezzenix.engine.opengl.Mesh;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.game.blocks.BlockType;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class GuiBlockIcon extends GuiComponent {
	//private static Shader FRAME_SHADER = new Shader("gui/frame.vert", "gui/frame.frag");

	public BlockType blockType;

	public GuiBlockIcon() {
		super();
	}

	@Override
	public void render() {

	}

	@Override
	public void rebuild() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			if (this.mesh != null) {
				this.mesh.dispose();
				this.mesh = null;
			}


		}
	}

	private void addVertex(List<Float> vertexList, Vector2f position) {

	}
}
