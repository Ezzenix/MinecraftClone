package com.ezzenix.gui.screen.handledscreen;

import com.ezzenix.Client;
import com.ezzenix.gui.Color;
import com.ezzenix.gui.Gui;
import com.ezzenix.gui.screen.Screen;
import com.ezzenix.inventory.Inventory;
import com.ezzenix.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HandledScreen extends Screen {
	List<Slot> slots = new ArrayList<>();
	Inventory inventory;
	ItemStack heldStack;

	private int STACK_PADDING = 12;
	private int STACK_DRAW_SIZE = Slot.SIZE_PX - STACK_PADDING * 2;

	private final int SLOT_COLOR = Color.pack(0f, 0f, 0f, 0.5f);
	private final int SLOT_COLOR_HOVERED = Color.pack(0.4f, 0.4f, 0.4f, 0.5f);

	public HandledScreen(Inventory inventory) {
		super("Inventory");

		this.inventory = inventory;

		int x = 335;
		int y = 220;
		int pad = (Slot.SIZE_PX + 4);
		for (int i = 0; i < inventory.size(); i++) {
			this.addSlot(new Slot(i, x, y));
			x += pad;
			if ((i + 1) % 9 == 0) {
				x = 335;
				y += pad;
			}
		}
	}

	public void addSlot(Slot slot) {
		this.slots.add(slot);
	}

	public void render() {
		super.render();

		if (heldStack != null && heldStack.isEmpty()) heldStack = null;

		int mouseX = Client.getMouse().getX();
		int mouseY = Client.getMouse().getY();

		for (Slot slot : this.slots) {
			boolean hovered = slot.isMouseWithin(mouseX, mouseY);

			Gui.drawRect(slot.x, slot.y, slot.size, slot.size, hovered ? SLOT_COLOR_HOVERED : SLOT_COLOR);

			ItemStack stack = this.inventory.getSlot(slot.id);
			if (stack != null) {
				Gui.drawStack(stack, slot.x + STACK_PADDING, slot.y + STACK_PADDING, STACK_DRAW_SIZE);
			}
		}

		if (heldStack != null) {
			Gui.drawStack(heldStack, mouseX - STACK_DRAW_SIZE / 2, mouseY - STACK_DRAW_SIZE / 2, STACK_DRAW_SIZE);
		}
	}

	private Slot getSlotAt(int mouseX, int mouseY) {
		for (Slot slot : this.slots) {
			if (slot.isMouseWithin(mouseX, mouseY)) {
				return slot;
			}
		}
		return null;
	}

	public void mouseDown(int mouseX, int mouseY) {
		Slot slot = getSlotAt(mouseX, mouseY);
		if (slot != null) {
			ItemStack stack = inventory.getSlot(slot.id);

			if (heldStack == null) {
				if (stack != null) {
					heldStack = stack;
					inventory.setSlot(slot.id, null);
				}
			} else {
				if (stack != null && heldStack.isSame(stack)) {
					stack.amount += heldStack.amount;
					heldStack = null;
				} else {
					inventory.setSlot(slot.id, heldStack);
					heldStack = stack;
				}
			}
		}
	}

	public void mouse2Down(int mouseX, int mouseY) {
		Slot slot = getSlotAt(mouseX, mouseY);
		if (slot != null) {
			ItemStack stack = inventory.getSlot(slot.id);

			if (heldStack == null) {
				if (stack != null) {
					if (stack.amount < 2) {
						// cant split if less than 2
						heldStack = stack;
						inventory.setSlot(slot.id, null);
						return;
					}

					int toTake = (int) Math.ceil((float) stack.amount / 2f);
					heldStack = stack.take(toTake);
				}
			} else {
				if (stack == null || heldStack.isSame(stack)) {
					if (stack == null) {
						inventory.setSlot(slot.id, heldStack.take(1));
					} else {
						heldStack.amount -= 1;
						stack.amount += 1;
					}
				} else {
					inventory.setSlot(slot.id, heldStack);
					heldStack = stack;
				}
			}
		}
	}
}
