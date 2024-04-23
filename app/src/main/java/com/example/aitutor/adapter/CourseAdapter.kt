package com.example.aitutor.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aitutor.R
import com.example.aitutor.activities.CourseChaptersActivity
import com.example.aitutor.model.Course
import com.google.android.material.card.MaterialCardView

class CourseAdapter(private val context: Context, private val mList: List<Course>) : RecyclerView.Adapter<CourseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_row_course_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val course = mList[position]
        holder.txtCourseTopic.text = course.topic
        holder.singleRowLayout.setOnClickListener {
            val intent = Intent(context, CourseChaptersActivity::class.java)
            intent.putExtra("courseIndex",position)
            context.startActivity(intent)
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val txtCourseTopic: TextView = itemView.findViewById(R.id.txtCourseNameSingleRow)
        val singleRowLayout: MaterialCardView = itemView.findViewById(R.id.singleRowCourseMainLayout)
    }
}
