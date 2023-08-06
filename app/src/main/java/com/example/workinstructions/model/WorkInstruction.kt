package com.example.workinstructions.model


data class WorkInstruction(
    val _id: String?,
    val header: String?,
    val steps: List<Step>
) {

}
