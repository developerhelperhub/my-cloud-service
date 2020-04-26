package com.developerhelperhub.ms.id.inventory.service;

import java.util.Collection;

public interface InventoryService {

	public Item addItem(Item item);

	public Item getItem(Long id);

	public Collection<Item> getItems();
}
