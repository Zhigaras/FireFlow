package com.zhigaras.fireflow.model

interface Data<out T : Any> {
    data object Empty : Data<Nothing>
    data class Success<T : Any>(val data: T) : Data<T>
    data class Error(val raw: String) : Data<Nothing>
}