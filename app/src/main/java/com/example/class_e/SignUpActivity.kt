package com.example.class_e

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var selectDobText: TextView
    private lateinit var dobEditText: EditText
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var firstNameLayout: TextInputLayout
    private lateinit var lastNameLayout: TextInputLayout
    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var confirmPasswordLayout: TextInputLayout
    private lateinit var dobLayout: TextInputLayout
    private lateinit var signUpButton: MaterialButton
    private lateinit var loginText: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        selectDobText = findViewById(R.id.selectDobText)
        dobEditText = findViewById(R.id.dobEditText)
        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        firstNameLayout = findViewById(R.id.firstNameLayout)
        lastNameLayout = findViewById(R.id.lastNameLayout)
        emailLayout = findViewById(R.id.emailLayout)
        passwordLayout = findViewById(R.id.passwordLayout)
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout)
        dobLayout = findViewById(R.id.dobLayout)
        signUpButton = findViewById(R.id.signUpButton)
        loginText = findViewById(R.id.loginText)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        selectDobText.setOnClickListener {
            openDatePickerDialog()
        }

        dobEditText.setOnClickListener {
            openDatePickerDialog()
        }

        signUpButton.setOnClickListener {
            Log.d("SignUpActivity", "Sign-up button clicked")
            signUpUser()
        }

        val loginTextContent = "Already have an account? Login"
        val spannableString = SpannableString(loginTextContent)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        spannableString.setSpan(
            clickableSpan,
            loginTextContent.indexOf("Login"),
            loginTextContent.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        loginText.text = spannableString
        loginText.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun openDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "${selectedDay}/${selectedMonth + 1}/$selectedYear"
                dobEditText.setText(formattedDate)
            },
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.datePicker.maxDate = calendar.timeInMillis

        datePickerDialog.show()
    }

    private fun signUpUser() {
        val firstName = firstNameEditText.text.toString()
        val lastName = lastNameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()
        val dob = dobEditText.text.toString()

        var isValid = true

        if (firstName.isEmpty()) {
            firstNameLayout.error = "Please enter first name"
            isValid = false
        } else {
            firstNameLayout.error = null
        }

        if (lastName.isEmpty()) {
            lastNameLayout.error = "Please enter last name"
            isValid = false
        } else {
            lastNameLayout.error = null
        }

        if (email.isEmpty()) {
            emailLayout.error = "Please enter email"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.error = "Please enter a valid email address"
            isValid = false
        } else {
            emailLayout.error = null
        }

        if (password.isEmpty()) {
            passwordLayout.error = "Please enter password"
            isValid = false
        } else if (password.length < 8 || !password.matches(".*\\d.*".toRegex())) {
            passwordLayout.error =
                "Password must be at least 8 characters long and contain at least one number"
            isValid = false
        } else {
            passwordLayout.error = null
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordLayout.error = "Please confirm password"
            isValid = false
        } else if (password != confirmPassword) {
            confirmPasswordLayout.error = "Passwords do not match"
            isValid = false
        } else {
            confirmPasswordLayout.error = null
        }

        if (dob.isEmpty()) {
            dobLayout.error = "Please enter date of birth"
            isValid = false
        } else {
            dobLayout.error = null
        }

        if (!isValid) {
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = User(auth.currentUser!!.uid, firstName, lastName, email)
                    db.collection("users").document(auth.currentUser!!.uid)
                        .set(user)

                            Toast.makeText(this, "Signed up successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()


                } else {
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        emailLayout.error = "An account with this email already exists"
                    } else {
                        Toast.makeText(
                            this,
                            "Signup failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }
}