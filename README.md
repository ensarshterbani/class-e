# Class-E

An Android application for class management and e-learning built with Kotlin and Firebase.

## Overview

Class-E is a mobile application that allows users to manage courses, create posts, and interact with other students and professors. The app provides functionality for course enrollment, assignment tracking, and communication within academic settings.

## Features

- User authentication (Login/Sign up)
- Course management and enrollment
- Post creation and discussion
- Comments system
- Student and professor profiles
- Assignment tracking
- Grading system integration

## Tech Stack

- **Language**: Kotlin
- **Platform**: Android (API 24+)
- **Backend**: Firebase (Authentication, Firestore)
- **UI**: Material Design Components
- **Architecture**: MVVM with LiveData and ViewBinding
- **Navigation**: Navigation Component

## Requirements

- Android Studio
- Android SDK 24 or higher
- Kotlin 1.9.24
- Gradle 8.7.2

## Setup

1. Clone the repository
2. Open the project in Android Studio
3. Add your Firebase configuration file (`google-services.json`) to the `app/` directory
4. Sync the project with Gradle
5. Build and run the application

## Project Structure

```
app/src/main/java/com/example/class_e/
├── Activities (Login, SignUp, Main)
├── Fragments (Courses, Home, Profile, Settings, etc.)
├── Adapters (Courses, Posts, Comments)
├── Models (Course, Post, User, etc.)
└── UI components
```

## Firebase Services Used

- Firebase Authentication
- Cloud Firestore
- Firebase Analytics

## Build

To build the project:

```bash
./gradlew build
```

To run tests:

```bash
./gradlew test
```

## Version

- Version: 1.0
- Min SDK: 24
- Target SDK: 34
# class-e
