package com.developerhelperhub.ms.id.controller;

import java.util.Collection;

public interface ItemService {

	public Item addItem(Item item);

	public Item getItem(Long id);

	public Collection<Item> getItems();
}
