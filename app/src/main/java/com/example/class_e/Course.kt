package com.example.class_e


data class Course(
    val courseName: String = "",
    val professorName: String = "",
    val assistantProfessorName: String = "",
    val academicYear: String = "",
    val numberOfStudents: Int = 0,
    val gradingScheme: List<GradingSchemeItem> = emptyList(),
    val grades: List<String> = emptyList(),
    val assignments: List<Assignment> = emptyList(),
    val professorProfile: Profile = Profile(),
    val assistantProfessorProfile: Profile = Profile(),
    val courseID: String = ""
)

data class Assignment(
    val name: String,
    val dueState: DueState
)

enum class DueState {
    DONE,
    MISSING,
    DUE
}

data class Profile(
    val userID: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val profileImage: Int = 0,
    val courses: List<String> = emptyList()
)

data class GradingSchemeItem(
    val range: String = "",
    val grade: String = ""
)