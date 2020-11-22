package com.developerhelperhub.ms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import(MyCloudDefaultClientConfiguration.class)
public class MyCloudDefaultCloudServerConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(MyCloudDefaultCloudServerConfiguration.class);

	@Autowired
	private Environment environment;

	@Autowired
	private RestTemplate identityRestTemplate;

	@Bean
	public ConfigClientProperties configClientProperties() {

		ConfigClientProperties client = new ConfigClientProperties(this.environment);
		client.setEnabled(true);

		return client;
	}

	/**
	 * In resources/META-INF, create a file called spring.factories and specify your
	 * custom configuration, as shown in the following example: spring.factories.
	 * 
	 * org.springframework.cloud.bootstrap.BootstrapConfiguration =
	 * com.developerhelperhub.ms.id.config.ConfigServerConfiguration
	 * 
	 * @return
	 */
	@Bean
	public ConfigServicePropertySourceLocator configServicePropertySourceLocator() {

		ConfigClientProperties clientProperties = configClientProperties();

		ConfigServicePropertySourceLocator configServicePropertySourceLocator = new ConfigServicePropertySourceLocator(
				clientProperties);
		configServicePropertySourceLocator.setRestTemplate(this.identityRestTemplate);

		return configServicePropertySourceLocator;
	}
}
