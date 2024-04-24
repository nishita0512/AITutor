package com.example.aitutor.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aitutor.util.Constants
import com.example.aitutor.adapter.CourseAdapter
import com.example.aitutor.databinding.ActivityMainBinding
import com.example.aitutor.model.Chapter
import com.example.aitutor.model.Course
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkCameraPermission()

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
        binding.coursesRecyclerView.adapter = CourseAdapter(this, Constants.courses)

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

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.CAMERA), Constants.CAMERA_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

}