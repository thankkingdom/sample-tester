package com.sampletester.request.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RestApiRequestBase extends RestApiRequestCore {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public static final String STATUS_ERROR_MESSAGE = "Http Status Code does not match the expectation. The expectaton is %s, but the actual is %s.";

	private String url;
	private String requestMethod = "GET";
	private Map<String, String> params = new HashMap<>();
	private String expectedHttpStatusCode = "200";
	private List<Map<String, String>> expectedErrors = new ArrayList<>();
	private RestApiRequestBase nextRequest;

	public RestApiRequestBase(String url) {
		this.url = url;
	}

	public RestApiRequestBase(String url, String requestMethod) {
		this.url = url;
		this.requestMethod = requestMethod;
	}

	public boolean run() {
		if ("POST".equalsIgnoreCase(requestMethod)) {
			return post();
		}
		return get();
	}

	@SuppressWarnings("unchecked")
	private final boolean checkResponse(Map<String, Object> response) {
		// Http Status Code Check
		if (StringUtils.isNotEmpty(expectedHttpStatusCode)
				&& !expectedHttpStatusCode.equals(response.getOrDefault(MAP_KEY_HTTP_STATUS_CODE, ""))) {
			logger.error(String.format(STATUS_ERROR_MESSAGE, response.getOrDefault(MAP_KEY_HTTP_STATUS_CODE, ""),
					expectedHttpStatusCode));
			return false;
		}

		// Error Code & Message Check
		List<Map<String, String>> errors = new ArrayList<>();
		errors = (List<Map<String, String>>) response.getOrDefault("errors", new ArrayList<>());
		if (CollectionUtils.isNotEmpty(expectedErrors)) {

			// The Error Number Mismatch
			if (CollectionUtils.isEmpty(errors) || expectedErrors.size() != errors.size()) {
				logger.error(String.format("The number of error does not match. expected is %s, but actual is %s",
						expectedErrors.size(), errors.size()));
				return false;
			}
			// The Code or Message Mismatch
			boolean checkFlag = true;
			for (Map<String, String> expectedError : expectedErrors) {
				if (!errors.contains(expectedError)) {
					checkFlag = false;
				}
			}
			if (!checkFlag) {
				logger.error("The error code or message does not matched.");
				logger.info(String.format("Expected Errors: %s", expectedErrors));
				logger.info(String.format("  Actual Errors: %s", errors));
				return false;
			}
		}
		return true;
	}

	public boolean get() {
		Map<String, Object> response = GET(url, params);
		logger.info(String.format("Response: %s", response));
		boolean result = checkResponse(response);
		if (nextRequest != null) {
			return nextRequest.run();
		}
		return result;
	}

	public boolean get(Map<String, String> params) {
		this.params = params;
		return get();
	}

	public boolean get(Map<String, String> params, String expectedHttpStatusCode) {
		this.params = params;
		this.expectedHttpStatusCode = expectedHttpStatusCode;
		return get();
	}

	public boolean get(Map<String, String> params, String expectedHttpStatusCode,
			List<Map<String, String>> expectedErrors) {
		this.params = params;
		this.expectedHttpStatusCode = expectedHttpStatusCode;
		this.expectedErrors = expectedErrors;
		return get();
	}

	public boolean post() {
		Map<String, Object> response = POST(url, params);
		logger.info(String.format("Response: %s", response));
		boolean result = checkResponse(response);
		if (nextRequest != null) {
			return nextRequest.run();
		}
		return result;
	}

	public boolean post(Map<String, String> params) {
		this.params = params;
		return post();
	}

	public boolean post(Map<String, String> params, String expectedHttpStatusCode) {
		this.params = params;
		this.expectedHttpStatusCode = expectedHttpStatusCode;
		return post();
	}

	public boolean post(Map<String, String> params, String expectedHttpStatusCode,
			List<Map<String, String>> expectedErrors) {
		this.params = params;
		this.expectedHttpStatusCode = expectedHttpStatusCode;
		this.expectedErrors = expectedErrors;
		return post();
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public String getExpectedHttpStatusCode() {
		return expectedHttpStatusCode;
	}

	public void setExpectedHttpStatusCode(String expectedHttpStatusCode) {
		this.expectedHttpStatusCode = expectedHttpStatusCode;
	}

	public List<Map<String, String>> getExpectedErrors() {
		return expectedErrors;
	}

	public void setExpectedErrors(List<Map<String, String>> expectedErrors) {
		this.expectedErrors = expectedErrors;
	}

	public RestApiRequestBase setNextRequest(RestApiRequestBase nextRequest) {
		this.nextRequest = nextRequest;
		return nextRequest;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

}
