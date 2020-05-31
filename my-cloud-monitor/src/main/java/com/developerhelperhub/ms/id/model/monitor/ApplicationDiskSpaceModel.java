package com.developerhelperhub.ms.id.model.monitor;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document
@Data
public class ApplicationDiskSpaceModel {

	private String time;

	private Long free;

	private Long total;

	private Long threshold;
}
