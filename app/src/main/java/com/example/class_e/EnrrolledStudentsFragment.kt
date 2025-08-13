package com.example.class_e

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class EnrolledStudentsFragment : Fragment() {

    private lateinit var studentsRecyclerView: RecyclerView
    private lateinit var studentsAdapter: StudentsAdapter
    private lateinit var students: MutableList<Profile>
    private lateinit var db: FirebaseFirestore
    private lateinit var courseId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_enrolled_students, container, false)

        studentsRecyclerView = view.findViewById(R.id.studentsRecyclerView)
        studentsRecyclerView.layoutManager = LinearLayoutManager(context)

        students = mutableListOf()
        studentsAdapter = StudentsAdapter(students)
        studentsRecyclerView.adapter = studentsAdapter

        db = FirebaseFirestore.getInstance()

        courseId = arguments?.getString("courseId") ?: ""

        loadEnrolledStudents()

        return view
    }

    private fun loadEnrolledStudents() {
        db.collection("courses").document(courseId).collection("students")
            .get()
            .addOnSuccessListener { result ->
                students.clear()
                for (document in result) {
                    val student = document.toObject(Profile::class.java)
                    students.add(student)
                }
                studentsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    inner class StudentsAdapter(private val students: List<Profile>) : RecyclerView.Adapter<StudentsAdapter.StudentViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
            return StudentViewHolder(view)
        }

        override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
            val student = students[position]

            holder.firstNameTextView.text = student.firstName
            holder.lastNameTextView.text = student.lastName
            holder.profileImageView.setImageResource(student.profileImage)

            holder.profileImageView.setOnClickListener {
                // Handle profile image click to show grading popup
                showGradingPopup(holder.itemView, student)
            }
        }

        override fun getItemCount(): Int {
            return students.size
        }

        inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val firstNameTextView: TextView = itemView.findViewById(R.id.studentFirstNameTextView)
            val lastNameTextView: TextView = itemView.findViewById(R.id.studentLastNameTextView)
            val profileImageView: ImageView = itemView.findViewById(R.id.studentProfileImageView)
        }

        private fun showGradingPopup(view: View, student: Profile) {
            // Implement the grading popup logic here
        }
    }
}