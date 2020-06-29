package com.developerhelperhub.ms.id.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${elasticsearch.pagsize.min}")
	private String minPageSize;

	@Value("${elasticsearch.pagsize.max}")
	private String maxPageSize;

	@Autowired
	private ElasticsearchTemplate template;

	public ElastiSearchLogModel search(String indexName, String type, String search, int page, int size) {
		if (size <= 0) {
			size = Integer.parseInt(minPageSize);
		}
		if (size > Integer.parseInt(maxPageSize)) {
			size = Integer.parseInt(maxPageSize);
		}

		LOGGER.debug("Searching.... indexName: {} type: {} page: {} size: {}", indexName, type, page, size);

		NativeSearchQueryBuilder bulider = new NativeSearchQueryBuilder().withIndices(indexName).withTypes(type)
				.withSort(new SortBuilders().fieldSort("@timestamp").order(SortOrder.ASC))
				.withPageable(PageRequest.of(page, size));

		if (search != null && !search.trim().isEmpty()) {
			bulider.withQuery(QueryBuilders.matchQuery("message", search.trim()));
		}

		SearchQuery query = bulider.build();

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

	public List<LogMessageModel> searchLogs(String applicationId, String search, int size) {
		String indexName = "my-cloud-logs-" + applicationId + "-application-*";
		ElastiSearchLogModel model = search(indexName, "_doc", search, 0, size);
		List<LogMessageModel> list;

		List<Integer> errorIndexs = new ArrayList<Integer>();

		list = IntStream.range(0, model.getData().size()).mapToObj(index -> {

			LogMessageModel log = new LogMessageModel();
			String text = (String) model.getData().get(index).getData().get("message");

			try {

				String[] split = text.split("\\|");
				log.setDatetime(split[0]);
				log.setThreadName(split[1]);
				log.setLogLevel(split[2]);
				log.setClassName(split[3]);
				log.setMessage(split[4]);

			} catch (IndexOutOfBoundsException e) {
				errorIndexs.add(index);
			}

			return log;

		}).filter(d -> d != null).collect(Collectors.toList());

//		if (errorIndexs.size() > 0) {
//
//			LOGGER.warn("Error index found {}!", errorIndexs.size());
//
//			List<LogMessageModel> removes = new ArrayList<>();
//
//			Map<Integer, String> mapping = new HashMap<>();
//
//			int index = errorIndexs.get(0);
//			int errorFor = index;
//			mapping.put(errorFor, "");
//
//			for (int errorIndex : errorIndexs) {
//				String message = "";
//
//				removes.add(list.get(errorIndex));
//
//				if (errorIndex == index) {
//
//					message = mapping.get(errorFor);
//
//					message = message + model.getData().get(errorIndex).getData().get("message") + "\n";
//
//					index++;
//
//				} else {
//
//					message = model.getData().get(errorIndex).getData().get("message") + "\n";
//
//					index = errorIndex + 1;
//					errorFor = errorIndex;
//
//				}
//
//				mapping.put(errorFor, message);
//			}
//
//			for (Map.Entry<Integer, String> entry : mapping.entrySet()) {
//
//				int errorIndex = entry.getKey() - 1;
//				if (errorIndex < 0) {
//					errorIndex = 0;
//				}
//
//				LogMessageModel message = list.get(errorIndex);
//
//				message.setMessage(message.getMessage() + "\n" + entry.getValue());
//			}
//
//			list.removeAll(removes);
//		}

		return list;
	}
}
