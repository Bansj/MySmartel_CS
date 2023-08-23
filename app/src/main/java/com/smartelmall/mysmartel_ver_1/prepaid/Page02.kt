package com.smartelmall.mysmartel_ver_1.prepaid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smartelmall.mysmartel_ver_1.databinding.FragmentPage02Binding


class Page02 : Fragment() {
    lateinit var binding: FragmentPage02Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPage02Binding.inflate(inflater, container, false)

        return binding.root
    }

}