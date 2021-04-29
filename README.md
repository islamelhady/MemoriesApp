# MVVM-Memories-app
This is simple memories app that follows MVVM architectural design pattern and uses android jetpack components.

![GitHub Cards Preview](screenshots/memories_card.jpg?raw=true)

## MVVM Architecture

MVVM - MVVM stands for Model, View, ViewModel. MVVM is one of the architectural patterns which enhances separation of concerns, it allows separating the user interface logic from the business (or the back-end) logic. Its target is to achieve the following principle “Keeping UI code simple and free of app logic in order to make it easier to manage”.          



## Android Jetpack components
1. Navigation Components - Navigation component helps you implement navigation, from simple button clicks to more complex patterns, such as app bars and the navigation drawer. The Navigation component also ensures a consistent and predictable user experience by adhering to an established set of principles.

2. Android Room Persistence - It is a SQLite object mapping library. Use it to Avoid boilerplate code and easily convert SQLite table data to Java objects. Room provides compile time checks of SQLite statements and can return RxJava, Flowable and LiveData observables.

3. Kotlin Coroutines - A coroutine is a concurrency design pattern that you can use on Android to simplify code that executes asynchronously. On Android, coroutines help to manage long-running tasks that might otherwise block the main thread and cause your app to become unresponsive.

4. ViewModel - It manages UI-related data in a lifecycle-conscious way. It stores UI-related data that isn't destroyed on app rotations.

5. LiveData - It notifies views of any database changes. Use LiveData to build data objects that notify views when the underlying database changes.

6. Kotlin - Kotlin is a modern statically typed programming language used by over 60% of professional Android developers that helps boost productivity, developer satisfaction, and code safety.


## Package Structure


    ├── adapter                             # Adapter for RecyclerView
    |   ├── DiffutilCallback
    |   └── MemoriesAdapter                 # ViewHolder for RecyclerView
    |
    ├── database                            # Local Persistence Database. Room (SQLite) database
    |   ├── Dao                             # Data Access Object for Room
    |   └── MemoriesDatabase                # Database Instance
    |
    ├── model                               # Model classes
    |   └── Memories
    |
    ├── repository                          # Repository classes
    |   └── MemoriesRepository
    |
    ├── ui                                  # Activity/View layer
    │   ├── fragments
    |   │   ├── MemoriesContentFragment     # Memories Content Fragment
    |   │   └── MemoriesFragment            # Main Screen Fragment
    │   ├── splash
    |   │   └── SplashActivity              # Splash Activity
    |
    ├── utils
    │   ├── ContentUriToActualFilePath
    |   ├── SwipeToDelete                   # Swipe To Delete for RecyclerView
    |   └── Util                            # ViewHolder for RecyclerView
    |
    ├── viewmodel                           # Main ViewModel
    │   ├── MemoriesViewModel
    |   └── MemoriesViewModelFactory
    |
    ├── MainActivity                        # Main Screen Activity

## Architecture

This app uses [***MVVM (Model View View-Model)***](https://developer.android.com/jetpack/docs/guide#recommended-app-arch) architecture.

![](screenshots/android_room_db.jpg?raw=true)
