package com.waynezhang.test.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TestApiParams {
    private Gson gson = new Gson();
    private JsonObject jsonObject = new JsonObject();

    public TestApiParams() {
    }

    public TestApiParams(Object params) {
        jsonObject = (JsonObject)gson.toJsonTree(params);
    }

    public TestApiParams(String key, String value) {
        jsonObject.addProperty(key, value);
    }

    public TestApiParams(String key, int value) {
        jsonObject.addProperty(key, value);
    }

    public TestApiParams add(String key, String value) {
        jsonObject.addProperty(key, value);
        return this;
    }

    public TestApiParams add(String key, int value) {
        jsonObject.addProperty(key, value);
        return this;
    }

    public TestApiParams add(String key, long value) {
        jsonObject.addProperty(key, value);
        return this;
    }

    public String toUrlString() {
        try {
            return URLEncoder.encode(toString(), "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
            return "";
        }
    }

    public String toString() {
        return gson.toJson(jsonObject);
    }

    public String get(String key) {
        return jsonObject.get(key).getAsString();
    }

}
