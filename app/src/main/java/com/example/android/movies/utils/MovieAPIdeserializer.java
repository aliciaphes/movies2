package com.example.android.movies.utils;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class MovieAPIdeserializer<T> implements JsonDeserializer<List<T>> {

    private Class<T[]> tClass;


    public MovieAPIdeserializer(Class<T[]> klass) {
        tClass = klass;
    }

    @Override
    public List<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonElement content = json.getAsJsonObject().get("results");
        T[] arrayOfT = new Gson().fromJson(content, tClass);
        return Arrays.asList(arrayOfT);
    }

}
