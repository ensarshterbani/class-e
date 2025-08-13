package com.example.class_e

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var posts: MutableList<Post>
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hme, container, false)

        posts = mutableListOf()
        db = FirebaseFirestore.getInstance()

        recyclerView = view.findViewById(R.id.homeRecyclerView)
        postAdapter = PostAdapter(posts)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = postAdapter

        loadPosts()

        val fab: FloatingActionButton = view.findViewById(R.id.fab)
        fab.setOnClickListener {
            // Navigate to the CreatePostFragment
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, CreatePostFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return view
    }

    private fun loadPosts() {
        db.collection("posts")
            .orderBy("date", Query.Direction.DESCENDING) // Order posts by date in descending order
            .get()
            .addOnSuccessListener { result ->
                posts.clear()
                for (document in result) {
                    val post = document.toObject(Post::class.java)
                    posts.add(post)
                }
                postAdapter.notifyDataSetChanged() // Notify the adapter to refresh the RecyclerView
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to load posts: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}