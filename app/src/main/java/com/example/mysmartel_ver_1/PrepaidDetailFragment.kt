package com.example.mysmartel_ver_1

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton


class PrepaidDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_prepaid_detail, container, false)
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