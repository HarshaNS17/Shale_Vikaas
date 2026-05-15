Shaale-Vikas is a modern Android application designed to bridge the gap between rural/government schools and their alumni communities. It provides a transparent, real-time digital platform where school administrators can post infrastructure needs, and alumni can contribute to their alma mater’s development.
📌 Problem Statement
Many government and rural schools suffer from delayed infrastructure improvements (repairs, sanitation, libraries) due to slow administrative processes and lack of funding. Simultaneously, many alumni want to support their schools but lack visibility into the school's actual, verified requirements.
🚀 The Solution
Shaale-Vikas provides a centralized ecosystem where:
•
Administrators post infrastructure needs with photos and cost estimates.
•
Alumni browse these needs, pledge financial support, and track project progress.
•
Transparency is maintained via real-time cloud synchronization and "Before/After" visual proof.
✨ Key Features
•
Dual-User Experience: Distinct dashboards for School Admins and Alumni.
•
Infrastructure Reporting: Admins can upload photos, descriptions, and budgets for school requirements.
•
Real-Time Tracking: Alumni can see a live progress bar of funds/pledges for any project.
•
Cloud Media Storage: High-resolution image evidence for every project.
•
Modern UI: Built with Jetpack Compose and Material 3 for a fluid, responsive experience.
•
Visual Feedback: Integrated Lottie Animations for success states and loading.
🛠 Tech Stack & Tools
•
Language: Kotlin (v2.0.21)
•
UI Framework: Jetpack Compose (Declarative UI)
•
Design System: Material 3
•
Architecture: MVVM (Model-View-ViewModel)
•
Dependency Injection: Dagger Hilt
•
Backend/Database:
◦
Firebase Authentication (Secure Login)
◦
Firebase Firestore (Real-Time NoSQL Database)
◦
Firebase Storage (Image Hosting)
•
Libraries:
◦
Coil: For asynchronous image loading.
◦
Lottie: For interactive vector animations.
◦
Navigation Compose: For type-safe screen routing.
◦
Coroutines & Flow: For asynchronous data handling.
🏗 Project Architecture
The project follows the MVVM (Model-View-ViewModel) architectural pattern combined with Dagger Hilt for dependency injection. This ensures the codebase is:
•
Scalable: Easy to add new features.
•
Testable: Separation of UI and Logic.
•
Maintainable: Clear data flow between Firestore and the UI.
⚙️ Installation & Setup
To run this project locally, follow these steps:
1.
Clone the repository:
Shell Script
git clone https://github.com/HarshaNS17/Shaale_Vikaas.git
2.
Setup Firebase:
◦
Create a project on the Firebase Console.
◦
Enable Authentication, Firestore, and Cloud Storage.
◦
Download the google-services.json file and place it in the app/ directory.
3.
Build the Project:
◦
Open the project in Android Studio (Ladybug or later).
◦
Sync the project with Gradle files.
◦
Run the application on an emulator or physical device (API Level 26+).
📝 Future Scope
•
Payment Integration: Adding UPI/Razorpay for direct digital donations.
•
AI Verification: Using Gemini AI to automatically verify the quality of infrastructure repairs.
•
Global Leaderboard: Recognizing top alumni contributors and most improved schools.
👨‍💻 Author
Harsha
Developed as part of the Namma Skill Initiative.
