package com.example.workinstructions;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.workinstructions.data.PromptManager;
import com.example.workinstructions.data.RepositoryWI;
import com.example.workinstructions.data.WIRepository;
import com.example.workinstructions.model.WIRequest;
import com.example.workinstructions.model.WorkInstruction;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class WIChatActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView welcomeTextView, textAttachedTv;
    EditText messageEditText;
    ImageButton sendButton, attachButton;
    ImageView uploadImage, imgAttachedIv;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    Boolean isImage = false;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    Uri filepath;
    Bitmap bitmap;
    String questionToImagga, instructionType, previousTags, editedTags;
    MyProgressDialog progressDialog;

    WIRepository wiRepository;
    List<WorkInstruction> workInstructionList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wichat);
        messageList = new ArrayList<>();

        // loading views
        recyclerView = findViewById(R.id.recycler_view);
        welcomeTextView = findViewById(R.id.welcome_text);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);
        attachButton = findViewById(R.id.attach_btn);
        uploadImage = findViewById(R.id.upload_image);
        imgAttachedIv = findViewById(R.id.img_attached);
        textAttachedTv = findViewById(R.id.text_attached);

        initRecyclerView();
        fetchLocalWI();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = messageEditText.getText().toString().trim();
//                generateImage(question);

                if (isImage) {
                    addToChat(bitmap, Message.SENT_BY_ME);
                    if(!question.isEmpty()){
                        addToChat(question, Message.SENT_BY_ME);
                    }
                    isImage = false;
                    File imageFile = convertBitmapToFile(bitmap);
                    uploadImageAndGetUploadId(imageFile);
                    uploadImage.setImageDrawable(null);
                } else {
                    addToChat(question, Message.SENT_BY_ME);
                    if (false) {
                        isWorkInstructionPresent(question);

                    } else {
                        String prompt = new PromptManager().getPrompt(question, "");
                        callAPI(prompt, false);
                    }
                }
                messageEditText.setText("");
                messageEditText.setVisibility(View.VISIBLE);
                disableAttachedView();
            }
        });

        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(WIChatActivity.this)
                        .withPermission(Manifest.permission.INTERNET)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "Select Image File"), 1);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                Log.d("ImagePermission", "onPermissionDenied: " + response.getPermissionName());
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });
    }

    private void fetchLocalWI() {
        RepositoryWI repositoryWI = new RepositoryWI();
        repositoryWI.fetchAllWorkInstruction(
                workInstructions -> {
                    Toast.makeText(WIChatActivity.this, workInstructions.toString(), Toast.LENGTH_SHORT).show();
                    workInstructionList = workInstructions;
                },
                exception -> {
                    Log.d("CurrentDebugg", "Failure => " + exception);
                });
    }



    public String toJSON(Object obj){
        return new Gson().toJson(obj);
    }

    private void initRecyclerView() {
        messageAdapter = new MessageAdapter(this, messageList);
        messageAdapter.setOnItemClickListener(new MessageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Message item) {
                String instructionFormat = "Title, Purpose, Scope, Materials and Tools, Safety Precautions, Step-by-Step Instructions, Quality Checks, Troubleshooting, Maintenance and Cleanup, Sign-off";
                String question = "Create a " + instructionType + " for " + item.getMessage() + " in the specified format. Format: " + instructionFormat;
                callAPI(question, false);
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(messageAdapter);
    }

    public void getTags(String uploadId) {
        // API credentials
        String credentialsToEncode = "acc_c3ee5e8317eed75" + ":" + "ca0d5f804945a16d8da6cbd8725cbaa6";
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString(credentialsToEncode.getBytes(StandardCharsets.UTF_8));

        // API endpoint
        String endpoint = "https://api.imagga.com/v2/tags";

        // Add the upload ID to the endpoint
        HttpUrl.Builder urlBuilder = HttpUrl.parse(endpoint).newBuilder();
        urlBuilder.addQueryParameter("image_upload_id", uploadId);
        String url = urlBuilder.build().toString();

        // Create the OkHttp client
        OkHttpClient client = new OkHttpClient();

        // Create the GET request
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", basicAuth)
                .build();

        // Perform the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle the failure case here
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Check if the response is successful
                if (response.isSuccessful()) {
                    // Handle the successful response here

                    String responseBody = response.body().string();
                    // Process the response as needed
                    Log.d("ResponseBody", responseBody);
                    questionToImagga = parseJsonAndGetFirstTwoEnValues(responseBody);
                    Log.d("CurrentDebugg", "TAGS: " + questionToImagga);
//                    String question = "Write 10 lines about " + questionToImagga;
                    String header = messageEditText.getText().toString();

                    String question = new PromptManager().getPrompt(header,questionToImagga);
                    Log.d("CurrentDebugg", question);
                    progressDialog.dismiss();
//                    generatePrompts(questionToImagga);
                    callAPI(question, false);

                } else {
                    // Handle the error case here
                    System.out.println("Error: " + response.code() + " - " + response.message());
                }
            }
        });
    }

