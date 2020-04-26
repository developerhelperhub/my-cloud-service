# Spring Boot 2.2.5 Cloud Circuit Breaker and Load Balancer

This repository contains the spring boot cloud Circuit breaker and load balancer implementation. This example is continuation of the [Cloud Zuul API Gateway](https://github.com/developerhelperhub/spring-boot2-cloud-netflix-api-gateway) example. I would suggest, please look previous implementation before looking this source code.

This repository contains seven maven project. 
* my-cloud-service: Its main module, it contains the dependecy management of our application.
* my-cloud-discovery-service: This is the server for the discovery service.
* identity-service: This authentication server service. 
* client-application-service: This client application for authentication server.
* inventory-service: This is one of the microservice which is called inventory service to manage the inventory in the project.
* sales-service: This is one of the microservice which is called sales service to manage the point of sales in the project.
* my-cloud-circuit-breaker: This is one of the microservice which is used to view hystrix dashboard


In this example, I am using the use case is pull the data from ```inventory-service``` to ```sales-service``` through rest API. In this case, we face the cascade failures if the ```inventory-service``` failed. As per my understanding, in this use case, we can handle two kind of approach, when we call the rest API from once service to another service, one is ```retry mechanism``` and another one is ```circuit breaker```. 

In this example, I am using circuit breaker implementation because I am assuming that, if the ```inventory-service``` service is failing more than one minute, my client should not face communication failures and ```sales-serivce``` should handle this issue accordingly and send the correct response back to the client. 

In this cases ```inventory-service``` failied, the circuit breaker will open, if the failure is reached the partiular threshold, and redirect the API call into callback method in the ```seales-service```. Once ```inventory-service``` is working fine, then circuit breaker will close and the API communication will start work.

We need to implement the API communication from ```inventory-service``` to ```sales-service``` before jumping into the cuircute breaker code implementation. 

### Code changes in the inventory-service.
We need to create the respective API endpoints in the service which are required to call the APIs from the ```sales-service```. 

I created one service for managing the items in the service. We need to create the model class and service class for item service.

```Item``` model class code:
```java
package com.developerhelperhub.ms.id.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {

	private Long id;
	private String name;
	private int quantity;

	public Item() {
	}

	public Item(Long id, String name, int quantity) {
		super();
		this.id = id;
		this.name = name;
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", quantity=" + quantity + "]";
	}
}
```

```ItemService``` model interface code:
```java
package com.developerhelperhub.ms.id.controller;

import java.util.Collection;

public interface ItemService {

	public Item addItem(Item item);

	public Item getItem(Long id);

	public Collection<Item> getItems();
}
```

```ItemServiceImpl``` implementation of item service:
```java
package com.developerhelperhub.ms.id.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ItemServiceImpl implements ItemService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ItemServiceImpl.class);

	private Map<Long, Item> items = new HashMap<Long, Item>();

	public Item addItem(Item item) {

		LOGGER.debug("Added item : {}", item);

		return items.put(item.getId(), item);
	}

	public Item getItem(Long id) {

		if (!items.containsKey(id)) {

			LOGGER.debug("Item not found by {}", id);

			throw new RuntimeException(id + " item not found!");
		}

		LOGGER.debug("Get item by {}", id);

		return items.get(id);
	}

	public Collection<Item> getItems() {

		LOGGER.debug("{} items found", items.size());

		return items.values();
	}
}
```

I added the default items in the service while loading this service. This code in ```InventoryServiceApplication```:
```java
package com.developerhelperhub.ms.id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.developerhelperhub.ms.id.controller.Item;
import com.developerhelperhub.ms.id.controller.ItemService;

@SpringBootApplication
@EnableDiscoveryClient
public class InventoryServiceApplication implements CommandLineRunner {

	@Autowired
	private ItemService itemService;

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	public void run(String... args) throws Exception {
		itemService.addItem(new Item(1L, "TV", 10));
		itemService.addItem(new Item(2L, "Fridge", 5));
		itemService.addItem(new Item(3L, "AC", 5));
	}
}
```

I created two controller, one is for ```InventoryUserController``` and another one is for ```InventoryAdminController```, these controller have its on base path patterns to differentiate it. The ```/user/**``` path pattern is using for user endpoints and ```/admin/***``` path pattern is using for admin endpoints.

The ```InventoryUserController``` is providing the endpoints for the clients to add the items, get the items and list the items. These endpoints are used for accessing the logged users who have the role is "ROLE_USER". The code in the ```InventoryUserController```:
```java
package com.developerhelperhub.ms.id.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/user")
public class InventoryUserController {

	@Autowired
	private ItemService itemService;

	@RequestMapping(value = "/items", method = RequestMethod.GET)
	public Collection<Item> items() {
		return itemService.getItems();
	}

	@RequestMapping(value = "/items", method = RequestMethod.POST)
	public Item addItem(@RequestBody Item item) {
		return itemService.addItem(item);
	}

	@RequestMapping(value = "/items/{id}", method = RequestMethod.GET)
	public Item getItem(@PathVariable(value = "id") Long id) {
		return itemService.getItem(id);
	}
}
```

The ```InventoryAdminController``` is providing the endpoints for the ```other services``` who are accessing internally, in this case, this ```InventoryAdminController``` APIs are using the ```sales-service``` to share the information between ```inventory-service``` and ```sales-service```.  

I am using the security for this ```Oauth2 grant type security client_credentials``` because I don't want the user information share between these services. In this secruity, we can use direct client credentials to generate the token and access the endpoints and I am providing the security scope ```ADMIN```, which means, these APIs can access only who are the clients should be thier secuirity scope should be ```ADMIN```.

The code in the ```InventoryAdminController```:
```java
package com.developerhelperhub.ms.id.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/admin")
public class InventoryAdminController {

	@Autowired
	private ItemService itemService;

	@RequestMapping(value = "/items", method = RequestMethod.GET)
	public Collection<Item> items() {
		return itemService.getItems();
	}

	@RequestMapping(value = "/items", method = RequestMethod.POST)
	public Item addItem(@RequestBody Item item) {
		return itemService.addItem(item);
	}

	@RequestMapping(value = "/items/{id}", method = RequestMethod.GET)
	public Item getItem(@PathVariable(value = "id") Long id) {
		return itemService.getItem(id);
	}
}
```

We need to add the relevent security for ```/user/**``` and ```/admin/**``` path patterns in the ```ResourceServerConfig```.
```java
  
  @Override
	public void configure(HttpSecurity http) throws Exception {
		http.anonymous().disable().authorizeRequests().antMatchers("/user/**").access("hasRole('USER')")
				.antMatchers("/admin/**").access("#oauth2.hasScope('ADMIN')").and().exceptionHandling()
				.accessDeniedHandler(new OAuth2AccessDeniedHandler());
	}
```
One more thing is that, ```#oauth2.hasScope('ADMIN')``` to support the secuirty expression handler for ```oauth2```, we need to enable the ```@EnableGlobalMethodSecurity``` and expression handler, I added new class for this which is called ```MethodSecurityConfiguration```:
```java
package com.developerhelperhub.ms.id.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

	@Override
	protected MethodSecurityExpressionHandler createExpressionHandler() {
		return new OAuth2MethodSecurityExpressionHandler();
	}
}
```
**Note:** ```prePostEnabled = true, securedEnabled = true``` should be added in the global method security annotation, because I got an errors are ```Caused by: java.lang.IllegalStateException: In the composition of all global method configuration, no annotation support was actually activated``` and ```java.lang.IllegalArgumentException: Failed to evaluate expression '#oauth2.throwOnError(oauth2.hasScope('ADMIN'))'```.


### Code changes in the sales-service.
I am explain the changes in two section, one is, how to implement the service call from this servie to ```inventory-service``` through the oauth2 and second section for enabling the circute breaker.

#### Section 1: implementing the Oauth2 service call
We need to enable the ```@EnableOAuth2Client``` and create new bean class ```OAuth2RestTemplate``` for implementing the rest template to call the endpoints of ```inventory-service```. We should enable the oauth2 client when we are using the oauth2 implementation in the project. 

The below code need to be added for bean creation in the ```ResourceServerConfig```:
```java
        @Bean
	@LoadBalanced
	public OAuth2RestTemplate identityRestTemplate() {
		final ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
		resourceDetails.setId("identity-service");
		resourceDetails.setClientId("my-cloud-identity-credentials");
		resourceDetails.setClientSecret("VkZpzzKa3uMq4vqg");
		resourceDetails.setGrantType("client_credentials");
		resourceDetails.setScope(Arrays.asList("ADMIN"));
		resourceDetails.setAccessTokenUri("http://localhost:8081/auth/oauth/token");

		OAuth2RestTemplate template = new OAuth2RestTemplate(resourceDetails);

		return template;
	}
```

**Note:**
* I already mentioned in above, we are using the ```client credentials``` for accessing the endpoints. We should add the ```my-cloud-identity-credentials``` client in the ```identity-service```.
* We need to add the ```@LoadBalanced``` annotation to call the APIs with service name in the rest template like ```http://inventory-service/admin/items```. The default load balance configuration will be added in the rest template calls.

#### Section 2: Enabling the ciruit breaker
We need to add the ```@EnableCircuitBreaker``` annotation in the main class ```SalesServiceApplication``` and add below  dependency in the pom.xml file.
```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```
I added the below property to disable the default circuit breaker configuration and added the ```HystrixConfiguration``` configuration class to customise the configuration.
```yml
spring:
  cloud:
    circuitbreaker:
      hystrix:
        enabled: false

```

Code in the HystrixConfiguration:
```java
package com.developerhelperhub.ms.id.config;

import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.netflix.hystrix.HystrixCircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

@Configuration
public class HystrixConfiguration {

	@Bean
	public Customizer<HystrixCircuitBreakerFactory> defaultConfig() {
		return factory -> factory.configureDefault(id -> HystrixCommand.Setter
				.withGroupKey(HystrixCommandGroupKey.Factory.asKey(id)).andCommandPropertiesDefaults(

						HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(4000)

				));
	}

}
```

We need to enable the ```actuator``` dependency for enabling the ```/actuator/hystrix.stream``` API. This API is used to view the graph in the ```my-cloud-circuit-breaker``` dashboard.
```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Once added the actuator dependency, we required to provide the endpoint access to access ```hystrix.stream``` in the property file.
```yml
management:
  endpoint:
    health:
      show-details: always
  endpoints:
    web:
      exposure:
        include: hystrix.stream, info, health, metrics
```
**Note:*** I am enabling the info, health, matrics API of actuator, this APIs helps to monitor the application on production and also we added ```show-details: always``` for health endpoint to view the all details of health information.

I provided the basic security for ```/actuator/**``` APIs for this, I added the class ```SecurityConfiguration```.
```java
package com.developerhelperhub.ms.id.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(1)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests().antMatchers("/actuator/**").authenticated().and().httpBasic();
	}

}
```

Basic security username and password added in the property file
```yml
spring:
  security:
    user:
      name: breaker
      password: breaker
```

#### Circuit breaker and REST API call implementation
We created the ```InventoryService``` service and ```Item``` model to get the information from the ```inventory-service```.

Code of ```Item``` model:
```java
package com.developerhelperhub.ms.id.inventory.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {

	private Long id;
	private String name;
	private int quantity;

	public Item() {
	}

	public Item(Long id, String name, int quantity) {
		super();
		this.id = id;
		this.name = name;
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", quantity=" + quantity + "]";
	}

}
```

Code of ```InventoryService``` service:
```java
package com.developerhelperhub.ms.id.inventory.service;

import java.util.Collection;

public interface InventoryService {

	public Item addItem(Item item);

	public Item getItem(Long id);

	public Collection<Item> getItems();
}
```

Code of ```InventoryServiceImpl``` service implementation of inventory service:
```java
package com.developerhelperhub.ms.id.inventory.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
public class InventoryServiceImpl implements InventoryService {

	private static final Logger LOGGER = LoggerFactory.getLogger(InventoryServiceImpl.class);

	@Autowired
	private OAuth2RestTemplate restTemplate;

	@HystrixCommand(fallbackMethod = "defaultAddItem")
	public Item addItem(Item item) {

		ParameterizedTypeReference<Item> reference = new ParameterizedTypeReference<Item>() {
		};

		HttpEntity<Item> headerEntity = new HttpEntity<Item>(item);

		ResponseEntity<Item> entity = restTemplate.exchange("http://inventory-service/admin/items", HttpMethod.POST,
				headerEntity, reference);

		LOGGER.debug("Added item : {}", entity.getBody());

		return entity.getBody();
	}

	public Item defaultAddItem(Item item) {
		return new Item(0L, "Default Item", 0);
	}

	@HystrixCommand(fallbackMethod = "defaultGetItem")
	public Item getItem(Long id) {

		ParameterizedTypeReference<Item> reference = new ParameterizedTypeReference<Item>() {
		};

		ResponseEntity<Item> entity = restTemplate.exchange("http://inventory-service/admin/items/" + id,
				HttpMethod.GET, null, reference);

		LOGGER.debug("Get item by {}", id);

		return entity.getBody();
	}

	public Item defaultGetItem(Long id) {
		return new Item(0L, "Default Item", 0);
	}

	@HystrixCommand(fallbackMethod = "defaultGetItems")
	public Collection<Item> getItems() {

		ParameterizedTypeReference<List<Item>> reference = new ParameterizedTypeReference<List<Item>>() {
		};

		ResponseEntity<List<Item>> entity = restTemplate.exchange("http://inventory-service/admin/items",
				HttpMethod.GET, null, reference);

		LOGGER.debug("{} items found", entity.getBody().size());

		return entity.getBody();
	}

	public Collection<Item> defaultGetItems() {
		List<Item> items = new ArrayList<Item>();
		items.add(new Item(0L, "Default Item", 0));
		return items;
	}

}
```
I added the callback methods in each method with help of ```@HystrixCommand``` annotation to specify which method is required to call when its fail the REST API call. eg: We added ```defaultAddItem``` callback method on ```addItem``` method, the ```defaultAddItem``` method call and send the hardcoded item information to the client, when the rest API failed.

We provides the endpoints in the ```SalesController```:
```java
package com.developerhelperhub.ms.id.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.developerhelperhub.ms.id.inventory.service.InventoryService;
import com.developerhelperhub.ms.id.inventory.service.Item;

@RestController
public class SalesController {

	@Autowired
	private InventoryService inventoryService;

	@RequestMapping(value = "/items", method = RequestMethod.GET)
	public Collection<Item> items() {
		return inventoryService.getItems();
	}

	@RequestMapping(value = "/items", method = RequestMethod.POST)
	public Item addItem(@RequestBody Item item) {
		return inventoryService.addItem(item);
	}

	@RequestMapping(value = "/items/{id}", method = RequestMethod.GET)
	public Item getItem(@PathVariable(value = "id") Long id) {
		return inventoryService.getItem(id);
	}
}
```

### Creating the my-cloud-circuit-breaker hystrix dashboard

This project is used to view the hystrix dashboard to view the all reports of all microservice. This microsrvice is running under the discovery service and authentication service. We need to add the below deplendecy for enabling the dashboard.
```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
</dependency>
```

We need to enable the ```@EnableHystrixDashboard``` annotation in the main class MyCloudCircuitBreakerApplication:
```java
package com.developerhelperhub.ms.id;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

@SpringBootApplication
@EnableDiscoveryClient
@EnableHystrixDashboard
public class MyCloudCircuitBreakerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyCloudCircuitBreakerApplication.class, args);
	}

}
```

We are enabling the basic security for these URL pattern ```"/", "/hystrix"``` for viewing the dashboard in the ```SecurityConfiguration``` class:
```java
package com.developerhelperhub.ms.id.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(2)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests().antMatchers("/", "/hystrix").authenticated().and().httpBasic()
				.authenticationEntryPoint(new AuthenticationEntryPointImpl());
	}

}
```

Username and password added in the property file
```yml
spring:
  security:
    user:
      name: breaker
      password: breaker
```

### Code changes in the identity-service.

We added the ```my-cloud-identity-credentials``` client in the run method of ```IdentityServiceApplication```.
```java
                client.setClientId("my-cloud-identity-credentials");
		client.setClientSecret("VkZpzzKa3uMq4vqg");

		// Added the new resources id's
		client.setResourceIds(new HashSet<String>(Arrays.asList("identity_id", "inventory_service_resource_id",
				"api_gateway_resource_id", "sales_service_resource_id", "my_cloud_discovery_id")));

		client.addGrantedAuthority("ADMIN");

		client.setSecretRequired(true);
		client.setScoped(true);
		client.setScope(new HashSet<String>(Arrays.asList("ADMIN")));
		client.setAuthorizedGrantTypes(new HashSet<String>(Arrays.asList("client_credentials")));
		client.setAccessTokenValiditySeconds(43199);
		client.setRefreshTokenValiditySeconds(83199);
		client.setAutoApprove(true);

		client.create();
```

### Testing 
I provided the collection and environment file of postman in the doc folder. Before start testing we have to run the below services in the order.

* identity-service
* my-cloud-discovery-service
* inventory-service
* sales-service
* api-gateway-service

**Note:** In this collection, I added the collection of inventory and sales service, so we can use the collection also testing without using API Gateway service.

**Testing API calles in order:**
* Identity Service
  * Login: POST ```http://localhost:8081/auth/oauth/token```
* API Gateway: testing invendory service
  * Get all items: GET - ```http://localhost:8085/inventory/user/items```
  * Add new item: PUT - ```http://localhost:8085/inventory/user/items```
  * Get item by id: GET - ```http://localhost:8085/inventory/user/items/1```
* API Gateway: testing sales service
  * Get all items: GET - ```http://localhost:8085/sales/items```
  * Add new item: PUT - ```http://localhost:8085/sales/items```
  * Get item by id: GET - ```http://localhost:8085/sales/items/1```

**Testing Hystrix Dashboard:**
* We can execute the ```http://localhost:8086/hystrix/``` in the brower, it ask the username and password which is ```breaker``` and ```breaker```.
* Once loaded the page, we need to provide the sales service ```/actuator/hystrix.stream``` URL in the page. which is ```http://breaker:breaker@localhost:8083/actuator/hystrix.stream```.
* Enter the ```Sales Service``` in the title input box
* Press the ```Monitor Stream``` button

**Note:** We will see the ```loading...``` label in the dashboard, because if any API call didn't happen of the sales service. In this cases: We required to invoke ```http://localhost:8085/sales/items``` API first in the postman, then you can see the graph is loading in the dashboard.

### Reference
* [Spring boot circuit breaker](https://cloud.spring.io/spring-cloud-netflix/2.2.x/reference/html/#circuit-breaker-spring-cloud-circuit-breaker-with-hystrix)
* [Rest template implementation](https://spring.io/blog/2015/01/20/microservice-registration-and-discovery-with-spring-cloud-and-netflix-s-eureka)
* [Oauth Scope Security](https://docs.spring.io/spring-security-oauth2-boot/docs/current/reference/htmlsingle/)
