package io.github.mlinardos.kvstore.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Parser {



    public Map<String, Object> parseString(String inputLine) throws IOException {

        JsonNode json;
        ObjectMapper mapper;
        inputLine = inputLine.replaceAll(" -> ", ":");
        inputLine = inputLine.replaceAll(" \\| ", ",");
        inputLine = "{" + inputLine + "}";
        inputLine = inputLine.replaceAll("\\[", "{");
        inputLine = inputLine.replaceAll("\\]", "}");


        try {
            mapper = new ObjectMapper();
            json = mapper.readTree(inputLine);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        //  Map<String, Object> map = mapper.convertValue(json, Map.class);

        List<String> keys = new ArrayList<>();
        Map<String, Object> jsonElements = mapper.readValue(inputLine, new TypeReference<Map<String, Object>>() {
        });
        getAllKeys(jsonElements, keys);

        return (jsonElements);

    }


    public String resultToString(String key , Object value) throws JsonProcessingException {
        String result;
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(value);
        jsonString = jsonString.replaceAll(":"," -> " );
        jsonString = jsonString.replaceAll( ",", " | ");
        jsonString = jsonString.replaceAll("\\{" ,"[");
        jsonString = jsonString.replaceAll( "\\}", "]");
        result = key + " -> " + jsonString;
        return result;
    }



    private void getAllKeys(Map<String, Object> jsonElements, List<String> keys) {
        jsonElements.entrySet()
                .forEach(entry -> {
                    keys.add(entry.getKey());
                    if (entry.getValue() instanceof Map) {
                        Map<String, Object> map = (Map<String, Object>) entry.getValue();
                        getAllKeys(map, keys);
                    } else if (entry.getValue() instanceof List) {
                        List<?> list = (List<?>) entry.getValue();
                        list.forEach(listEntry -> {
                            if (listEntry instanceof Map) {
                                Map<String, Object> map = (Map<String, Object>) listEntry;
                                getAllKeys(map, keys);
                            }
                        });
                    }
                });
    }



    public JsonNode ObjectToJsonNode(Object value) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(value);
        JsonNode json = mapper.readTree(jsonString);
        return json;
    }

}
