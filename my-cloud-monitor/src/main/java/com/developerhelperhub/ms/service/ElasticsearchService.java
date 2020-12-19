package com.developerhelperhub.ms.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
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

import com.developerhelperhub.ms.model.monitor.AccessLogModel;
import com.developerhelperhub.ms.model.monitor.ElastiSearchLogModel;
import com.developerhelperhub.ms.model.monitor.LogMessageModel;
import com.developerhelperhub.ms.model.monitor.AccessLogModel.AccessLogMessageModel;
import com.developerhelperhub.ms.model.monitor.ElastiSearchLogModel.ElastiSearchLogData;

@Service
public class ElasticsearchService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchService.class);

	private final String ELASTIC_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	@Value("${mycloud.elasticsearch.pagsize.min}")
	private String minPageSize;

	@Value("${mycloud.elasticsearch.pagsize.max}")
	private String maxPageSize;

	@Autowired
	private ElasticsearchTemplate template;

	private SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy:hh:mm:ss");
	private SimpleDateFormat formatterElasticSearch = new SimpleDateFormat(ELASTIC_DATE_FORMAT);

	public ElastiSearchLogModel search(String indexName, String type, String searchKey, int page, int size,
			String order, long fromDate, long toDate) {

		NativeSearchQueryBuilder bulider = new NativeSearchQueryBuilder().withIndices(indexName).withTypes("_doc")
				.withSort(buildLogSortByTimestamp(order));

		String strFromDate = formatterElasticSearch.format(new Date(fromDate));
		String strToDate = formatterElasticSearch.format(new Date(toDate));

		LOGGER.debug("Query range ... {} {}", strFromDate, strToDate);

		BoolQueryBuilder qb = QueryBuilders.boolQuery();

		qb.must(QueryBuilders.rangeQuery("@timestamp").gte(strFromDate).lte(strToDate).format(ELASTIC_DATE_FORMAT));

		if (searchKey != null && !searchKey.trim().isEmpty()) {
			qb.must(QueryBuilders.matchQuery("message", searchKey.trim()));
		}

		bulider.withQuery(qb);

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

	public List<LogMessageModel> searchLogs(String applicationId, String search, int size, String order, long fromDate,
			long toDate) {
		String indexName = "my-cloud-logs-" + applicationId + "-application-*";

		ElastiSearchLogModel model = search(indexName, "_doc", search, 0, size, order, fromDate, toDate);
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

	public AccessLogModel searchAccessLogs(String applicationId, String search, int size, String order, String group,
			long fromDate, long toDate) {
		String indexName = "my-cloud-logs-" + applicationId + "-access_log-*";

		ElastiSearchLogModel model = search(indexName, "_doc", search, 0, size, order, fromDate, toDate);

		AccessLogModel logModel = new AccessLogModel();

		List<AccessLogModel.AccessLogMessageModel> messages;

		messages = IntStream.range(0, model.getData().size()).mapToObj(index -> {

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
				String datetime = datetimeSplit[0].replace("[", "");
				log.setDatetime(formatter.parse(datetime));
				log.setDatetimeFormatted(datetime);

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

			} catch (IndexOutOfBoundsException | ParseException e) {

				LOGGER.warn("Search parse access log error: {}", text);

			}

			return log;

		}).filter(d -> d != null).collect(Collectors.toList());

		Map<AccessLogModel.Group, List<AccessLogMessageModel>> grouped = messages.stream()
				.collect(Collectors.groupingBy(x -> {

					AccessLogModel.Group groupKey = new AccessLogModel.Group();

					groupKey.setTimestamp(x.getDatetime().getTime());

					if (group.toLowerCase().equals("hour")) {

						groupKey.setGroupTime(x.getDatetime().getTime() / 3600000);

					} else if (group.toLowerCase().equals("minute")) {

						groupKey.setGroupTime(x.getDatetime().getTime() / 60000);

					} else {

						groupKey.setGroupTime(x.getDatetime().getTime() / 1000);

					}

					return groupKey;
				}));

		Map<Long, AccessLogModel.Metric> metrics = new TreeMap<>();

		for (Map.Entry<AccessLogModel.Group, List<AccessLogMessageModel>> entry : grouped.entrySet()) {

			long groupTime = entry.getKey().getGroupTime();

			AccessLogModel.Metric metric;

			if (metrics.containsKey(groupTime)) {
				metric = metrics.get(groupTime);
			} else {
				metric = new AccessLogModel.Metric();
				metrics.put(groupTime, metric);
			}

			Map<String, Integer> data = new HashMap<>();

			for (AccessLogMessageModel log : entry.getValue()) {

				String method = "other";

				if (log.getRequestMethod().toLowerCase().contains("post")
						|| log.getRequestMethod().toLowerCase().contains("put")
						|| log.getRequestMethod().toLowerCase().contains("get")
						|| log.getRequestMethod().toLowerCase().contains("delete")
						|| log.getRequestMethod().toLowerCase().contains("patch")) {
					method = log.getRequestMethod().toLowerCase();
				}

				String statusKey = "";

				if (log.getStatusCode().toLowerCase().contains("1")) {
					statusKey = "1x";
				} else if (log.getStatusCode().toLowerCase().contains("2")) {
					statusKey = "2x";
				} else if (log.getStatusCode().toLowerCase().contains("3")) {
					statusKey = "3x";
				} else if (log.getStatusCode().toLowerCase().contains("4")) {
					statusKey = "4x";
				} else if (log.getStatusCode().toLowerCase().contains("5")) {
					statusKey = "5x";
				} else {
					statusKey = "x";
				}

				String request = "request";

				if (!data.containsKey(method)) {
					data.put(method, 0);
				}
				data.put(method, data.get(method) + 1);

				if (!data.containsKey(statusKey)) {
					data.put(statusKey, 0);
				}
				data.put(statusKey, data.get(statusKey) + 1);

				if (!data.containsKey(request)) {
					data.put(request, 0);
				}
				data.put(request, data.get(request) + 1);
			}

			SimpleDateFormat formatterTime;

			if (group.toLowerCase().equals("hour")) {
				formatterTime = new SimpleDateFormat("HH:mm");
			} else {
				formatterTime = new SimpleDateFormat("mm:ss");
			}

			metric.setTime(formatterTime.format(new Date(entry.getKey().getTimestamp())));

			if (data.containsKey("post"))
				metric.setMethodPost(data.get("post"));

			if (data.containsKey("put"))
				metric.setMethodPut(data.get("put"));

			if (data.containsKey("get"))
				metric.setMethodGet(data.get("get"));

			if (data.containsKey("delete"))
				metric.setMethodDelete(data.get("delete"));

			if (data.containsKey("patch"))
				metric.setMethodPatch(data.get("patch"));

			if (data.containsKey("other"))
				metric.setMethodOther(data.get("other"));

			if (data.containsKey("request"))
				metric.setRequest(data.get("request"));

			if (data.containsKey("1x"))
				metric.setStatus1x(data.get("1x"));

			if (data.containsKey("2x"))
				metric.setStatus2x(data.get("2x"));

			if (data.containsKey("3x"))
				metric.setStatus3x(data.get("3x"));

			if (data.containsKey("4x"))
				metric.setStatus4x(data.get("4x"));

			if (data.containsKey("5x"))
				metric.setStatus5x(data.get("5x"));

			if (data.containsKey("x"))
				metric.setStatusx(data.get("x"));

			metrics.put(groupTime, metric);

		}

		logModel.setGroup(group);
		logModel.setMatrics(metrics.values());
		logModel.setMessages(messages);

		return logModel;
	}
}
