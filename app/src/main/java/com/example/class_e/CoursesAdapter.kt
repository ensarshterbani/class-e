package com.example.class_e

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class CoursesAdapter(private val courses: List<Course>) : RecyclerView.Adapter<CoursesAdapter.CourseViewHolder>() {

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseNameTextView: TextView = itemView.findViewById(R.id.courseNameTextView)
        val professorNameTextView: TextView = itemView.findViewById(R.id.professorNameTextView)
        val assistantProfessorNameTextView: TextView = itemView.findViewById(R.id.assistantProfessorNameTextView)
        val courseIDTextView: TextView = itemView.findViewById(R.id.courseIDTextView)
        val academicYearTextView: TextView = itemView.findViewById(R.id.academicYearTextView)
        val numberOfStudentsTextView: TextView = itemView.findViewById(R.id.numberOfStudentsTextView)
        val gradingSchemeTable: TableLayout = itemView.findViewById(R.id.gradingSchemeTable)
        val expandableSection: View = itemView.findViewById(R.id.expandableSection)
        val professorProfileImageView: ImageView = itemView.findViewById(R.id.professorProfileImageView)
        val assistantProfessorProfileImageView: ImageView = itemView.findViewById(R.id.assistantProfessorProfileImageView)
        val enrolledStudentsTextView: TextView = itemView.findViewById(R.id.enrolledStudentsTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]
        holder.courseNameTextView.text = course.courseName
        holder.professorNameTextView.text = course.professorName
        holder.assistantProfessorNameTextView.text = course.assistantProfessorName
        holder.courseIDTextView.text = "Course ID: ${course.courseID}"
        holder.academicYearTextView.text = course.academicYear
        holder.numberOfStudentsTextView.text = "Enrolled Students: ${course.numberOfStudents}"

        holder.professorProfileImageView.setImageResource(course.professorProfile.profileImage)
        holder.assistantProfessorProfileImageView.setImageResource(course.assistantProfessorProfile.profileImage)

        holder.gradingSchemeTable.removeViews(1, holder.gradingSchemeTable.childCount - 1)

        for (scheme in course.gradingScheme) {
            val tableRow = TableRow(holder.itemView.context)

            val componentTextView = TextView(holder.itemView.context)
            componentTextView.text = scheme.range
            componentTextView.setPadding(8, 8, 8, 8)
            componentTextView.gravity = android.view.Gravity.CENTER

            val weightTextView = TextView(holder.itemView.context)
            weightTextView.text = scheme.grade
            weightTextView.setPadding(8, 8, 8, 8)
            weightTextView.gravity = android.view.Gravity.CENTER

            tableRow.addView(componentTextView)
            tableRow.addView(weightTextView)
            holder.gradingSchemeTable.addView(tableRow)
        }

        holder.itemView.setOnClickListener {
            if (holder.expandableSection.visibility == View.GONE) {
                holder.expandableSection.visibility = View.VISIBLE
            } else {
                holder.expandableSection.visibility = View.GONE
            }
        }

        holder.professorProfileImageView.setOnClickListener {
            showProfilePopup(holder.itemView, course.professorProfile.userID)
        }

        holder.assistantProfessorProfileImageView.setOnClickListener {
            showProfilePopup(holder.itemView, course.assistantProfessorProfile.userID)
        }

        holder.enrolledStudentsTextView.setOnClickListener {
            val activity = it.context as FragmentActivity
            val fragment = EnrolledStudentsFragment()
            val bundle = Bundle()
            bundle.putString("courseId", course.courseID)
            fragment.arguments = bundle

            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount() = courses.size

    private fun showProfilePopup(view: View, userID: String) {
        val inflater = LayoutInflater.from(view.context)
        val popupView = inflater.inflate(R.layout.popup_profile, null)

        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.isOutsideTouchable = true

        val profileImageView: ImageView = popupView.findViewById(R.id.profileImageView)
        val userIDTextView: TextView = popupView.findViewById(R.id.userIDTextView)
        val firstNameTextView: TextView = popupView.findViewById(R.id.firstNameTextView)
        val lastNameTextView: TextView = popupView.findViewById(R.id.lastNameTextView)
        val emailTextView: TextView = popupView.findViewById(R.id.emailTextView)
        val coursesTextView: TextView = popupView.findViewById(R.id.coursesTextView)
        val requestConsultationButton: Button = popupView.findViewById(R.id.requestConsultationButton)

        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userID)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val profile = document.toObject(Profile::class.java)
                    if (profile != null) {
                        profileImageView.setImageResource(profile.profileImage)
                        userIDTextView.text = "User ID: ${profile.userID}"
                        firstNameTextView.text = "First Name: ${profile.firstName}"
                        lastNameTextView.text = "Last Name: ${profile.lastName}"
                        emailTextView.text = "Email: ${profile.email}"
                        coursesTextView.text = "Courses: ${profile.courses.joinToString(", ")}"
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(view.context, "Failed to retrieve user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        requestConsultationButton.setOnClickListener {
            Toast.makeText(view.context, "Consultation email sent", Toast.LENGTH_SHORT).show()
        }

        // Dim the background
        val container = view.rootView as ViewGroup
        val dimView = View(view.context)
        dimView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dimView.setBackgroundColor(Color.BLACK)
        dimView.alpha = 0.5f
        container.addView(dimView)

        popupWindow.setOnDismissListener {
            container.removeView(dimView)
        }

        popupWindow.showAtLocation(view, android.view.Gravity.CENTER, 0, 0)
    }
}