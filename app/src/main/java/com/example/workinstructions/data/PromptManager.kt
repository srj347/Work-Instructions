package com.example.workinstructions.data

class PromptManager {
    fun generatePrompt(prompt:String, input: String, output: String): String{
        return "$input \n $prompt \n $output"
    }

    fun getPrompt(header: String, imgTags: String): String{
        val question = "Generate work instruction to give steps to perform or fix a specific task. Consider" + header + " and " + imgTags + " pointers to get context on the task to be performed. Generate only 5 task steps for industrial use cases. Keep it concise. Assume you are supervisor writing the steps for mechanic/operator. Present the steps in JSON format with key 'steps'.";
        return question
    }
}