package com.example.mysmartel_ver_1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mysmartel_ver_1.databinding.FragmentPage01Binding


class Page01 : Fragment() {
    lateinit var binding: FragmentPage01Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPage01Binding.inflate(inflater, container, false)

        return binding.root
    }

}