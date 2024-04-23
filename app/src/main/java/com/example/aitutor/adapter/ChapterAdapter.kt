package com.example.aitutor.adapter

import com.example.aitutor.activities.ChapterContentActivity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aitutor.R
import com.example.aitutor.model.Chapter
import com.google.android.material.card.MaterialCardView

class ChapterAdapter(private val context: Context, private val courseIndex: Int, private val mList: List<Chapter>) : RecyclerView.Adapter<ChapterAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_row_chapter_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val chapter = mList[position]
        holder.txtChapterTopic.text = chapter.name
        holder.txtChapterDescription.text = chapter.description
        holder.singleRowLayout.setOnClickListener {
            val intent = Intent(context, ChapterContentActivity::class.java)
            intent.putExtra("courseIndex",courseIndex)
            intent.putExtra("chapterIndex",position)
            context.startActivity(intent)
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val txtChapterTopic: TextView = itemView.findViewById(R.id.txtChapterNameSingleRow)
        val txtChapterDescription: TextView = itemView.findViewById((R.id.txtChapterDescriptionSingleRow))
        val singleRowLayout: MaterialCardView = itemView.findViewById(R.id.singleRowChapterMainLayout)
    }
}
