package com.ezzenix.client.gui.library.components;

import com.ezzenix.client.Client;
import com.ezzenix.client.gui.library.Gui;
import com.ezzenix.client.gui.screen.Screen;
import com.ezzenix.client.gui.library.UDim2;
import com.ezzenix.engine.opengl.Mesh;
import org.joml.Vector2f;

public class GuiComponent {
	public UDim2 size = UDim2.fromOffset(200, 100);
	public UDim2 position = new UDim2();
	public Vector2f anchorPoint = new Vector2f();
	public Gui.SizeConstraint sizeConstraint = Gui.SizeConstraint.XY;
	public GuiComponent adornee = null;
	public Screen screen = null;
	public boolean isStandalone = false;


	protected Mesh mesh;
	public boolean shouldRebuild = true;

	public GuiComponent() {
		Gui.registerComponent(this);
	}

	public void render() {
	}

	public void rebuild() {

	}

	public void standalone() {
		this.isStandalone = true;
		this.rebuild();
	}

	public Vector2f getAdorneeInset() {
		if (this.adornee == null) {
			return new Vector2f(0, 0);
		} else {
			return this.adornee.computeTopLeftCorner(this.adornee.getAbsolutePosition(), this.adornee.getAbsoluteSize());
		}
	}

	public Vector2f getAdorneeSize() {
		if (this.adornee == null) {
			return new Vector2f(Client.getWindow().getWidth(), Client.getWindow().getHeight());
		} else {
			return this.adornee.getAbsoluteSize();
		}
	}

	public Vector2f getAbsolutePosition() {
		Vector2f adorneeSize = getAdorneeSize();

		float x = this.position.scaleX * adorneeSize.x + this.position.offsetX;
		float y = this.position.scaleY * adorneeSize.y + this.position.offsetY;

		return new Vector2f(x, y);
	}

	public boolean isPointWithin(int x, int y) {
		Vector2f absoluteSize = this.getAbsoluteSize();
		Vector2f absolutePosition = this.getAbsolutePosition();
		Vector2f topLeftCorner = this.computeTopLeftCorner(absolutePosition, absoluteSize);

		return x >= topLeftCorner.x && x <= topLeftCorner.x + absoluteSize.x
			&& y >= topLeftCorner.y && y <= topLeftCorner.y + absoluteSize.y;
	}

	public void mouseButton1Activated(int x, int y) {
	}

	public Vector2f getAbsoluteSize() {
		Vector2f adorneeSize = getAdorneeSize();

		float computedX = switch (this.sizeConstraint) {
			case Gui.SizeConstraint.XY, Gui.SizeConstraint.XX -> this.size.scaleX * adorneeSize.x;
			case Gui.SizeConstraint.YY -> this.size.scaleX * adorneeSize.y;
		};

		float computedY = switch (this.sizeConstraint) {
			case Gui.SizeConstraint.XY, Gui.SizeConstraint.YY -> this.size.scaleY * adorneeSize.y;
			case Gui.SizeConstraint.XX -> this.size.scaleY * adorneeSize.x;
		};

		float x = computedX + this.size.offsetX;
		float y = computedY + this.size.offsetY;

		return new Vector2f(x, y);
	}

	public Vector2f computeTopLeftCorner(Vector2f absolutePosition, Vector2f absoluteSize) {
		float x = absolutePosition.x - absoluteSize.x * this.anchorPoint.x;
		float y = absolutePosition.y - absoluteSize.y * this.anchorPoint.y;
		return this.getAdorneeInset().add(x, y);
	}

	public void removed() {
	}

	public void dispose() {
		this.removed();
		Gui.unregisterComponent(this);
		if (this.mesh != null) {
			this.mesh.dispose();
			this.mesh = null;
		}
	}
}
