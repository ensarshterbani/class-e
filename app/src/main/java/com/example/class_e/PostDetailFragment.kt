package com.example.class_e

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class PostDetailFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var comments: MutableList<Comment>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post_detail, container, false)

        val authorName: TextView = view.findViewById(R.id.postAuthor)
        val courseName: TextView = view.findViewById(R.id.courseName)
        val postTitle: TextView = view.findViewById(R.id.postTitle)
        val postContent: TextView = view.findViewById(R.id.postContent)
        val postDate: TextView = view.findViewById(R.id.postDate)
        val profileImage: ImageView = view.findViewById(R.id.profileImage)
        val commentInput: EditText = view.findViewById(R.id.commentInput)
        val addCommentButton: Button = view.findViewById(R.id.addCommentButton)
        val attachmentIcon: ImageView = view.findViewById(R.id.attachmentIcon)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        comments = mutableListOf()

        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView)
        commentsAdapter = CommentsAdapter(comments)
        commentsRecyclerView.layoutManager = LinearLayoutManager(context)
        commentsRecyclerView.adapter = commentsAdapter

        val args = arguments
        if (args != null) {
            authorName.text = args.getString("author")
            courseName.text = args.getString("courseName")
            postTitle.text = args.getString("title")
            postContent.text = args.getString("content")
            postDate.text = args.getString("date")
            profileImage.setImageResource(args.getInt("profileImage"))

            loadComments(args.getString("title") ?: "")
        }

        // Handle comment button click
        addCommentButton.setOnClickListener {
            val commentText = commentInput.text.toString().trim()
            if (commentText.isNotEmpty()) {
                addComment(args?.getString("title") ?: "", commentText)
                commentInput.text.clear()
            } else {
                Toast.makeText(context, "Comment cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle attachment icon click
        attachmentIcon.setOnClickListener {
            // Handle attachment
        }

        return view
    }

    private fun loadComments(postTitle: String) {
        db.collection("posts").whereEqualTo("title", postTitle).get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val postId = result.documents[0].id
                    db.collection("posts").document(postId).collection("comments")
                        .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
                        .get()
                        .addOnSuccessListener { result ->
                            comments.clear()
                            for (document in result) {
                                val comment = document.toObject(Comment::class.java)
                                comments.add(comment)
                            }
                            commentsAdapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(context, "Failed to load comments: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to find post: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addComment(postTitle: String, commentText: String) {
        val user = auth.currentUser
        if (user != null) {
            db.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val firstName = document.getString("firstName") ?: "Anonymous"
                        val lastName = document.getString("lastName") ?: ""
                        val profileImage = document.getLong("profileImage")?.toInt() ?: R.drawable.profile_image1

                        val currentDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                        val comment = Comment(
                            userId = user.uid,
                            firstName = firstName,
                            lastName = lastName,
                            profileImage = profileImage,
                            text = commentText,
                            timestamp = currentDate
                        )

                        db.collection("posts").whereEqualTo("title", postTitle).get()
                            .addOnSuccessListener { result ->
                                if (!result.isEmpty) {
                                    val postId = result.documents[0].id
                                    db.collection("posts").document(postId).collection("comments")
                                        .add(comment)
                                        .addOnSuccessListener {
                                            comments.add(comment)
                                            commentsAdapter.notifyItemInserted(comments.size - 1)
                                            commentsRecyclerView.scrollToPosition(comments.size - 1)
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(context, "Failed to add comment: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Failed to find post: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to retrieve user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}