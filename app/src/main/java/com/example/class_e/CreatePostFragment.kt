package com.example.class_e

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class CreatePostFragment : Fragment() {

    private lateinit var courseNameEditText: TextInputEditText
    private lateinit var courseNameInputLayout: TextInputLayout
    private lateinit var postTitleEditText: TextInputEditText
    private lateinit var postTitleInputLayout: TextInputLayout
    private lateinit var postContentEditText: TextInputEditText
    private lateinit var postContentInputLayout: TextInputLayout
    private lateinit var postButton: Button
    private lateinit var attachmentIcon: ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_post, container, false)

        courseNameEditText = view.findViewById(R.id.courseNameEditText)
        courseNameInputLayout = view.findViewById(R.id.courseNameInputLayout)
        postTitleEditText = view.findViewById(R.id.postTitleEditText)
        postTitleInputLayout = view.findViewById(R.id.postTitleInputLayout)
        postContentEditText = view.findViewById(R.id.postContentEditText)
        postContentInputLayout = view.findViewById(R.id.postContentInputLayout)
        postButton = view.findViewById(R.id.postButton)
        attachmentIcon = view.findViewById(R.id.attachmentIcon)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        postButton.setOnClickListener {
            createPost()
        }

        attachmentIcon.setOnClickListener {
            // Handle attachment click
            // For now, just show a message or handle the attachment logic
        }

        return view
    }

    private fun createPost() {
        val courseName = courseNameEditText.text.toString().trim()
        val postTitle = postTitleEditText.text.toString().trim()
        val postContent = postContentEditText.text.toString().trim()

        var isValid = true

        if (courseName.isEmpty()) {
            courseNameInputLayout.error = "Course name is required"
            isValid = false
        } else {
            courseNameInputLayout.error = null
        }

        if (postTitle.isEmpty()) {
            postTitleInputLayout.error = "Post title is required"
            isValid = false
        } else {
            postTitleInputLayout.error = null
        }

        if (postContent.isEmpty()) {
            postContentInputLayout.error = "Post description is required"
            isValid = false
        } else {
            postContentInputLayout.error = null
        }

        if (!isValid) {
            return
        }

        val user = auth.currentUser
        if (user != null) {
            val currentDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            val post = Post(
                author = user.displayName ?: "Anonymous",
                title = postTitle,
                content = postContent,
                date = currentDate,
                profileImage = R.drawable.profile_image1, // Replace with actual user profile image if available
                courseName = courseName
            )

            db.collection("posts")
                .add(post)
                .addOnSuccessListener {
                    Toast.makeText(context, "Post created successfully", Toast.LENGTH_SHORT).show()
                    navigateToHomeFragment()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to create post: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun navigateToHomeFragment() {
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, HomeFragment())
        transaction.commit()
    }
}