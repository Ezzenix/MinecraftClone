package com.ezzenix.inventory;

import com.ezzenix.item.Items;

public class Inventory {
	private final ItemStack[] slots;

	public Inventory(int size) {
		this.slots = new ItemStack[size];

		setSlot(0, new ItemStack(Items.GRASS_BLOCK, 10));
		setSlot(1, new ItemStack(Items.STONE, 5));
		setSlot(2, new ItemStack(Items.DIRT, 2));
		setSlot(3, new ItemStack(Items.SAND, 1));
	}

	public ItemStack getSlot(int slot) {
		return this.slots[slot];
	}

	public void setSlot(int slot, ItemStack itemStack) {
		this.slots[slot] = itemStack;
	}
}
