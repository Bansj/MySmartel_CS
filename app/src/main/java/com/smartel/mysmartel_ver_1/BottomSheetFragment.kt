package com.smartel.mysmartel_ver_1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.mysmartel_ver_1.R


class BottomSheetFragment : Fragment() {




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false)

        val btnShowFragment = view.findViewById<Button>(R.id.btn_detailDeduct)
        btnShowFragment.setOnClickListener {
            val fragment = BottomSheetFragment()
            requireFragmentManager().beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_up, // Animation for fragment enter
                    R.anim.slide_out_down, // Animation for fragment exit
                    R.anim.slide_in_up, // Animation for fragment pop-enter
                    R.anim.slide_out_down // Animation for fragment pop-exit
                )
                .add(android.R.id.content, fragment)
                .addToBackStack(null)
                .commit()
        }

        return view


    }
}