package com.ezzenix.inventory;

import com.ezzenix.item.Item;

public class ItemStack {
	public Item item;
	public int amount;

	public ItemStack(Item item, int amount) {
		this.item = item;
		this.amount = amount;
	}

	public boolean isSame(ItemStack v) {
		return this.item == v.item;
	}

	@Override
	public ItemStack clone() {
		return new ItemStack(this.item, this.amount);
	}

	/*
	Removes amount from stack and returns new stack with amount and same item
	 */
	public ItemStack take(int amount) {
		amount = Math.min(amount, this.amount);
		this.amount -= amount;
		return new ItemStack(this.item, amount);
	}

	public boolean isEmpty() {
		return this.amount < 1;
	}
}
