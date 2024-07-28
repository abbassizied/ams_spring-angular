package com.sip.ams;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sip.ams.entities.Provider;

@SpringBootTest(classes = AmsRestApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ProviderRestControllerIntegrationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void testGetAllProviders() throws Exception {
		restTemplate = new TestRestTemplate();
		URI uri = new URI("http://localhost:" + port + "/providers");

		ResponseEntity<Provider[]> response = restTemplate.getForEntity(uri, Provider[].class);
		Provider[] employees = response.getBody();
		assertThat(employees.length > 0);

	}

	@Test
	void testAddProvider_success() throws Exception {
		restTemplate = new TestRestTemplate();
		URI uri = new URI("http://localhost:" + port + "/providers");

		Provider provider = new Provider("Japon", "TOSHIBA CORPORATION", "tdsl-infoweb@ml.toshiba.co.jp");
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(uri, provider, String.class);
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
	}

}
