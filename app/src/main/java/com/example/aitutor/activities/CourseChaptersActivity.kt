package com.example.aitutor.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aitutor.util.Constants
import com.example.aitutor.adapter.ChapterAdapter
import com.example.aitutor.databinding.ActivityCourseChaptersBinding

class CourseChaptersActivity : AppCompatActivity() {

    lateinit var binding: ActivityCourseChaptersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseChaptersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val courseIndex = intent.getIntExtra("courseIndex",0)

        binding.txtTopTitleCourse.text = Constants.courses[courseIndex].topic

        val chapters = Constants.courses[courseIndex].chapters
        binding.chaptersRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chaptersRecyclerView.adapter = ChapterAdapter(this,courseIndex,chapters)

    }

}