package com.tacitknowledge.parser;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class JsonParser {

    public JsonObject parse(String jsonString) {
        com.google.gson.JsonParser parser = new com.google.gson.JsonParser();

        JsonObject object;

        try {
            object = parser.parse(jsonString).getAsJsonObject();
        } catch (JsonParseException ex) {
            throw new RuntimeException("Exception parsing json string: " + jsonString, ex);
        }
        return object;
    }
}
