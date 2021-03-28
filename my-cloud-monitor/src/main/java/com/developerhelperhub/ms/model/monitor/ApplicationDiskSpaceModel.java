package com.developerhelperhub.ms.model.monitor;

import java.util.List;

import com.developerhelperhub.ms.model.ApplicationDiskSpace;

import lombok.Data;

@Data
public class ApplicationDiskSpaceModel {

	@Data
	public static class DataModel {

		private String time;
		private Long value;

	}

	private ApplicationDiskSpace data;

	private List<DataModel> free;

	private List<DataModel> total;

	private List<DataModel> threshold;

}
