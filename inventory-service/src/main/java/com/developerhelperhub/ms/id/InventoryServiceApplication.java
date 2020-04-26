package com.developerhelperhub.ms.id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.developerhelperhub.ms.id.controller.Item;
import com.developerhelperhub.ms.id.controller.ItemService;

@SpringBootApplication
@EnableDiscoveryClient
public class InventoryServiceApplication implements CommandLineRunner {

	@Autowired
	private ItemService itemService;

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	public void run(String... args) throws Exception {
		itemService.addItem(new Item(1L, "TV", 10));
		itemService.addItem(new Item(2L, "Fridge", 5));
		itemService.addItem(new Item(3L, "AC", 5));
	}
}
