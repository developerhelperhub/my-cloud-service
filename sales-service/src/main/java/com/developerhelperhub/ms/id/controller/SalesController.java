package com.developerhelperhub.ms.id.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.developerhelperhub.ms.id.inventory.service.InventoryService;
import com.developerhelperhub.ms.id.inventory.service.Item;

@RestController
@RequestMapping("/sales")
public class SalesController {

	@Autowired
	private InventoryService inventoryService;

	@RequestMapping(value = "/items", method = RequestMethod.GET)
	public Collection<Item> items() {
		return inventoryService.getItems();
	}

	@RequestMapping(value = "/items", method = RequestMethod.POST)
	public Item addItem(@RequestBody Item item) {
		return inventoryService.addItem(item);
	}

	@RequestMapping(value = "/items/{id}", method = RequestMethod.GET)
	public Item getItem(@PathVariable(value = "id") Long id) {
		return inventoryService.getItem(id);
	}
}
