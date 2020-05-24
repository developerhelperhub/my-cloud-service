package com.developerhelperhub.ms.id.service.info;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
public class ApplicationInfo {

	@Data
	@Document
	public static class Build {
		public String version;
		public String artifact;
		public String name;
		public String group;
		public Date time;
	}

	public Build build;
}
