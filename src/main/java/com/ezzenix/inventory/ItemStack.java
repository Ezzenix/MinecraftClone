package com.ezzenix.inventory;

import com.ezzenix.item.Item;

public class ItemStack {
	public Item item;
	public int amount;

	public ItemStack(Item item, int amount) {
		this.item = item;
		this.amount = amount;
	}
}
