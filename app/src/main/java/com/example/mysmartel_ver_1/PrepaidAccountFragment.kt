package com.example.mysmartel_ver_1

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import com.example.mysmartel_ver_1.databinding.FragmentPrepaidAccountBinding


class PrepaidAccountFragment : Fragment() {

    private var _binding: FragmentPrepaidAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using binding
        _binding = FragmentPrepaidAccountBinding.inflate(inflater, container, false)
        val view = binding.root

        // Set click listener for the button
        binding.btnCopy.setOnClickListener {
            val clipboardManager =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("label", binding.txtAccount.text)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(
                requireContext(),
                "텍스트가 복사되었습니다.",
                Toast.LENGTH_SHORT
            ).show()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up the binding instance to prevent memory leaks
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 로그아웃 버튼을 누르면 프래그먼트 창에서 login 액티비티 창으로 화면전환 하는 클릭 이벤트
        val btnLogOut = view.findViewById<ImageButton>(R.id.btn_go_to_login)
        btnLogOut.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}