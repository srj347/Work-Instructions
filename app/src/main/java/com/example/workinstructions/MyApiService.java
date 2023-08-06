package com.example.workinstructions;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyApiService {
    private static final String BASE_URL = "https://api.imagga.com/v2/";

    public String getTags(String imageUrl) {
        String apiKey = "acc_c3ee5e8317eed75";
        String apiSecret = "ca0d5f804945a16d8da6cbd8725cbaa6";

        OkHttpClient client = new OkHttpClient();

        // Build the request URL with the image URL as a query parameter
        String requestUrl = BASE_URL + "tags?image_url=" + imageUrl;

        // Create a basic authentication header
        String credentials = apiKey + ":" + apiSecret;
        String basicAuthHeader = "Basic " + android.util.Base64.encodeToString(credentials.getBytes(), android.util.Base64.NO_WRAP);

        Request request = new Request.Builder()
                .url(requestUrl)
                .header("Authorization", basicAuthHeader)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                // Handle the error response here if needed
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
