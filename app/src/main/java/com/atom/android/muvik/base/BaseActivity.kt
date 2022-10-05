package com.atom.android.muvik.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VBinding : ViewBinding>(private val bindingLayoutInflater: (LayoutInflater) -> VBinding) :
    AppCompatActivity() {

    private var _binding: VBinding? = null
    val binding: VBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingLayoutInflater.invoke(layoutInflater)
        setContentView(binding.root)
        initView()
        initData()
        initEvent()
    }

    abstract fun initView()
    abstract fun initData()
    abstract fun initEvent()

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
