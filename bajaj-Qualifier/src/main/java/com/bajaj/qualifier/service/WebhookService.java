package com.bajaj.qualifier.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bajaj.qualifier.dto.FinalQueryRequest;
import com.bajaj.qualifier.dto.WebhookRequest;
import com.bajaj.qualifier.dto.WebhookResponse;

@Service
public class WebhookService {

	private final RestTemplate restTemplate;

	public WebhookService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void executeFlow() {

		String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

		WebhookRequest request = new WebhookRequest();
		request.setName("Hardik");
		request.setRegNo("250850120076");
		request.setEmail("hardikbansla@gmail.com");

		WebhookResponse response = restTemplate.postForObject(generateUrl, request, WebhookResponse.class);

		if (response == null) {
			throw new RuntimeException("No response from generateWebhook");
		}

		String finalSqlQuery = """
				SELECT
					    id,
					    name,
					    created_at
					FROM users
					ORDER BY created_at DESC;

									        """;

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", response.getAccessToken());

		FinalQueryRequest finalQuery = new FinalQueryRequest(finalSqlQuery);

		HttpEntity<FinalQueryRequest> entity = new HttpEntity<>(finalQuery, headers);

		restTemplate.postForEntity(response.getWebhook(), entity, String.class);
	}
}
