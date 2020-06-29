package com.developerhelperhub.ms.id.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
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

import com.developerhelperhub.ms.id.model.monitor.AccessLogMessageModel;
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

	public ElastiSearchLogModel search(String indexName, String type, String searchKey, int page, int size,
			String order) {

		NativeSearchQueryBuilder bulider = new NativeSearchQueryBuilder().withIndices(indexName).withTypes("_doc")
				.withSort(buildLogSortByTimestamp(order));

		if (searchKey != null && !searchKey.trim().isEmpty()) {
			bulider.withQuery(QueryBuilders.matchQuery("message", searchKey.trim()));
		}

		return search(bulider, page, size);
	}

	public ElastiSearchLogModel search(NativeSearchQueryBuilder bulider, int page, int size) {
		if (size <= 0) {
			size = Integer.parseInt(minPageSize);
		}
		if (size > Integer.parseInt(maxPageSize)) {
			size = Integer.parseInt(maxPageSize);
		}

		bulider.withPageable(PageRequest.of(page, size));

		SearchQuery query = bulider.build();

		LOGGER.debug("Searching.... indexName: {} type: {} page: {} size: {}", query.getIndices(), query.getTypes(),
				page, size);

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

		ElastiSearchLogModel model = template.query(query, extractor);

		LOGGER.debug("Search result loaded {}!", model.getData().size());

		return model;

	}

	private FieldSortBuilder buildLogSortByTimestamp(String order) {

		FieldSortBuilder sort = new SortBuilders().fieldSort("@timestamp");

		if (order != null && !order.trim().isEmpty()) {

			if (order.toLowerCase().equals("desc")) {
				sort.order(SortOrder.DESC);
			} else {
				sort.order(SortOrder.ASC);
			}

		} else {
			sort.order(SortOrder.ASC);
		}
		return sort;
	}

	public List<LogMessageModel> searchLogs(String applicationId, String search, int size, String order) {
		String indexName = "my-cloud-logs-" + applicationId + "-application-*";

		ElastiSearchLogModel model = search(indexName, "_doc", search, 0, size, order);
		List<LogMessageModel> list;

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

				LOGGER.warn("Search parse log error: {}", text);

			}

			return log;

		}).filter(d -> d != null).collect(Collectors.toList());

		return list;
	}

	public List<AccessLogMessageModel> searchAccessLogs(String applicationId, String search, int size, String order) {
		String indexName = "my-cloud-logs-" + applicationId + "-access_log-*";

		ElastiSearchLogModel model = search(indexName, "_doc", search, 0, size, order);
		List<AccessLogMessageModel> list;

		list = IntStream.range(0, model.getData().size()).mapToObj(index -> {

			AccessLogMessageModel log = new AccessLogMessageModel();
			String text = (String) model.getData().get(index).getData().get("message");

			try {

				// pattern - "%t|%a|%h|%p|%m|%U|%q|%s|%D|%T|%F|%l|%S|%I|%v|%u"

				// %t - Date and time, in Common Log Format
				// %a - Remote IP address
				// %h - Remote host name (or IP address if enableLookups for the connector is
				// false)
				// %p - Local port on which this request was received. See also %{xxx}p below.
				// %m - Request method (GET, POST, etc.)
				// %U - Requested URL path
				// %q - Query string (prepended with a '?' if it exists)
				// %s - HTTP status code of the response
				// %D - Time taken to process the request, in millis
				// %T - Time taken to process the request, in seconds
				// %F - Time taken to commit the response, in millis
				// %l - Remote logical username from identd (always returns '-')
				// %S - User session ID
				// %I - Current request thread name (can compare later with stacktraces)
				// %v - Local server name
				// %u - Remote user that was authenticated (if any), else '-'

				String[] split = text.split("\\|");

				String[] datetimeSplit = split[0].split(" ");
				log.setDatetime(datetimeSplit[0].replace("[", ""));

				log.setRemoteIpAddress(split[1]);
				log.setRemoteHostname(split[2]);
				log.setLocalPort(split[3]);
				log.setRequestMethod(split[4]);
				log.setRequestUrl(split[5]);
				log.setQueryString(split[6]);
				log.setStatusCode(split[7]);
				log.setTimeProcessRequestMills(Float.parseFloat(split[8]));
				log.setTimeProcessRequestSeconds(Float.parseFloat(split[9]));
				log.setTimeCommitResponseMills(Float.parseFloat(split[10]));
				log.setRemoteLogicalUsername(split[11]);
				log.setUserSessionId(split[12]);
				log.setCurrentThreadName(split[13]);
				log.setLocalServerName(split[14]);
				log.setAuthenticatedRemoteUsername(split[15]);

			} catch (IndexOutOfBoundsException e) {

				LOGGER.warn("Search parse access log error: {}", text);

			}

			return log;

		}).filter(d -> d != null).collect(Collectors.toList());

		return list;
	}
}
