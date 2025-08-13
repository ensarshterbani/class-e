package com.example.class_e

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        holder.authorName.text = post.author
        holder.postTitle.text = post.title
        holder.postDate.text = post.date // Use the date as a string
        holder.courseName.text = post.courseName

        holder.profileImage.setImageResource(post.profileImage)

        holder.itemView.setOnClickListener {
            val activity = it.context as FragmentActivity
            val fragment = PostDetailFragment()

            val bundle = Bundle()
            bundle.putString("author", post.author)
            bundle.putString("title", post.title)
            bundle.putString("content", post.content)
            bundle.putString("date", post.date)
            bundle.putInt("profileImage", post.profileImage)
            bundle.putString("courseName", post.courseName)
            fragment.arguments = bundle

            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val authorName: TextView = itemView.findViewById(R.id.postAuthor)
        val courseName: TextView = itemView.findViewById(R.id.courseName)
        val postTitle: TextView = itemView.findViewById(R.id.postTitle)
        val postDate: TextView = itemView.findViewById(R.id.postDate)
        val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
    }
}