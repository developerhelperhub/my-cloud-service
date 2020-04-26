package com.developerhelperhub.ms.id.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ItemServiceImpl implements ItemService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ItemServiceImpl.class);

	private Map<Long, Item> items = new HashMap<Long, Item>();

	public Item addItem(Item item) {

		LOGGER.debug("Added item : {}", item);

		return items.put(item.getId(), item);
	}

	public Item getItem(Long id) {

		if (!items.containsKey(id)) {

			LOGGER.debug("Item not found by {}", id);

			throw new RuntimeException(id + " item not found!");
		}

		LOGGER.debug("Get item by {}", id);

		return items.get(id);
	}

	public Collection<Item> getItems() {

		LOGGER.debug("{} items found", items.size());

		return items.values();
	}

}
