package com.example.class_e

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommentsAdapter(private val comments: List<Comment>) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]

        holder.firstName.text = comment.firstName
        holder.lastName.text = comment.lastName
        holder.commentText.text = comment.text
        holder.profileImage.setImageResource(comment.profileImage)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val firstName: TextView = itemView.findViewById(R.id.commentFirstName)
        val lastName: TextView = itemView.findViewById(R.id.commentLastName)
        val commentText: TextView = itemView.findViewById(R.id.commentText)
        val profileImage: ImageView = itemView.findViewById(R.id.commentProfileImage)
    }
}