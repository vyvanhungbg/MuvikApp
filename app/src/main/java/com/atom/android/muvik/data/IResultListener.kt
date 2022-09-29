package com.atom.android.muvik.data

interface IResultListener<T> {
    fun onSuccess(list: T)
    fun onFail(message: String)
}
