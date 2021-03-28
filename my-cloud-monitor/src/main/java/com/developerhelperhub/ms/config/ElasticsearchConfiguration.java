package com.developerhelperhub.ms.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

@Configuration
public class ElasticsearchConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchConfiguration.class);

	@Value("${mycloud.elasticsearch.host}")
	private String hostName;

	@Value("${mycloud.elasticsearch.port}")
	private String port;

	@Value("${mycloud.elasticsearch.cluster-name}")
	private String clusterName;

	@Bean
	public Client elasticsearchClient() throws UnknownHostException {

		LOGGER.info("Elasticsearch cluster name: {}", this.clusterName);
		LOGGER.info("Elasticsearch host: {}", this.hostName);
		LOGGER.info("Elasticsearch port: {}", this.port);

		Settings settings = Settings.builder().put("cluster.name", this.clusterName).build();
		TransportClient client = new PreBuiltTransportClient(settings);
		client.addTransportAddress(
				new TransportAddress(InetAddress.getByName(this.hostName), Integer.parseInt(this.port)));
		return client;
	}

	@Bean(name = { "elasticsearchOperations", "elasticsearchTemplate" })
	public ElasticsearchTemplate elasticsearchTemplate() throws UnknownHostException {
		return new ElasticsearchTemplate(elasticsearchClient());
	}
}
