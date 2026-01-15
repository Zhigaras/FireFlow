package com.zhigaras.lib

import com.google.firebase.database.GenericTypeIndicator
import com.zhigaras.lib.model.ParseResult
import kotlinx.coroutines.flow.Flow

interface FireFlow {
    
    suspend fun postWithIdGenerating(obj: Any?, vararg children: String): String
    
    suspend fun <T : Any> getDataSnapshot(clazz: Class<T>, vararg children: String): T

    suspend fun <T : Any> getDataSnapshot(clazz: GenericTypeIndicator<T>, vararg children: String): T

    fun post(obj: Any?, vararg children: String)
    
    fun <T : Any> subscribe(clazz: Class<T>, vararg children: String): Flow<ParseResult<T>>

//    fun <T : Any> subscribeToList(clazz: Class<T>, vararg children: String): Flow<List<T>>
//
//    fun addItemToList(item: String, vararg children: String)
//
//    fun removeListItem(itemId: String, vararg children: String)

    companion object {
        fun create(dbProvider: DatabaseProvider): FireFlow = FireFlowImpl(dbProvider)
    }
}