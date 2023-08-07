package com.example.workinstructions;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class JsonParser {
    public static List<String> parseJsonAndGetSteps(String jsonString) {
        List<String> stepsList = new ArrayList<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonMap = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
            });

            List<Map<String, Object>> steps = (List<Map<String, Object>>) jsonMap.get("steps");
            for (Map<String, Object> stepMap : steps) {
                String step = (String) stepMap.get("step");
                stepsList.add(step);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return stepsList;
    }

    // Example usage
//    public static void main(String[] args) {
//        String jsonString = "{ \"steps\": [ { \"step\": \"1. Inspect the bearing, thrust bearing, and support for any signs of wear or damage.\", \"keywords\": [\"inspect\", \"bearing\", \"thrust bearing\", \"support\", \"wear\", \"damage\"] }, { \"step\": \"2. If any signs of wear or damage are found, replace the bearing, thrust bearing, and support.\", \"keywords\": [\"replace\", \"bearing\", \"thrust bearing\", \"support\", \"wear\", \"damage\"] }, { \"step\": \"3. Clean the bearing, thrust bearing, and support with a clean cloth.\", \"keywords\": [\"clean\", \"bearing\", \"thrust bearing\", \"support\", \"cloth\"] }, { \"step\": \"4. Apply a thin layer of lubricant to the bearing, thrust bearing, and support.\", \"keywords\": [\"apply\", \"lubricant\", \"bearing\", \"thrust bearing\", \"support\"] }, { \"step\": \"5. Reassemble the bearing, thrust bearing, and support in the correct order.\", \"keywords\": [\"reassemble\", \"bearing\", \"thrust bearing\", \"support\", \"order\"] }, { \"step\": \"6. Test the bearing, thrust bearing, and support for proper operation.\", \"keywords\": [\"test\", \"bearing\", \"thrust bearing\", \"support\", \"operation\"] } ] }";
//        List<String> stepsList = parseJsonAndGetSteps(jsonString);
//
//        for (String step : stepsList) {
//            Log.d("JSON Parser", "Step: " + step);
//        }
//    }
}
