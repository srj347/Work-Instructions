package com.example.workinstructions.model

import com.google.gson.annotations.SerializedName

data class StepRequest(
    @field:SerializedName("STEP")
    val name: String?,
    @field:SerializedName("INSTRUCTIONS")
    val instruction: String?
) {
}