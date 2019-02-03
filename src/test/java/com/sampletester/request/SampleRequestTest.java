package com.sampletester.request;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestInstance(Lifecycle.PER_CLASS)
public class SampleRequestTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void testGet() {
		logger.info("testGet() START");

		Map<String, String> params = new HashMap<>();
		params.put("token", "TOKEN");
		String expectedHttpStatusCode = "200";

		InfoRestApiRequest request = new InfoRestApiRequest("TOKEN", 1L);
		request.setExpectedHttpStatusCode(expectedHttpStatusCode);

		boolean result = request.run();
		logger.info("testGet() END");
		assertTrue(result);
	}

	@Test
	public void testPost() {
		logger.info("testPost() START");

		String url = "http://localhost:8080/bad";

		Map<String, String> params = new HashMap<>();
		params.put("token", "TOKEN");
		params.put("reservation_id", String.valueOf(2));

		String expectedHttpStatusCode = "200";
		List<Map<String, String>> expectedErrors = new ArrayList<>();
		HashMap<String, String> error = new HashMap<>();
		error.put("code", "E001");
		error.put("message", "エラー");
		expectedErrors.add(error);

		RestApiPostRequest request = new RestApiPostRequest(url);
		request.setParams(params);
		request.setExpectedHttpStatusCode(expectedHttpStatusCode);
		request.setExpectedErrors(expectedErrors);
		boolean result = request.run();

		logger.info("testPost() END");
		assertTrue(result);
	}

	@Test
	public void testChain() {
		logger.info("testChain() START");

		Map<String, String> params = new HashMap<>();
		params.put("token", "TOKEN");
		String expectedHttpStatusCode = "200";
		Map<String, String> expectedContents = new HashMap<>();
		expectedContents.put("subject", "こんにちは");

		InfoRestApiRequest request1 = new InfoRestApiRequest("TOKEN", 3L);
		request1.setExpectedHttpStatusCode(expectedHttpStatusCode);
		request1.setExpectedContents(expectedContents);

		Map<String, String> expectedContents2 = new HashMap<>();
		expectedContents2.put("subject", "こんにちは２");
		expectedContents2.put("text", "");

		InfoRestApiRequest request2 = new InfoRestApiRequest("TOKEN", 4L);
		request2.setExpectedHttpStatusCode(expectedHttpStatusCode);
		request2.setExpectedContents(expectedContents2);

		request1.setNextRequest(request2);
		boolean result = request1.run();

		logger.info("testChain() END");
		assertTrue(result);
	}
}
