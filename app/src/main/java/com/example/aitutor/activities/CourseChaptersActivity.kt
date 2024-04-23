package com.example.aitutor.activities

import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aitutor.Constants
import com.example.aitutor.adapter.ChapterAdapter
import com.example.aitutor.databinding.ActivityCourseChaptersBinding
import com.example.aitutor.databinding.ActivityCreateChapterBinding

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