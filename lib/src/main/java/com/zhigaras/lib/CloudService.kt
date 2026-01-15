package com.zhigaras.lib

import com.google.firebase.database.GenericTypeIndicator
import kotlinx.coroutines.flow.Flow

interface CloudService {
    
    suspend fun postWithIdGenerating(obj: Any?, vararg children: String): String
    
    suspend fun <T : Any> getDataSnapshot(clazz: Class<T>, vararg children: String): T

    suspend fun <T : Any> getDataSnapshot(clazz: GenericTypeIndicator<T>, vararg children: String): T

    fun post(obj: Any?, vararg children: String)
    
    fun <T : Any> subscribe(clazz: Class<T>, vararg children: String): Flow<T>
    
    fun <T : Any> subscribeToList(clazz: Class<T>, vararg children: String): Flow<List<T>>
    
    fun addItemToList(item: String, vararg children: String)
    
    fun removeListItem(itemId: String, vararg children: String)
    
    companion object {
        const val USERS_PATH = "Users"
        const val TOPICS_PATH = "Topics"
        const val SIGNALING_PATH = "Signaling"
    }
}