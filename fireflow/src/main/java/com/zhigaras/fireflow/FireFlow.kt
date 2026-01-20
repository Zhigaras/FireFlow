package com.zhigaras.fireflow

import com.google.firebase.database.GenericTypeIndicator
import com.zhigaras.fireflow.model.Data
import kotlinx.coroutines.flow.Flow

/**
 * Interface for interacting with Firebase Realtime Database using Coroutines and Flow.
 */
interface FireFlow {

    /**
     * Pushes a new object into the database with an automatically generated unique ID.
     *
     * @param obj The data to be saved.
     * @param children The path segments to the parent node.
     * @return The unique generated string identifier (push-key).
     */
    suspend fun postWithIdGenerating(obj: Any?, vararg children: String): String

    /**
     * Performs a single fetch (get) from the database and casts the result to the specified class.
     *
     * @param T The type of the expected object.
     * @param clazz The class of the data to be retrieved.
     * @param children The path segments to the data location.
     * @return An instance of type [T] or null if data is missing or empty.
     */
    suspend fun <T : Any> getDataSnapshot(clazz: Class<T>, vararg children: String): T?

    /**
     * Performs a single fetch (get) for complex generic types (e.g., List or Map).
     *
     * @param T The type of the expected object.
     * @param clazz A [GenericTypeIndicator] to preserve generic type information during deserialization.
     * @param children The path segments to the data location.
     * @return An instance of type [T] or null if data is missing.
     */
    suspend fun <T : Any> getDataSnapshot(clazz: GenericTypeIndicator<T>, vararg children: String): T?

    /**
     * Overwrites or sets data at the specified path.
     *
     * @param obj The data to be written.
     * @param children The path segments to the target location.
     */
    suspend fun post(obj: Any?, vararg children: String)

    /**
     * Subscribes to real-time data updates at the specified path.
     * Results are wrapped in a [Data] container to handle parsed and raw data if it can not be parsed.
     *
     * @param T The type of the expected object.
     * @param clazz The class of the data to be retrieved.
     * @param children The path segments to the location to watch.
     * @return A [Flow] emitting [Data] objects whenever the database updates.
     */
    fun <T : Any> subscribe(clazz: Class<T>, vararg children: String): Flow<Data<T>>

    /**
     * Subscribes to real-time data updates but filters out any updates that fail to be parsed.
     *
     * @param T The type of the expected object.
     * @param clazz The class of the data to be retrieved.
     * @param children The path segments to the location to watch.
     * @return A [Flow] emitting only successfully parsed objects of type [T].
     */
    fun <T : Any> subscribeIgnoreUnparsed(clazz: Class<T>, vararg children: String): Flow<T>

//    fun <T : Any> subscribeToList(clazz: Class<T>, vararg children: String): Flow<List<T>>
//
//    fun addItemToList(item: String, vararg children: String)
//
//    fun removeListItem(itemId: String, vararg children: String)

    companion object {
        fun create(dbProvider: DatabaseProvider): FireFlow = FireFlowImpl(dbProvider)
    }
}