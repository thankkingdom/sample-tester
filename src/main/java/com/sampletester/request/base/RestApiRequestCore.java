package com.sampletester.request.base;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class RestApiRequestCore {

	public static final String MAP_KEY_HTTP_STATUS_CODE = "http_status_code";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final OkHttpClient client = new OkHttpClient();
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final String ContentTypeJson = "application/json;charset=utf8";
	private final MediaType JSON = MediaType.parse(ContentTypeJson);

	@SuppressWarnings("unchecked")
	protected final Map<String, Object> GET(String url, Map<String, String> params) {
		logger.info(String.format("GET: %s", url));
		logger.info(String.format("params: %s", params));
		Map<String, Object> result = new HashMap<String, Object>();

		HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
		params.forEach(urlBuilder::addEncodedQueryParameter);

		//@formatter:off
		Request request = new Request.Builder().addHeader("Content-Type", ContentTypeJson)
				.url(urlBuilder.build()).get().build();
		//@formatter:on

		try (Response response = client.newCall(request).execute()) {
			String jData = response.body().string();
			// JSONObject jObject = new JSONObject(jData);
			result = objectMapper.readValue(jData, Map.class);
			result.put(MAP_KEY_HTTP_STATUS_CODE, String.valueOf(response.code()));
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			result.put(MAP_KEY_HTTP_STATUS_CODE, "500");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	protected final Map<String, Object> POST(String url, Map<String, String> params) {
		logger.info(String.format("POST: %s", url));
		logger.info(String.format("params: %s", params));
		Map<String, Object> result = new HashMap<String, Object>();

		String content = "";
		try {
			content = objectMapper.writeValueAsString(params);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}

		RequestBody body = RequestBody.create(JSON, content);
		Request request = new Request.Builder().url(url).post(body).build();

		try (Response response = client.newCall(request).execute()) {
			String jData = response.body().string();
			result = objectMapper.readValue(jData, Map.class);
			result.put(MAP_KEY_HTTP_STATUS_CODE, String.valueOf(response.code()));
			return result;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
