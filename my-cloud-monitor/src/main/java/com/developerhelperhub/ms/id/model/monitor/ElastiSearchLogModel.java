package com.developerhelperhub.ms.id.model.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.document.DocumentField;

import lombok.Data;

@Data
public class ElastiSearchLogModel {

	@Data
	public static class ElastiSearchLogData {
		private String id;
		private String index;
		private String type;
		private Map<String, Object> data;
		private Map<String, DocumentField> fields;
	}

	private List<ElastiSearchLogData> data = new ArrayList<>();
}
