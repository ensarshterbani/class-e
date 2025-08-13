package com.example.class_e

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.class_e.SettingsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var userIDTextView: TextView
    private lateinit var firstNameTextView: TextView
    private lateinit var lastNameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var settingsTextView: TextView
    private lateinit var settingsIcon: ImageView
    private lateinit var logoutButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        profileImageView = view.findViewById(R.id.profileImageView)
        userIDTextView = view.findViewById(R.id.userIDTextView)
        firstNameTextView = view.findViewById(R.id.firstNameTextView)
        lastNameTextView = view.findViewById(R.id.lastNameTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        settingsTextView = view.findViewById(R.id.settingsTextView)
        settingsIcon = view.findViewById(R.id.settingsIcon)
        logoutButton = view.findViewById(R.id.logoutButton)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        loadUserProfile()

        settingsTextView.setOnClickListener {
            navigateToSettings()
        }

        settingsIcon.setOnClickListener {
            navigateToSettings()
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        return view
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        if (user != null) {
            db.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userID = document.getString("userID")
                        val firstName = document.getString("firstName")
                        val lastName = document.getString("lastName")
                        val email = document.getString("email")

                        userIDTextView.text = userID
                        firstNameTextView.text = firstName
                        lastNameTextView.text = lastName
                        emailTextView.text = email
                    } else {
                        Toast.makeText(activity, "No such document", Toast.LENGTH_SHORT).show()
                        Log.e("ProfileFragment", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(activity, "Failed to load user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                    Log.e("ProfileFragment", "Failed to load user data", exception)
                }
        } else {
            Toast.makeText(activity, "User not authenticated", Toast.LENGTH_SHORT).show()
            Log.e("ProfileFragment", "User not authenticated")
        }
    }

    private fun navigateToSettings() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, SettingsFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }
}