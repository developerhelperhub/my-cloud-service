package com.developerhelperhub.ms.id.model.actuator;

import java.util.List;

import lombok.Data;

@Data
public class ActuatorMetricModel {

	@Data
	public static class Measurement {
		public String statistic;
		public double value;
	}

	@Data
	public static class AvailableTag {
		public String tag;
		public List<String> values;
	}

	public String name;
	public String description;
	public String baseUnit;
	public List<Measurement> measurements;
	public List<AvailableTag> availableTags;

}
