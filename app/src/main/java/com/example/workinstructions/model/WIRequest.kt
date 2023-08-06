package com.example.workinstructions.model

data class WIRequest(
    val header: String?,
    val steps: List<Step>
) {
}