package com.developerhelperhub.ms.id.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/admin")
public class InventoryAdminController {

	@Autowired
	private ItemService itemService;

	@RequestMapping(value = "/items", method = RequestMethod.GET)
	public Collection<Item> items() {
		return itemService.getItems();
	}

	@RequestMapping(value = "/items", method = RequestMethod.POST)
	public Item addItem(@RequestBody Item item) {
		return itemService.addItem(item);
	}

	@RequestMapping(value = "/items/{id}", method = RequestMethod.GET)
	public Item getItem(@PathVariable(value = "id") Long id) {
		return itemService.getItem(id);
	}
}
