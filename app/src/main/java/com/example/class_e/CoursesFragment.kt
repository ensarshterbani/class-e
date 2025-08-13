package com.example.class_e

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class CoursesFragment : Fragment() {

    private lateinit var coursesRecyclerView: RecyclerView
    private lateinit var coursesAdapter: CoursesAdapter
    private lateinit var fabMain: FloatingActionButton
    private lateinit var fabCreateCourse: FloatingActionButton
    private lateinit var fabJoinCourse: FloatingActionButton
    private var isFabOpen = false

    private lateinit var db: FirebaseFirestore
    private lateinit var courses: MutableList<Course>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_courses, container, false)
        coursesRecyclerView = view.findViewById(R.id.coursesRecyclerView)
        coursesRecyclerView.layoutManager = LinearLayoutManager(context)

        courses = mutableListOf()
        coursesAdapter = CoursesAdapter(courses)
        coursesRecyclerView.adapter = coursesAdapter

        db = FirebaseFirestore.getInstance()

        fabMain = view.findViewById(R.id.fab_main)
        fabCreateCourse = view.findViewById(R.id.fab_create_course)
        fabJoinCourse = view.findViewById(R.id.fab_join_course)

        fabMain.setOnClickListener {
            if (isFabOpen) {
                closeFabMenu()
            } else {
                openFabMenu()
            }
        }

        fabCreateCourse.setOnClickListener {
            // Navigate to CreateCourseFragment
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, CreateCourseFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        fabJoinCourse.setOnClickListener {
            // Navigate to JoinCourseFragment
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, JoinCourseFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        loadCourses()

        return view
    }

    private fun openFabMenu() {
        isFabOpen = true
        fabCreateCourse.visibility = View.VISIBLE
        fabJoinCourse.visibility = View.VISIBLE
    }

    private fun closeFabMenu() {
        isFabOpen = false
        fabCreateCourse.visibility = View.GONE
        fabJoinCourse.visibility = View.GONE
    }

    private fun loadCourses() {
        db.collection("courses")
            .get()
            .addOnSuccessListener { result ->
                courses.clear()
                for (document in result) {
                    val course = document.toObject(Course::class.java)
                    courses.add(course)
                }
                coursesAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }
}