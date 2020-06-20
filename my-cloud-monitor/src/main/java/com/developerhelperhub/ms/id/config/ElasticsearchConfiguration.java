package com.developerhelperhub.ms.id.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

@Configuration
public class ElasticsearchConfiguration {

	@Bean
	public Client elasticsearchClient() throws UnknownHostException {
		Settings settings = Settings.builder().put("cluster.name", "docker-cluster").build();
		TransportClient client = new PreBuiltTransportClient(settings);
		client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
		return client;
	}

	@Bean(name = { "elasticsearchOperations", "elasticsearchTemplate" })
	public ElasticsearchTemplate elasticsearchTemplate() throws UnknownHostException {
		return new ElasticsearchTemplate(elasticsearchClient());
	}
}
