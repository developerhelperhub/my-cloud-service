package com.developerhelperhub.ms.id.inventory.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
public class InventoryServiceImpl implements InventoryService {

	private static final Logger LOGGER = LoggerFactory.getLogger(InventoryServiceImpl.class);

//	@Autowired
//	private OAuth2RestTemplate restTemplate;

	@Autowired
	private RestTemplate restTemplate;

	@HystrixCommand(fallbackMethod = "defaultAddItem")
	public Item addItem(Item item) {

		ParameterizedTypeReference<Item> reference = new ParameterizedTypeReference<Item>() {
		};

		HttpEntity<Item> headerEntity = new HttpEntity<Item>(item);

		ResponseEntity<Item> entity = restTemplate.exchange("http://inventory-service/admin/items", HttpMethod.POST,
				headerEntity, reference);

		LOGGER.debug("Added item : {}", entity.getBody());

		return entity.getBody();
	}

	public Item defaultAddItem(Item item) {
		return new Item(0L, "Default Item", 0);
	}

	@HystrixCommand(fallbackMethod = "defaultGetItem")
	public Item getItem(Long id) {

		ParameterizedTypeReference<Item> reference = new ParameterizedTypeReference<Item>() {
		};

		ResponseEntity<Item> entity = restTemplate.exchange("http://inventory-service/admin/items/" + id,
				HttpMethod.GET, null, reference);

		LOGGER.debug("Get item by {}", id);

		return entity.getBody();
	}

	public Item defaultGetItem(Long id) {
		return new Item(0L, "Default Item", 0);
	}

	@HystrixCommand(fallbackMethod = "defaultGetItems")
	public Collection<Item> getItems() {

		ParameterizedTypeReference<List<Item>> reference = new ParameterizedTypeReference<List<Item>>() {
		};

		ResponseEntity<List<Item>> entity = restTemplate.exchange("http://inventory-service/admin/items",
				HttpMethod.GET, null, reference);

		LOGGER.debug("{} items found", entity.getBody().size());

		return entity.getBody();
	}

	public Collection<Item> defaultGetItems() {
		List<Item> items = new ArrayList<Item>();
		items.add(new Item(0L, "Default Item", 0));
		return items;
	}

}
