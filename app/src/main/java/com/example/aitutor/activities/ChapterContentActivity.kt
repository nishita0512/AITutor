package com.example.aitutor.activities

import android.R.attr.category
import android.R.attr.data
import android.R.attr.name
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.aitutor.R
import com.example.aitutor.databinding.ActivityChapterContentBinding
import com.example.aitutor.util.Constants
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ChapterContentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChapterContentBinding

    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChapterContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Constants.isUserConfused.value = false
        Constants.confusionOverride = false
        Constants.confusionDialogVisible = false

        cameraExecutor = Executors.newSingleThreadExecutor()
        imageCapture = ImageCapture.Builder().build()
        startCamera()

        val courseIndex = intent.getIntExtra("courseIndex",0)
        val chapterIndex = intent.getIntExtra("chapterIndex",0)

        val content = Constants.courses[courseIndex].chapters[chapterIndex].content
        if(!content.isNullOrBlank() && content.isNotEmpty()) {
            Log.d("Content Fetched",content)
            binding.txtChapterContent.text = Constants.courses[courseIndex].chapters[chapterIndex].content
            checkIfConfused()
        }
        else {
            binding.txtChapterContent.visibility = View.GONE
            binding.chapterContentProgressBar.visibility = View.VISIBLE
            getChapterContent(courseIndex,chapterIndex,false)
        }

        CoroutineScope(Dispatchers.IO).launch {
            Constants.isUserConfused.collect() { confused ->
                if(confused && !Constants.confusionDialogVisible && !Constants.confusionOverride){

                    runOnUiThread {
                        val message = "Are you confused? Want an easier Explanation?"
                        AlertDialog.Builder(this@ChapterContentActivity)
                            .setMessage(message)
                            .setPositiveButton("Yes") { dialog, which ->
                                binding.txtChapterContent.visibility = View.GONE
                                binding.chapterContentProgressBar.visibility = View.VISIBLE
                                getChapterContent(courseIndex,chapterIndex,true)
                                Constants.confusionDialogVisible = false
                                dialog.dismiss()
                            }
                            .setNegativeButton("No") { dialog, which ->
                                Constants.confusionOverride = true
                                Constants.confusionDialogVisible = false
                                dialog.dismiss()
                            }
                            .show()
                    }

                }
            }
        }

    }

    private fun getChapterContent(courseIndex: Int, chapterIndex: Int, easier: Boolean = false){
        val sharedPreferences = getSharedPreferences("courses",MODE_PRIVATE)
        val gson = GsonBuilder().create()
        Log.d("Fetching Response","")

        val courseTopic = Constants.courses[courseIndex].topic
        val chapterName = Constants.courses[courseIndex].chapters[chapterIndex].name
        val chapterDescription = Constants.courses[courseIndex].chapters[chapterIndex].description

        val content = Constants.courses[courseIndex].chapters[chapterIndex].content

        val prompt = if(easier){
            "Simplifiy the following content:\n$content"
        }
        else{
            "Create content for following chapter:\n" +
                    "Course Topic: $courseTopic\n" +
                    "Chapter Name: $chapterName\n" +
                    "Chapter Description: $chapterDescription"
        }

        CoroutineScope(Dispatchers.IO).launch {

            val response = Constants.generativeModel.generateContent(prompt).text.toString()
            Constants.courses[courseIndex].chapters[chapterIndex].content = response.replace("*","")

            val spEditor = sharedPreferences.edit()
            spEditor.putString(courseTopic,gson.toJson(Constants.courses[courseIndex].chapters))
            spEditor.apply()

            runOnUiThread {
                binding.txtChapterContent.visibility = View.VISIBLE
                binding.chapterContentProgressBar.visibility = View.GONE
                binding.txtChapterContent.text = Constants.courses[courseIndex].chapters[chapterIndex].content
                checkIfConfused()
            }

        }
    }

    private fun checkIfConfused(){
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(Runnable {
            Log.d("In Runnable", "Taking Picture")
            if(!cameraExecutor.isShutdown && !cameraExecutor.isTerminated && !Constants.confusionOverride){
                takePhoto()
                checkIfConfused()
            }
        },1000)
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
                Toast.makeText(this@ChapterContentActivity,"Unable to Use Camera",Toast.LENGTH_LONG).show()
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
                        isConfusedAPICall(savedUri!!)
                    }
                    else{
                        Toast.makeText(this@ChapterContentActivity,"Unable to save picture",Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    private fun isConfusedAPICall(uri: Uri){
        val client = OkHttpClient()

        val mediaType = "image/jpeg".toMediaTypeOrNull()
        val file = File(uri.path.toString())

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", "image.jpg", file.asRequestBody(mediaType))
            .build()

        val request = Request.Builder()
            .url("${Constants.API_URL}/isconfused")
            .post(requestBody)
            .build()

        Log.d("My Request",request.body.toString())

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string().toString().replace("\"","").trim()
                    Log.d("Response", "-$responseData-")
                    if(responseData == "Confused"){
//                        Toast.makeText(this@ChapterContentActivity,"Confused...",Toast.LENGTH_LONG).show()
                        Constants.isUserConfused.value = true
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}