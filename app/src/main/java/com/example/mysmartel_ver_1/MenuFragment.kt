package com.example.mysmartel_ver_1

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ExpandableListView
import android.widget.ImageButton
import android.widget.Spinner
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysmartel_ver_1.databinding.FragmentMenuBinding


class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 부가서비스 메뉴 스크롤
        binding.layout01.setOnClickListener {
            if (binding.layoutDetailExtraServices.visibility == View.VISIBLE) {
                binding.layoutDetailExtraServices.visibility = View.GONE
                binding.layoutBtn01.animate().apply {
                    duration = 300
                    rotation(0f)
                }
            } else {
                binding.layoutDetailExtraServices.visibility = View.VISIBLE
                binding.layoutBtn01.animate().apply {
                    duration = 300
                    rotation(180f)
                }
            }
        }

        // 청구서 메뉴 스크롤
        binding.layout02.setOnClickListener {
            if (binding.layoutDetailBill.visibility == View.VISIBLE) {
                binding.layoutDetailBill.visibility = View.GONE
                binding.layoutBtn02.animate().apply {
                    duration = 300
                    rotation(0f)
                }
            } else {
                binding.layoutDetailBill.visibility = View.VISIBLE
                binding.layoutBtn02.animate().apply {
                    duration = 300
                    rotation(180f)
                }
            }
        }

        // 변경신청 메뉴 스크롤
        binding.layout03.setOnClickListener {
            if (binding.layoutDetailChange.visibility == View.VISIBLE) {
                binding.layoutDetailChange.visibility = View.GONE
                binding.layoutBtn03.animate().apply {
                    duration = 300
                    rotation(0f)
                }
            } else {
                binding.layoutDetailChange.visibility = View.VISIBLE
                binding.layoutBtn03.animate().apply {
                    duration = 300
                    rotation(180f)
                }
            }
        }

        // 정지 메뉴 시크롤
        binding.layout04.setOnClickListener {
            if (binding.layoutDetailPause.visibility == View.VISIBLE) {
                binding.layoutDetailPause.visibility = View.GONE
                binding.layoutBtn04.animate().apply {
                    duration = 300
                    rotation(0f)
                }
            } else {
                binding.layoutDetailPause.visibility = View.VISIBLE
                binding.layoutBtn04.animate().apply {
                    duration = 300
                    rotation(180f)
                }
            }
        }

        // 고객센터 메뉴 시크롤
        binding.layout05.setOnClickListener {
            if (binding.layoutDetailCS.visibility == View.VISIBLE) {
                binding.layoutDetailCS.visibility = View.GONE
                binding.layoutBtn05.animate().apply {
                    duration = 500
                    rotation(0f)
                }
            } else {
                binding.layoutDetailCS.visibility = View.VISIBLE
                binding.layoutBtn05.animate().apply {
                    duration = 500
                    rotation(180f)
                }
            }
        }

        // Click listeners for buttons in the menu fragment
        binding.btnMyInfo.setOnClickListener {
            it.findNavController().navigate(R.id.action_menuFragment_to_myInfoFragment)
        }

        binding.btnBenefit.setOnClickListener {
            it.findNavController().navigate(R.id.action_menuFragment_to_benefitFragment)
        }


        // 톱니바퀴 버튼을 누르면 메뉴 프래그먼트 창에서 Setting 액티비티 창으로 화면전환 하는 클릭 이벤트
        val btnSetting = view.findViewById<ImageButton>(R.id.btn_setting)
        btnSetting.setOnClickListener {
            val intent = Intent(activity, Setting::class.java)
            startActivity(intent)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}