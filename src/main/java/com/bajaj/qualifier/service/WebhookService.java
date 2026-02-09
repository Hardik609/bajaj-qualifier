package com.bajaj.qualifier.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

		System.out.println("Calling generateWebhook API...");

		String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

		WebhookRequest request = new WebhookRequest();
		request.setName("Hardik");
		request.setRegNo("250850120076"); // EVEN
		request.setEmail("hardikbansla@gmail.com");

		WebhookResponse response = restTemplate.postForObject(generateUrl, request, WebhookResponse.class);

		if (response == null) {
			throw new RuntimeException("No response from generateWebhook");
		}

		System.out.println("Webhook URL received: " + response.getWebhook());
		System.out.println("Access Token received");

		// EVEN PRN → Question 2
		String finalSqlQuery = """
				                SELECT
				    d.DEPARTMENT_NAME,
				    ROUND(AVG(TIMESTAMPDIFF(YEAR, e.DOB, CURDATE())), 2) AS AVERAGE_AGE,
				    SUBSTRING_INDEX(
				        GROUP_CONCAT(
				            CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME)
				            ORDER BY e.FIRST_NAME SEPARATOR ', '
				        ),
				        ', ',
				        10
				    ) AS EMPLOYEE_LIST
				FROM DEPARTMENT d
				JOIN EMPLOYEE e
				    ON e.DEPARTMENT = d.DEPARTMENT_ID
				JOIN PAYMENTS p
				    ON p.EMP_ID = e.EMP_ID
				WHERE p.AMOUNT > 70000
				GROUP BY d.DEPARTMENT_ID, d.DEPARTMENT_NAME
				ORDER BY d.DEPARTMENT_ID DESC;
				                """;

		System.out.println("FINAL SQL QUERY:");
		System.out.println(finalSqlQuery);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", response.getAccessToken());

		FinalQueryRequest finalQuery = new FinalQueryRequest(finalSqlQuery);

		HttpEntity<FinalQueryRequest> entity = new HttpEntity<>(finalQuery, headers);

		System.out.println("Submitting SQL to webhook...");

		ResponseEntity<String> submitResponse = restTemplate.postForEntity(response.getWebhook(), entity, String.class);

		System.out.println("Submission response status: " + submitResponse.getStatusCode());
	}
}
