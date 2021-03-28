package com.developerhelperhub.ms.model;

import lombok.Data;

@Data
public class ApplicationDiskSpace {

	private Long free;

	private Long total;

	private Long threshold;
}
