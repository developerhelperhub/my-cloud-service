//package com.developerhelperhub.ms.id.config;
//
//import org.junit.runner.RunWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.web.server.LocalServerPort;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class OAuth2AuthorizationServerConfigTest {
//
//	@LocalServerPort
//	private int port;
//
////	@Test
////	public void givenDBUser_whenRevokeToken_thenAuthorized() {
////		Response response = obtainAccessToken("first-client", "noonewilleverguess", "enduser", "password");
////
////		assertEquals(response.getStatusCode(), 200);
////	}
////
////	private Response obtainAccessToken(String clientId, String clientSecret, String username, String password) {
////		Response response = RestAssured.given().contentType("application/x-www-form-urlencoded; charset=UTF-8")
////				.formParam("grant_type", "authorization_code").formParam("client_id", clientId)
////				.formParam("client_secret", clientSecret).formParam("username", username)
////				.formParam("password", password).when().post("http://localhost:" + port + "/oauth/authorize");
////
////		return response;
////	}
//}
