package com.developerhelperhub.ms.id.model;

import lombok.Data;

@Data
public class ApplicationDiskSpace {

	private Long free;

	private Long total;

	private Long threshold;
}
