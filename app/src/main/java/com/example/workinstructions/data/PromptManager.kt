package com.example.workinstructions.data

class PromptManager {

    val GPT_API_KEY = "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"

    fun generatePrompt(prompt:String, input: String, output: String): String{
        return "$input \n $prompt \n $output"
    }

    fun getPrompt(header: String, imgTags: String): String{
        val question = "Generate work instruction to give steps to perform or fix a specific task. Consider" + header + " and " + imgTags + " pointers to get context on the task to be performed. Generate steps for industrial use cases. Keep it professional. Assume you are supervisor writing the steps for mechanic/operator. Also generate keyworks related to every step. Give the steps in JSON format with key 'steps' and key 'keyword' on each step.";
        return question
    }
}