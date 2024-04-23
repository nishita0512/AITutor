package com.example.aitutor.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aitutor.Constants
import com.example.aitutor.R
import com.example.aitutor.databinding.ActivityChapterContentBinding

class ChapterContentActivity : AppCompatActivity() {

    lateinit var binding: ActivityChapterContentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChapterContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val courseIndex = intent.getIntExtra("courseIndex",0)
        val chapterIndex = intent.getIntExtra("chapterIndex",0)

        binding.txtChapterContent.text = Constants.courses[courseIndex].chapters[chapterIndex].description

    }
}