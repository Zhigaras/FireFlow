# FireFlow

A lightweight Kotlin wrapper for the **Firebase Realtime Database**. It simplifies database interactions by transforming raw references into type-safe **Coroutines** and **Flows**.

---

## 📖 Table of Contents
- [Core Concepts](#core-concepts)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Dependency Injection](#dependency-injection)

---

## Core Concepts

### `DatabaseProvider`
An abstraction layer for providing a `DatabaseReference`.
- **Default Implementation:** Uses the standard [FirebaseDatabase.getInstance()](https://firebase.google.com).
- **Custom Implementation:** Use this for [Multi-Region Support](https://firebase.google.com), scoped access (e.g., specific sub-nodes), or connecting to the [Firebase Local Emulator](https://firebase.google.com).

### `Node` & `RootNode`
The entry point to your database. Use `RootNode` as your base anchor and navigate using the fluent `child()` API.

---

## Installation

Add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.zhigaras:fireflow:$last_version")
}
```
## Quick Start
### Initialize the Root

The RootNode is your entry point. You can inject it using your favorite DI framework or create it manually.

```kotlin
// Using the default Firebase instance
val root = Node.root()

// Or provide a custom DatabaseProvider for multi-region or testing
val root = Node.root(MyCustomProvider())
```

### Basic CRUD Operations
Navigate through nodes using the child() method and perform one-shot operations.

```kotlin
val usersNode = root.child("users")

// Write data
usersNode.set(User(name = "Alex"))

// Write with auto-generated ID
val newId = usersNode.postWithIdGenerating(User(name = "Bob"))
```

### Type-Safe Interaction
Convert any Node into a TypedNode to work with your POJOs directly.
```kotlin
val profileNode = root.child("profiles").child("user_123").asTyped(Profile::class.java)

// Fetch once
val profile = profileNode.fetch()

// Reactive updates
profileNode.observe().collect { profile -> 
    println("Profile updated: ${profile.name}") 
}

// Atomic-like update
profileNode.update { it.copy(lastSeen = System.currentTimeMillis()) }
```
### Working with Collections
To handle lists of data, use asTypedCollectionOf.

```kotlin
val postsNode = root.child("posts").asTypedCollectionOf(Post::class.java)

// Returns Flow<Map<String, Post>> where String is the Firebase ID
postsNode.observe().collect { postsMap ->
    postsMap.forEach { (id, post) -> println("Post $id: ${post.title}") }
}
```
**NOTE**: Collection does not have a direct update() method. To update a specific item, navigate to its child:
```kotlin
postsNode.child("post_id").asTyped(Post::class.java).update { ... }
```

## Dependency Injection
This library is designed for clean architecture. Inject RootNode into your repositories for better testability.

**Example with Koin:**
```kotlin
val appModule = module {
    single<DatabaseProvider> { DatabaseProvider.Default() }
    single<RootNode> { Node.root(get()) }
    single { UserRepository(get()) }
}
```

---

Ready to simplify your Firebase code? Feel free to open an issue or a PR!
