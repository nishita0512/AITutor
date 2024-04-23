package com.example.aitutor.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aitutor.Constants
import com.example.aitutor.model.Course
import com.example.aitutor.adapter.CourseAdapter
import com.example.aitutor.databinding.ActivityMainBinding
import com.example.aitutor.model.Chapter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("courses",MODE_PRIVATE)
        val gson = GsonBuilder().create()

        Constants.courses.clear()
        val allEntries: Map<String, *> = sharedPreferences.all
        val type = object : TypeToken<ArrayList<Chapter>>() {}.type
        for ((key, value) in allEntries) {
            Constants.courses.add(Course(key,gson.fromJson(value.toString(),type)))
            Log.d("map values ", key + " : " + value.toString())
        }

        binding.btnAddMainActivity.setOnClickListener {
            startActivity(Intent(this, CreateCourseActivity::class.java))
        }

        binding.coursesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.coursesRecyclerView.adapter = CourseAdapter(this,Constants.courses)

        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT) {
                finish()
            }
        } else {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            })
        }

    }
}