package com.zhigaras.fireflow.node

/**
 * The top-level entry point into the Firebase Realtime Database.
 *
 * [RootNode] acts as the anchor for your database structure. It is typically
 * initialized at the application level and used to navigate to specific sub-nodes.
 *
 * ### Dependency Injection (DI)
 * If you are using DI frameworks like [Dagger/Hilt](https://developer.android.com)
 * or [Koin](https://insert-koin.io), you should inject this interface into your
 * Repositories or ViewModels.
 *
 * **Example (Koin):**
 * ```kotlin
 * val appModule = module {
 *     single<RootNode> { Node.root() }
 * }
 * ```
 */
interface RootNode : Node
