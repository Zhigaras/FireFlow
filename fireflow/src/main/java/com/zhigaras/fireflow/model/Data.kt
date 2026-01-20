package com.zhigaras.fireflow.model

interface Data<out T : Any> {
    data class Parsed<T : Any>(val data: T) : Data<T>
    data class Raw(val raw: String) : Data<Nothing>
}