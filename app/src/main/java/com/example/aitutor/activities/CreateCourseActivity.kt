package com.example.aitutor.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.aitutor.util.Constants
import com.example.aitutor.databinding.ActivityCreateChapterBinding
import com.example.aitutor.model.Chapter
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CreateCourseActivity : AppCompatActivity() {

    lateinit var binding: ActivityCreateChapterBinding

    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateChapterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()
        imageCapture = ImageCapture.Builder().build()
        startCamera()

        binding.btnCreateCourse.setOnClickListener {
            binding.btnCreateCourse.visibility = View.INVISIBLE
            binding.btnCreateCourseProgressBar.visibility = View.VISIBLE
            getAge()
        }

    }

    fun getChapters(age: Int){
        val sharedPreferences = getSharedPreferences("courses",MODE_PRIVATE)
        val gson = GsonBuilder().create()

        val topic = binding.edtTxtTopic.text.toString().trim()

        val prompt = "Create few chapters for a person of age $age to learn the basics of $topic in the following format:\n" +
                "Chapter1Name~ChapterDescription;;" +
                "Chapter1Name~ChapterDescription;;" +
                "." +
                "." +
                "so on.\n" +
                "The response should only contain the content in given format, no extra information."

        CoroutineScope(Dispatchers.IO).launch {
            val response = Constants.generativeModel.generateContent(prompt).text.toString().trimIndent()
//                val response = "\"Welcome to Machine Learning!\": \"This chapter introduces you to the world of Machine Learning, what it is, why it's important, and real-world examples of how it's used in our everyday lives.\"\n" +
//                        "\"Learning from Data\": \"This chapter dives into the core concept of Machine Learning: learning from data. We'll explore different types of data, how it's collected and prepared for use in machine learning models.\"\n" +
//                        "\"Supervised vs. Unsupervised Learning\": \"Here, we'll uncover the two main categories of Machine Learning: supervised learning, where the data is labeled, and unsupervised learning, where the data has no predefined labels.\"\n" +
//                        "\"Common Machine Learning Algorithms\": \"This chapter introduces you to some of the most popular machine learning algorithms, including linear regression, decision trees, and k-means clustering. We'll explore what each algorithm does and when it might be used.\"\n" +
//                        "\"Model Evaluation and Bias\": \"In this chapter, we'll discuss how to evaluate the performance of a machine learning model and identify potential biases that can creep into the training data and influence the model's results.\"\n" +
//                        "\"Beyond the Basics\": \"This chapter serves as a springboard for further exploration. We'll touch on advanced topics like deep learning, neural networks, and the exciting future possibilities of machine learning.\""

//                val response = "1. \"Chapter1: Introduction to Driving\" : \"This chapter covers the basics of driving, including how to start a car, how to control the steering wheel, and how to use the gas and brake pedals.\"\n" +
//                        "\n" +
//                        "2. \"Chapter2: Basic Car Controls\" : \"This chapter discusses the various controls found in a car, including the steering wheel, the dashboard, and the gear shift.\"\n" +
//                        "\n" +
//                        "3. \"Chapter3: Defensive Driving Techniques\" : \"This chapter teaches drivers how to anticipate and avoid potential hazards, such as other cars, pedestrians, and animals.\"\n" +
//                        "\n" +
//                        "4. \"Chapter4: Driving in Adverse Conditions\" : \"This chapter provides tips for driving safely in bad weather, such as rain, snow, and fog.\"\n" +
//                        "\n" +
//                        "5. \"Chapter5: Maintaining Your Vehicle\" : \"This chapter covers the basics of car maintenance, including how to check tire pressure, change a flat tire, and add oil.\""

//                val response = "Chapter1: Introduction to Programming,Overview of programming concepts and its applications;Chapter2: Variables and Data Types,Understanding variables, data types, and their usage;Chapter3: Operators and Expressions,Exploring different types of operators and their precedence, along with expressions evaluation;Chapter4: Control Flow,Mastering conditional statements (if-else, switch-case) and loop structures (for, while, do-while);Chapter5: Functions,Delving into functions, their definitions, parameters, and return values;Chapter6: Arrays,Working with arrays, including their declaration, initialization, and access mechanisms;Chapter7: Strings,Understanding strings, their manipulation techniques, and string-related functions;Chapter8: Object-Oriented Programming Concepts,Introducing OOP concepts like classes, objects, inheritance, and polymorphism;Chapter9: Input and Output,Managing input and output operations, including file handling basics;Chapter10: Debugging and Error Handling,Learning techniques for debugging code, identifying errors, and handling exceptions."

            val chapterList = parseChapters(response)

            val spEditor = sharedPreferences.edit()
            spEditor.putString(topic,gson.toJson(chapterList))
            spEditor.apply()
            Log.d("Gemini Response", response)
            Log.d("Added to Shared Pref",  topic + gson.toJson(chapterList))

            startActivity(Intent(this@CreateCourseActivity,MainActivity::class.java))

            runOnUiThread {
                binding.btnCreateCourse.visibility = View.VISIBLE
                binding.btnCreateCourseProgressBar.visibility = View.GONE
            }
        }
    }

    fun parseChapters(chaptersString: String): ArrayList<Chapter> {
        val chaptersList = ArrayList<Chapter>()
        val chapters = chaptersString.split(";;")
        println("Chapters Size: "+chapters.size)

        for (chapter in chapters) {
            if(chapter.isNotBlank()) {
                val parts = chapter.split("~")
                println("parts Size: "+parts.size + chapter)
                if (parts.size == 2) {
                    val chapterName = parts[0].trim('*').trim('*').trim('"')
                    val chapterDescription = parts[1].trim('*').trim('*').trim('"')
                    chaptersList.add(Chapter(chapterName, chapterDescription, ""))
                }
            }
        }

        return chaptersList
    }

    private fun getAge(){
        Log.d("Get Age", "Taking Picture")
        if(!cameraExecutor.isShutdown && !cameraExecutor.isTerminated){
            takePhoto()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }


            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (ex: Exception) {
                Toast.makeText(this@CreateCourseActivity,"Unable to Use Camera", Toast.LENGTH_LONG).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            cacheDir,
            "isConfused.jpg"
        )

        if(!photoFile.exists()){
            photoFile.createNewFile()
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Log.d("Picture Error",exception.message.toString())
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    if(outputFileResults.savedUri != null){
                        val savedUri = outputFileResults.savedUri
                        Log.d("Picture Saved",savedUri.toString())
//                        Log.d("Picture File Exists")
                        getAgeAPICall(savedUri!!)
                    }
                    else{
                        Toast.makeText(this@CreateCourseActivity,"Unable to save picture", Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    private fun getAgeAPICall(uri: Uri){
        val client = OkHttpClient()

        val mediaType = "image/jpeg".toMediaTypeOrNull()
        val file = File(uri.path.toString())

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", "image.jpg", file.asRequestBody(mediaType))
            .build()

        val request = Request.Builder()
            .url("${Constants.API_URL}/predictage")
            .post(requestBody)
            .build()

        Log.d("My Request",request.body.toString())

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string().toString()
                    Log.d("Response Age",responseData)
                    if(responseData.isNotBlank()){
                        getChapters(responseData.trim().toInt())
                    }
                }
            }
        })
    }

}