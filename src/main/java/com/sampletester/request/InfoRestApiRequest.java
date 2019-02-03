package com.sampletester.request;

import java.util.HashMap;
import java.util.Map;

import com.sampletester.request.base.RestApiRequestTestBase;

public class InfoRestApiRequest extends RestApiRequestTestBase {

	private static final String url = "http://localhost:8080/info";

	public InfoRestApiRequest(String apiToken, Long id) {
		super(url);
		Map<String, String> params = new HashMap<>();
		params.put("api_token", apiToken);
		params.put("reservation_id", String.valueOf(id));
		setParams(params);
	}

}
