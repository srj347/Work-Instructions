package com.example.workinstructions.data

import com.example.workinstructions.model.Step
import com.example.workinstructions.model.WorkInstruction
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.lang.StringBuilder

class WIRepository {
    private val COLLECTION_INNOVAPPTIVE = "innovapptive"
    private val firestore = FirebaseFirestore.getInstance()
    private val collectionRef = firestore.collection(COLLECTION_INNOVAPPTIVE)

    // Define the field names for the data in the Firestore documents
    val FIELD_HEADER = "header"
    val FIELD_STEPS = "steps"
    val FIELD_STEP_NAME = "STEP"
    val FIELD_STEP_INSTRUCTIONS = "INSTRUCTIONS"
    val FIELD_STEP_HINTS = "HINTS"
    val FIELD_STEP_WARNINGS = "WARNINGS"
    val FIELD_STEP_REACTION_PLAN = "REACTION_PLAN"

    fun fetchAllWorkInstruction(onSuccess: (List<WorkInstruction>) -> Unit, onFailure: (Exception) -> Unit) {
        collectionRef.get()
            .addOnSuccessListener { result ->
                val workInstructions = mutableListOf<WorkInstruction>()

                for (document in result) {
                    val header = document.getString(FIELD_HEADER)
                    val stepsData = document.getString(FIELD_STEPS)

                    if (header != null && stepsData != null) {
                        val steps = parseStepsFromJson(stepsData)

                        val workInstruction = WorkInstruction(
                            _id = document.id,
                            header = header,
                            steps = steps
                        )
                        workInstructions.add(workInstruction)
                    }
                }

                onSuccess(workInstructions)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    private fun parseStepsFromJson(jsonString: String): List<Step> {
        val gson = Gson()
        return gson.fromJson(jsonString, Array<Step>::class.java).toList()
    }

    fun getStringFromObject(wiList: ArrayList<WorkInstruction>): String {
        var result: StringBuilder = StringBuilder()
        for(wi in wiList){
            result.append("${wi.header}\n")
            for(step in wi.steps){
                result.append("${step.name ?: ""}. ${step.instruction?: ""}. ${step.hint?: ""}. ${step.reactionPlan?: ""} \n")
            }
            result.append("\n")
        }
        return result.toString()
    }
}
