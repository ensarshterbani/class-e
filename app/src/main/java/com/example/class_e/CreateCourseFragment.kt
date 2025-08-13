package com.example.class_e

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class CreateCourseFragment : Fragment() {

    private lateinit var courseNameEditText: TextInputEditText
    private lateinit var courseNameInputLayout: TextInputLayout
    private lateinit var assistantUserIDEditText: TextInputEditText
    private lateinit var gradingSchemeContainer: LinearLayout
    private lateinit var addGradingSchemeButton: Button
    private lateinit var createCourseButton: Button
    private var gradingSchemeAdded = false

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_course, container, false)

        courseNameEditText = view.findViewById(R.id.courseNameEditText)
        courseNameInputLayout = view.findViewById(R.id.courseNameInputLayout)
        assistantUserIDEditText = view.findViewById(R.id.assistantUserIDEditText)
        gradingSchemeContainer = view.findViewById(R.id.gradingSchemeContainer)
        addGradingSchemeButton = view.findViewById(R.id.addGradingSchemeButton)
        createCourseButton = view.findViewById(R.id.createCourseButton)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        addGradingSchemeButton.setOnClickListener {
            addGradingSchemeItem()
            gradingSchemeAdded = true
        }

        createCourseButton.setOnClickListener {
            createCourse()
        }

        return view
    }

    private fun addGradingSchemeItem() {
        val gradingSchemeItemView = LayoutInflater.from(context).inflate(R.layout.item_grading_scheme, gradingSchemeContainer, false)
        val pointsEditText: EditText = gradingSchemeItemView.findViewById(R.id.pointsEditText)
        val gradeEditText: EditText = gradingSchemeItemView.findViewById(R.id.gradeEditText)
        val pointsInputLayout: TextInputLayout = gradingSchemeItemView.findViewById(R.id.pointsInputLayout)
        val gradeInputLayout: TextInputLayout = gradingSchemeItemView.findViewById(R.id.gradeInputLayout)

        pointsEditText.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                pointsInputLayout.error = "Points are required"
            } else {
                pointsInputLayout.error = null
            }
        }

        gradeEditText.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                gradeInputLayout.error = "Grade is required"
            } else {
                gradeInputLayout.error = null
            }
        }

        gradingSchemeContainer.addView(gradingSchemeItemView)
    }

    private fun getGradingScheme(): List<GradingSchemeItem> {
        val gradingScheme = mutableListOf<GradingSchemeItem>()
        for (i in 0 until gradingSchemeContainer.childCount) {
            val itemView = gradingSchemeContainer.getChildAt(i)
            val pointsEditText: EditText = itemView.findViewById(R.id.pointsEditText)
            val gradeEditText: EditText = itemView.findViewById(R.id.gradeEditText)
            val points = pointsEditText.text.toString()
            val grade = gradeEditText.text.toString()

            if (points.isEmpty() || grade.isEmpty()) {
                return emptyList()
            }

            gradingScheme.add(GradingSchemeItem(points, grade))
        }
        return gradingScheme
    }

    private fun getCurrentAcademicYear(): String {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val nextYear = currentYear + 1
        return "$currentYear-$nextYear"
    }

    private fun createCourse() {
        val courseName = courseNameEditText.text.toString()
        val assistantUserID = assistantUserIDEditText.text.toString()

        if (courseName.isEmpty()) {
            courseNameInputLayout.error = "Course name is required"
            return
        } else {
            courseNameInputLayout.error = null
        }

        if (!gradingSchemeAdded) {
            Toast.makeText(context, "Please add at least one grading scheme item", Toast.LENGTH_SHORT).show()
            return
        }

        val gradingScheme = getGradingScheme()
        if (gradingScheme.isEmpty()) {
            return
        }

        val user = auth.currentUser
        if (user != null) {
            val academicYear = getCurrentAcademicYear()
            val course = Course(
                courseName = courseName,
                professorName = user.displayName ?: "Unknown",
                assistantProfessorName = assistantUserID,
                academicYear = academicYear,
                numberOfStudents = 0,
                gradingScheme = gradingScheme,
                grades = emptyList(),
                assignments = emptyList(),
                professorProfile = Profile(
                    userID = user.uid,
                    firstName = user.displayName ?: "Unknown",
                    lastName = "",
                    email = user.email ?: "",
                    profileImage = R.drawable.profile_image1,
                    courses = emptyList()
                ),
                assistantProfessorProfile = Profile(
                    userID = assistantUserID,
                    firstName = "",
                    lastName = "",
                    email = "",
                    profileImage = R.drawable.profile_image1,
                    courses = emptyList()
                )
            )

            db.collection("courses")
                .add(course)
                .addOnSuccessListener { documentReference ->
                    val courseID = documentReference.id
                    db.collection("courses").document(courseID)
                        .update("courseID", courseID)

                            Toast.makeText(context, "Course created successfully", Toast.LENGTH_SHORT).show()
                            navigateToCoursesFragment()

                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to create course: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun navigateToCoursesFragment() {
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, CoursesFragment())
        transaction.commit()
    }
}