//    public void generatePrompts(String tags) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                previousTags = tags;
//                generateTitle(tags);
//            }
//        });
//    }
//
//    public void generateTitle(String tags) {
//        String question = "Take all " + tags + "to consideration and generate 5 most relevant titles";
//        callAPI(question, true);
//    }

    public String parseJsonAndGetFirstTwoEnValues(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject resultObject = jsonObject.getJSONObject("result");
            JSONArray tagsArray = resultObject.getJSONArray("tags");

            StringBuilder enValues = new StringBuilder();
            for (int i = 0; i < tagsArray.length(); i++) {
                if (i >= 3) {
                    break;
                }
                JSONObject tagObject = tagsArray.getJSONObject(i).getJSONObject("tag");
                String enValue = tagObject.optString("en");
                if (enValue != null && !enValue.isEmpty()) {
                    enValues.append(enValue).append(", ");
                }
            }

            return enValues.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return ""; // Return an empty string in case of any JSON parsing error
        }
    }

    private File convertBitmapToFile(Bitmap bitmap) {
        try {
            File file = new File(getCacheDir(), "temp_image.jpg");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    void addToChat(String message, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message, sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    void addToChat(Bitmap image, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(image, sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    void addResponse(String response) {
        messageList.remove(messageList.size() - 1);
        addToChat(response, Message.SENT_BY_BOT);
    }


    Request getApiRequest(String question) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "text-davinci-003");
            jsonBody.put("prompt", question);
            jsonBody.put("max_tokens", 4000);
            jsonBody.put("temperature", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization", "Bearer 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")
                .post(body)
                .build();

        return request;
    }

    private void isWorkInstructionPresent(String question) {
        if(workInstructionList == null){
            Toast.makeText(WIChatActivity.this, "WI LIST is null", Toast.LENGTH_SHORT).show();
            return ;
        }
        String questionJSON = "{\"question\":" + "\""+ question + "\"" + "}";

        String input = questionJSON + "\n" + toJSON(workInstructionList);
        String output = "[\n" +
                "{\n" +
                "    \"CONFIDENCE_SCORE\": \"\",\n" +
                "    \"STEP\": \"\",\n" +
                "    \"INSTRUCTIONS\":\"\"\n" +
                "  }\n" +
                "]";
        String task =
                "1. Use the provided Above JSON data and Extract all the 'STEP' and 'INSTRUCTIONS' related to the question from the JSON. \n" +
                "2. Calculate the confidence score for each 'STEP' and 'INSTRUCTIONS', in the entire JSON, based on \"HOW WELL IT ANSWERS THE QUESTION\".\n" +
                "3. Return the json array in the below \"Output Format\". (No code or explanation is needed, return only the output in the below format).";

        String prompt = new PromptManager().generatePrompt(task, input, output);

        callAPI(prompt, false);
//        Request request = getApiRequest(prompt);
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                addResponse("Failed to load response due to " + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                if(response.isSuccessful()){
//                    JSONObject jsonObject = null;
//                    try{
//                        jsonObject = new JSONObject(response.body().string());
//                        Toast.makeText(WIChatActivity.this, jsonObject.toString(), Toast.LENGTH_SHORT).show();
//                        addResponse(jsonObject.toString());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        });
    }

    void generateImage(String prompt) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("prompt", prompt);
            jsonBody.put("n", 1);
            jsonBody.put("size", "1024x1024");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/images/generations")
                .header("Authorization", "Bearer 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")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("CurrentDebugg", "Error while image generation: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray dataArray = jsonObject.getJSONArray("data");

                        if (dataArray != null && dataArray.length() > 0) {
                            JSONObject dataObject = dataArray.getJSONObject(0);
                            String imageUrl = dataObject.optString("url");
                            RequestOptions options = new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC); // Caching strategy for the image

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(WIChatActivity.this)
                                            .asBitmap()
                                            .load(imageUrl)
                                            .apply(options)
                                            .into(new BitmapImageViewTarget(uploadImage) {
                                                @Override
                                                public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                                                    Log.d("CurrentDebugg", bitmap.toString());
//                                                    uploadImage.setVisibility(View.VISIBLE);
//                                                    uploadImage.setImageBitmap(bitmap);
                                                }
                                            });
                                }
                            });
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        });
    }

    void callAPI(String question, Boolean isTitle) {
        //okhttp
        if (!isTitle) {
            messageList.add(new Message("Typing... ", Message.SENT_BY_BOT));
//            messageAdapter.notifyDataSetChanged();
        }
        client.newCall(getApiRequest(question)).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response due to " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getString("text");
                        if (isTitle) {
                            String[] titles = generatePoints(result);
                            for (String title : titles) {
                                if (title.length() > 2) {
                                    addToChat(title, Message.SENT_AS_PROMPT);
                                }
                            }

                        } else {
                            addResponse(result.trim());
                            Log.d("CurrentDebugg", "RESPONSE: " + response.toString());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    addResponse("Failed to load response due to " + response.body().string());
                }
            }
        });
    }

    public String[] generatePoints(String originalString) {
        // Split the original string into points using some delimiter (e.g., newline)
        Log.d("Original", originalString);
        String[] pointsArray = originalString.split("\n");

        // Ensure that there are at least 5 points in the array
        for (String x : pointsArray) {
            Log.d("Original PointsArray", x);
        }

        return pointsArray;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        messageEditText.setVisibility(View.GONE);
        welcomeTextView.setVisibility(View.GONE);
        isImage = true;
        if (requestCode == 1 && resultCode == RESULT_OK) {
            filepath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filepath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                uploadImage.setImageBitmap(bitmap);
                enableAttachedView();

            } catch (Exception ex) {
                Log.d("UploadError", ex.getMessage());
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void enableAttachedView() {
        imgAttachedIv.setVisibility(View.VISIBLE);
        textAttachedTv.setVisibility(View.VISIBLE);
    }

    private void disableAttachedView() {
        imgAttachedIv.setVisibility(View.GONE);
        textAttachedTv.setVisibility(View.GONE);
    }

    public Bitmap getBitmapFromURL(String url){
        final Bitmap[] res = {null};
        Glide.with(WIChatActivity.this)
                .asBitmap()
                .load(url)
                .transition(BitmapTransitionOptions.withCrossFade())
                .into(new BitmapImageViewTarget(uploadImage) {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        super.onResourceReady(resource, transition);
                        // The 'resource' parameter contains the loaded Bitmap.
                        // Use the Bitmap as needed.
                        res[0] = resource;
                    }
                });
        return res[0];
    }
    public void uploadImageAndGetUploadId(File imageFile) {
        // API credentials
        final String[] uploadId = new String[1];
        String credentialsToEncode = "acc_c3ee5e8317eed75" + ":" + "ca0d5f804945a16d8da6cbd8725cbaa6";
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString(credentialsToEncode.getBytes(StandardCharsets.UTF_8));

        // API endpoint
        String endpoint = "https://api.imagga.com/v2/uploads";

        // Create the OkHttp client
        OkHttpClient client = new OkHttpClient();
        progressDialog = new MyProgressDialog(this);
        progressDialog.show();

        // Create the request body with the image file
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", imageFile.getName(),
                        RequestBody.create(MediaType.parse("image/jpeg"), imageFile))
                .build();

        // Create the POST request
        Request request = new Request.Builder()
                .url(endpoint)
                .header("Authorization", basicAuth)
                .post(requestBody)
                .build();

        // Perform the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle the failure case here
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Check if the response is successful
                if (response.isSuccessful()) {
                    // Handle the successful response here
                    String responseBody = response.body().string();
                    // Parse the response to get the upload ID
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        uploadId[0] = jsonObject.getJSONObject("result").getString("upload_id");
                        // Do something with the upload ID
                        Log.d("Upload ID: ", uploadId[0]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // I have upload id here
                    getTags(uploadId[0]);
                    progressDialog.dismiss();
                } else {
                    // Handle the error case here
                    System.out.println("Error: " + response.code() + " - " + response.message());
                    progressDialog.dismiss();
                }
            }
        });

    }
}