package com.developerhelperhub.ms.id.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.developerhelperhub.ms.id.model.monitor.ElastiSearchLogModel;
import com.developerhelperhub.ms.id.model.monitor.ElastiSearchLogModel.ElastiSearchLogData;
import com.developerhelperhub.ms.id.model.monitor.LogMessageModel;

@Service
public class ElasticsearchService {

	@Autowired
	private ElasticsearchTemplate template;

	public ElastiSearchLogModel search(String indexName, String type, int page, int size) {
		if (size == 0) {
			size = 100;
		}

		SearchQuery query = new NativeSearchQueryBuilder().withIndices(indexName).withTypes(type)
				.withSort(new SortBuilders().fieldSort("@timestamp").order(SortOrder.DESC))
				.withPageable(PageRequest.of(page, size)).build();

		ResultsExtractor<ElastiSearchLogModel> extractor = new ResultsExtractor<ElastiSearchLogModel>() {

			@Override
			public ElastiSearchLogModel extract(SearchResponse response) {

				ElastiSearchLogModel model = new ElastiSearchLogModel();

				SearchHit[] results = response.getHits().getHits();

				for (SearchHit hit : results) {
					ElastiSearchLogData data = new ElastiSearchLogData();
					data.setId(hit.getId());
					data.setIndex(hit.getIndex());
					data.setType(hit.getType());
					data.setData(hit.getSourceAsMap());
					data.setFields(hit.getFields());

					model.getData().add(data);
				}

				return model;
			}
		};

		return template.query(query, extractor);

	}

	public List<LogMessageModel> searchLogs(String applicationId, int size) {
		String indexName = "my-cloud-logs-" + applicationId + "-*";
		ElastiSearchLogModel model = search(indexName, "_doc", 0, size);
		List<LogMessageModel> list;

		list = model.getData().stream().map(data -> {
			LogMessageModel log = new LogMessageModel();
			log.setMessage((String) data.getData().get("message"));
			return log;
		}).collect(Collectors.toList());

		return list;
	}
}
