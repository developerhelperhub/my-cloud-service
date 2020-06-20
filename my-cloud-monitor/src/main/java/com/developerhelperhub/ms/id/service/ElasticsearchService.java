package com.developerhelperhub.ms.id.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchService.class);

	@Autowired
	private ElasticsearchTemplate template;

	public ElastiSearchLogModel search(String indexName, String type, int page, int size) {
		if (size == 0) {
			size = 100;
		}

		LOGGER.debug("Searching.... indexName: {} type: {} page: {} size: {}", indexName, type, page, size);

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

		LOGGER.debug("Searching.... indexName: {} type: {} page: {} size: {}", indexName, type, page, size);

		ElastiSearchLogModel model = template.query(query, extractor);

		LOGGER.debug("Search result loaded {}!", model.getData().size());

		return model;

	}

	public List<LogMessageModel> searchLogs(String applicationId, int size) {
		String indexName = "my-cloud-logs-" + applicationId + "-*";
		ElastiSearchLogModel model = search(indexName, "_doc", 0, size);
		List<LogMessageModel> list;

		list = model.getData().stream().map(data -> {
			LogMessageModel log = new LogMessageModel();
			String text = (String) data.getData().get("message");

			try {

				String[] split = text.split("\\|");
				log.setDatetime(split[0]);
				log.setThreadName(split[1]);
				log.setLogLevel(split[2]);
				log.setClassName(split[3]);
				log.setMessage(split[4]);

			} catch (IndexOutOfBoundsException e) {
				LOGGER.warn("Log message parse issue found!");
			}

			return log;
		}).collect(Collectors.toList());

		return list;
	}
}
