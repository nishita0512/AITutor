package com.example.aitutor.util

import com.example.aitutor.model.Course
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow

object Constants {
    val CAMERA_PERMISSION_CODE = 110
    val GEMINI_API_KEY = "<GEMINI_API_KEY>"
    val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = GEMINI_API_KEY
    )
    val API_URL = "<API_URL>"
    var courses = ArrayList<Course>()
    var isUserConfused = MutableStateFlow(false)
    var confusionDialogVisible = false
    var confusionOverride = false
}