package com.developerhelperhub.ms.id.model;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document
@Data
public class ApplicationDiskSpace {

	private String status;

	private Long free;

	private Long total;

	private Long threshold;
}
