package com.example.workinstructions.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Step(
    @field:SerializedName("STEP")
    val name: String?,
    @field:SerializedName("INSTRUCTIONS")
    val instruction: String?,
    @field:SerializedName("HINTS")
    val hint: String?,
    @field:SerializedName("REACTION_PLAN")
    val reactionPlan: String?,
    @field:SerializedName("WARNINGS")
    val warning: String?,
    @field:SerializedName("IMAGE")
    val image: String?,
    @field:SerializedName("IMAGETAGS")
    val imageTags: String?
): Serializable {
}