package com.example.class_e


data class Post(
    val author: String = "",
    val title: String = "",
    val content: String = "",
    val date: String = "",
    val profileImage: Int = 0,
    val courseName: String = ""
)

data class Comment(
    val userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val profileImage: Int = 0,
    val text: String = "",
    val timestamp: String = ""
)