package com.gagein.ui.common;

/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

/**
 * A request for retrieving a {@link JSONObject} response body at a given URL, allowing for an
 * optional {@link JSONObject} to be passed in as part of the request body.
 */
public class JsonUTF8Request extends JsonRequest<JSONObject> {
	protected static final String TYPE_UTF8_CHARSET = "charset=UTF-8";

	public JsonUTF8Request(int method, String url, JSONObject jsonRequest, Listener<JSONObject> listener, ErrorListener errorListener) {
		super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener, errorListener);
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		 try {
	            String type = response.headers.get(HTTP.CONTENT_TYPE);
	            if (type == null) {
	                type = TYPE_UTF8_CHARSET;
	                response.headers.put(HTTP.CONTENT_TYPE, type);
	            } else if (!type.contains("UTF-8")) {
	                type += ";" + TYPE_UTF8_CHARSET;
	                response.headers.put(HTTP.CONTENT_TYPE, type);
	            }
	            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
	            return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
	        } catch (Exception e) {
	        	return Response.error(new ParseError(e));
	        }
	}

}

