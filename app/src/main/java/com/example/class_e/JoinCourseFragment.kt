package com.example.class_e

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class JoinCourseFragment : Fragment() {

    private lateinit var courseIDEditText: TextInputEditText
    private lateinit var courseIDInputLayout: TextInputLayout
    private lateinit var joinButton: MaterialButton

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_join_course, container, false)

        courseIDEditText = view.findViewById(R.id.courseIDEditText)
        courseIDInputLayout = view.findViewById(R.id.courseIDInputLayout)
        joinButton = view.findViewById(R.id.joinButton)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        joinButton.setOnClickListener {
            joinCourse()
        }

        return view
    }

    private fun joinCourse() {
        val courseID = courseIDEditText.text.toString().trim()

        if (courseID.isEmpty()) {
            courseIDInputLayout.error = "Course ID is required"
            return
        } else {
            courseIDInputLayout.error = null
        }

        val user = auth.currentUser
        if (user != null) {
            db.collection("courses").document(courseID)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val course = document.toObject(Course::class.java)
                        if (course != null) {
                            val updatedNumberOfStudents = course.numberOfStudents + 1
                            db.collection("courses").document(courseID)
                                .update("numberOfStudents", updatedNumberOfStudents)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Joined course successfully", Toast.LENGTH_SHORT).show()
                                    navigateToCoursesFragment()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Failed to join course: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(context, "Course not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to find course: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun navigateToCoursesFragment() {
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, CoursesFragment())
        transaction.commit()
    }
}