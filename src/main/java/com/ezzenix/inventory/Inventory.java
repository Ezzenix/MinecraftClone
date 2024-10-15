package com.ezzenix.inventory;

public class Inventory {
	private final ItemStack[] slots;

	public Inventory(int size) {
		this.slots = new ItemStack[size];
	}

	public ItemStack getSlot(int slot) {
		return this.slots[slot];
	}

	public void setSlot(int slot, ItemStack itemStack) {
		this.slots[slot] = itemStack;
	}

	public int size() {
		return this.slots.length;
	}
}
