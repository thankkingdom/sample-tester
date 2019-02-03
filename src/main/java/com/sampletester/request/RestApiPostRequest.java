package com.sampletester.request;

import java.util.List;
import java.util.Map;

import com.sampletester.request.base.RestApiRequestBase;

public class RestApiPostRequest extends RestApiRequestBase {

	public RestApiPostRequest(String url) {
		super(url);
		super.setRequestMethod("POST");
	}

	public RestApiPostRequest(String url, Map<String, String> params, String expectedHttpStatusCode,
			List<Map<String, String>> expectedErrors) {
		super(url);
		super.setParams(params);
		super.setExpectedHttpStatusCode(expectedHttpStatusCode);
		super.setExpectedErrors(expectedErrors);
	}
}
