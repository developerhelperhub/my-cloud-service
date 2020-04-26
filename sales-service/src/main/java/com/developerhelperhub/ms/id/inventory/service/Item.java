package com.developerhelperhub.ms.id.inventory.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {

	private Long id;
	private String name;
	private int quantity;

	public Item() {
	}

	public Item(Long id, String name, int quantity) {
		super();
		this.id = id;
		this.name = name;
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", quantity=" + quantity + "]";
	}

}
