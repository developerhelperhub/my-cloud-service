package com.developerhelperhub.ms.model;

import java.util.List;

import lombok.Data;

@Data
public class ApplicationMonitorModel {

	@Data
	public static class Build {
		private String version;
		private String artifact;
		private String name;
		private String group;
		private String time;

	}

	@Data
	public static class Matric implements Comparable<Matric> {

		private long timestamp;
		private String time;
		private long value;

		@Override
		public int compareTo(Matric o) {
			return (int) (this.timestamp - o.timestamp);
		}

	}

	@Data
	public static class MatricGroup implements Comparable<MatricGroup> {

		private int order;
		private String name;
		private String display;
		private List<Matric> matrics;

		public int compareTo(MatricGroup o) {
			return this.order - o.order;
		};
	}

	private String name;

	private String status;

	private ApplicationDiskSpace diskSpace;

	private String lastUpdated;

	private List<DiscoveryResponseModel.Instance> instance;

	private Build build;

	private List<MatricGroup> memory;
	
	private List<MatricGroup> buffer;
	
	private List<MatricGroup> thread;

}
