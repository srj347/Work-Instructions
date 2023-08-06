package com.example.workinstructions.data;

import com.example.workinstructions.model.Step;
import com.example.workinstructions.model.WorkInstruction;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RepositoryWI {

    private static final String COLLECTION_INNOVAPPTIVE = "innovapptive";

    private FirebaseFirestore firestore;
    private CollectionReference collectionRef;

    public RepositoryWI() {
        firestore = FirebaseFirestore.getInstance();
        collectionRef = firestore.collection(COLLECTION_INNOVAPPTIVE);
    }

    // Define the field names for the data in the Firestore documents
    private static final String FIELD_HEADER = "header";
    private static final String FIELD_STEPS = "steps";
    private static final String FIELD_STEP_NAME = "STEP";
    private static final String FIELD_STEP_INSTRUCTIONS = "INSTRUCTIONS";
    private static final String FIELD_STEP_HINTS = "HINTS";
    private static final String FIELD_STEP_WARNINGS = "WARNINGS";
    private static final String FIELD_STEP_REACTION_PLAN = "REACTION_PLAN";

    public void fetchAllWorkInstruction(
            final OnSuccessListener<List<WorkInstruction>> onSuccess,
            final OnFailureListener onFailure) {

        collectionRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot result) {
                        List<WorkInstruction> workInstructions = new ArrayList<>();

                        for (DocumentSnapshot document : result) {
                            String header = document.getString(FIELD_HEADER);
                            String stepsData = document.getString(FIELD_STEPS);

                            if (header != null && stepsData != null) {
                                List<Step> steps = parseStepsFromJson(stepsData);

                                WorkInstruction workInstruction = new WorkInstruction(
                                        document.getId(),
                                        header,
                                        steps);
                                workInstructions.add(workInstruction);
                            }
                        }

                        onSuccess.onSuccess(workInstructions);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception exception) {
                        onFailure.onFailure(exception);
                    }
                });
    }

    public static List<Step> parseStepsFromJson(String jsonString) {
        Gson gson = new Gson();
        Step[] stepsArray = gson.fromJson(jsonString, Step[].class);
        return Arrays.asList(stepsArray);
    }
}

