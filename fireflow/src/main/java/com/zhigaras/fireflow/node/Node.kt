package com.zhigaras.fireflow.node

import com.zhigaras.fireflow.DatabaseProvider
import com.zhigaras.fireflow.node.typed.TypedNode

/**
 * Represents a specific location (path) in the Firebase Realtime Database.
 *
 * This interface provides a fluent API for navigating the database tree and
 * converting raw data into type-safe objects or collections.
 */
interface Node {

    /**
     * Navigates to a deeper path in the database relative to the current node.
     *
     * @param path A relative path (e.g., "users/settings").
     * @return A new [Node] instance pointing to the specified child path.
     */
    fun child(path: String): Node

    /**
     * Converts the current node into a [TypedNode.Object] for single-object operations.
     *
     * @param clazz The class type to map the database data into.
     * @return A typed handler for the specified class.
     */
    fun <T : Any> asTyped(clazz: Class<T>): TypedNode.Object<T>

    /**
     * Converts the current node into a [TypedNode.Collection] for handling lists of data.
     *
     * @param clazz The class type of the items within the collection.
     * @return A typed handler for list-based operations.
     */
    fun <T : Any> asTypedCollectionOf(clazz: Class<T>): TypedNode.Collection<T>

    /**
     * Performs a standard "set" operation. Overwrites the data at this node.
     *
     * @param obj The data to write.
     */
    suspend fun set(obj: Any?)

    /**
     * Generates a unique key using Firebase's [push()](https://firebase.google.com)
     * method and saves the object at that location.
     *
     * @param obj The data to save.
     * @return The unique auto-generated ID (push ID).
     */
    suspend fun postWithIdGenerating(obj: Any): String

    companion object {

        /**
         * Entry point to the library. Creates a [RootNode] starting from the provided [DatabaseProvider].
         *
         * @param provider The strategy to obtain the [DatabaseReference]. Defaults to [DatabaseProvider.Default].
         * @return An implementation of [RootNode] linked to the provider's reference.
         */
        fun root(provider: DatabaseProvider = DatabaseProvider.Default()): RootNode =
            NodeImpl(provider.provide())
    }
}
