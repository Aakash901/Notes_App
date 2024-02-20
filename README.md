Notes App
The Notes App is a simple Android application designed to allow users to create, view, update, and delete notes. It follows the MVVM (Model-View-ViewModel) architecture pattern, which separates the user interface from the business logic and data operations.

Features
User authentication via Google Sign-In.
Display all notes of the logged-in user.
Add new notes.
Update existing notes.
Delete notes.
Architecture
The app follows the MVVM architecture pattern:

Model: Contains data classes representing users and notes, as well as a database helper class for SQLite interactions.
View: Consists of activities and fragments responsible for presenting the user interface.
ViewModel: Manages the presentation logic and communicates with the model.
Technologies Used
Google Sign-In API for user authentication.
SQLite database for local data storage.
RecyclerView for displaying a list of notes.
Data binding for connecting the view with the ViewModel.
ViewModel and LiveData for managing UI-related data in a lifecycle-aware manner.

