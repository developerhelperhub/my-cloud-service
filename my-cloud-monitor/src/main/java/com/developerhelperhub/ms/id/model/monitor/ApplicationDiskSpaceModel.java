package com.developerhelperhub.ms.id.model.monitor;

import java.util.List;

import lombok.Data;

@Data
public class ApplicationDiskSpaceModel {

	@Data
	public static class DataModel {

		private String time;
		private Long value;

	}

	private List<DataModel> free;

	private List<DataModel> total;

	private List<DataModel> threshold;

}
