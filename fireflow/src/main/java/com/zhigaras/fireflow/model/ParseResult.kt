package com.zhigaras.fireflow.model

interface ParseResult<out T : Any> {
    data class Success<T : Any>(val data: T) : ParseResult<T>
    data class Error(val raw: String) : ParseResult<Nothing>
}