package com.zhigaras.fireflow.node.typed

import com.zhigaras.fireflow.node.Node
import kotlinx.coroutines.flow.Flow

/**
 * A type-aware wrapper around a [Node] that handles data serialization and deserialization.
 *
 * @param T The type of the data expected at this database location.
 */
interface TypedNode<T : Any> : Node {

    /**
     * Performs a single-shot fetch of the data at this node.
     *
     * @return The deserialized object of type [T], or `null` if the path does not exist.
     * @throws Exception if data conversion fails.
     */
    suspend fun fetch(): T?

    /**
     * Creates a cold [Flow] that streams data updates from this node.
     *
     * ### Features:
     * - **Automatic Retries:** Includes a built-in [retry] mechanism. If a non-fatal error occurs
     *   (e.g., transient network issues), the flow will attempt to re-subscribe after a short delay.
     *
     * @return A [Flow] emitting the latest data of type [T].
     * @throws FireFlowException if a fatal error occurs or retries are exhausted.
     * @see [ValueEventListener.onDataChange](https://firebase.google.com)
     */
    fun observe(): Flow<T>

    /**
     * Represents a [TypedNode] specifically for objects.
     */
    interface Object<T : Any> : TypedNode<T> {

        /**
         * Atomic-like update operation.
         *
         * Fetches the current value, applies the [transform] function, and posts
         * the result back to the database.
         *
         * @param transform A lambda that takes the current state and returns the new state.
         */
        suspend fun update(transform: (T) -> T)
    }

    /**
     * Represents a [TypedNode] for a collection of items (a node with multiple children).
     *
     * In Firebase, collections are typically represented as a JSON object where
     * keys are unique IDs and values are the items.
     *
     * ### Modifying Items:
     * Note that [Collection] does **not** provide a direct `update()` method. To update
     * a specific item within this collection, navigate to the specific child node:
     *
     * ```kotlin
     * // Example: Updating a user in a "users" collection
     * rootNode.child("users")
     *     .child(userId)
     *     .asTyped(User::class.java)
     *     .update { currentUser -> currentUser.copy(status = "online") }
     * ```
     */
    interface Collection<T : Any> : TypedNode<Map<String, T>>
}
