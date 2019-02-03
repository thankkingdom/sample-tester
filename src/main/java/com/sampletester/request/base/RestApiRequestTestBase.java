package com.sampletester.request.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RestApiRequestTestBase extends RestApiRequestBase {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public static final String STATUS_ERROR = "HTTPステータスコードが予想と異なります。予想：%s、実際：%s";
	public static final String ERROR_SIZE_ERROR = "エラー数が予想と異なります。予想：%s、実際：%s";
	public static final String ERROR_CONTENTS_MISMATCH_ERROR = "エラーコードもしくはメッセージが予想と異なります。";
	public static final String CONTENTS_KEY_NOT_FOUND = "コンテンツ「%s」が見つかりませんでした。";
	public static final String CONTENTS_VALUE_ERROR = "コンテンツ「%s」の値が等しくありません。予想：%s、実際：%s";

	private String url;
	private String requestMethod = "GET";
	private Map<String, String> params = new HashMap<>();
	private String expectedHttpStatusCode = "200";
	private List<Map<String, String>> expectedErrors = new ArrayList<>();
	private Map<String, String> expectedContents = new HashMap<>();
	private RestApiRequestTestBase nextRequest;

	public RestApiRequestTestBase(String url) {
		this.url = url;
	}

	public RestApiRequestTestBase(String url, String requestMethod) {
		this.url = url;
		this.requestMethod = requestMethod;
	}

	public boolean run() {
		if ("POST".equalsIgnoreCase(requestMethod)) {
			return post();
		}
		return get();
	}

	// Override if you implement in your own way.
	protected boolean checkErrorCodeAndMessage(List<Map<String, String>> expects, List<Map<String, String>> actuals) {
		// The Error Number Mismatch
		if (CollectionUtils.isEmpty(actuals) || expects.size() != actuals.size()) {
			logger.error(String.format(ERROR_SIZE_ERROR, expects.size(), actuals.size()));
			return false;
		}
		// The Code or Message Mismatch
		boolean checkFlag = true;
		for (Map<String, String> expectedError : expects) {
			if (!actuals.contains(expectedError)) {
				checkFlag = false;
			}
		}
		if (!checkFlag) {
			logger.error(ERROR_CONTENTS_MISMATCH_ERROR);
			logger.info(String.format("Expected Errors: %s", expects));
			logger.info(String.format("  Actual Errors: %s", actuals));
			return false;
		}
		return true;
	}

	// Override if you implement in your own way.
	protected boolean checkContents(Map<String, String> expects, Map<String, Object> actuals) {
		boolean checkFlg = true;
		for (String key : expects.keySet()) {
			if (!actuals.containsKey(key)) {
				logger.error(String.format(CONTENTS_KEY_NOT_FOUND, key));
				checkFlg = false;
			} else if (!expects.getOrDefault(key, "").equals(actuals.getOrDefault(key, ""))) {
				logger.error(String.format(CONTENTS_VALUE_ERROR, key, expects.getOrDefault(key, ""),
						actuals.getOrDefault(key, "")));
				checkFlg = false;
			}
		}
		if (!checkFlg) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private final boolean checkResponse(Map<String, Object> response) {
		// Http Status Code Check
		if (StringUtils.isNotEmpty(expectedHttpStatusCode)) {
			if (!expectedHttpStatusCode.equals(response.getOrDefault(MAP_KEY_HTTP_STATUS_CODE, ""))) {
				logger.error(String.format(STATUS_ERROR, expectedHttpStatusCode,
						response.getOrDefault(MAP_KEY_HTTP_STATUS_CODE, "")));
				return false;
			}
			logger.info("HTTPステータス チェックOK.");
		}

		// Error Code & Message Check
		List<Map<String, String>> errors = (List<Map<String, String>>) response.getOrDefault("errors",
				new ArrayList<>());
		if (CollectionUtils.isNotEmpty(expectedErrors)) {
			if (!checkErrorCodeAndMessage(expectedErrors, errors)) {
				return false;
			}
			logger.info("エラー チェックOK.");
		}

		// Contents Check
		if (expectedContents != null && expectedContents.keySet().size() > 0) {
			if (!checkContents(expectedContents, response)) {
				return false;
			}
			logger.info("コンテンツ チェックOK.");
		}
		return true;
	}

	public boolean get() {
		Map<String, Object> response = GET(url, params);
		logger.info(String.format("Response: %s", response));
		boolean result = checkResponse(response);
		if (!result) {
			return false;
		}
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

	public Map<String, String> getExpectedContents() {
		return expectedContents;
	}

	public void setExpectedContents(Map<String, String> expectedContents) {
		this.expectedContents = expectedContents;
	}

	public RestApiRequestTestBase setNextRequest(RestApiRequestTestBase nextRequest) {
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
