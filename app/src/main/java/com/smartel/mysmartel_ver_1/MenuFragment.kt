package com.smartel.mysmartel_ver_1

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.mysmartel_ver_1.R
import com.example.mysmartel_ver_1.databinding.FragmentMenuBinding


class MenuFragment : Fragment() {

    private lateinit var txtcustName: TextView
    private lateinit var txtPhoneNumber: TextView
    private lateinit var txtTelecom: TextView
    private lateinit var sharedPrefs: MyInfoSharedPreferences


    private val viewModel: MyInfoViewModel by viewModels({ requireActivity() })

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private var custName: String? = null
    private var phoneNumber: String? = null
    private var telecom: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefs = MyInfoSharedPreferences(requireContext())

        /*// Initialize the TextViews
        txtcustName = binding.txtcustName
        txtPhoneNumber = binding.txtPhoneNumber
        txtTelecom = binding.txtTelecom*/

        // Retrieve the data from the ViewModel or arguments
        val custName = viewModel.custName ?: arguments?.getString("custName")
        val phoneNumber = viewModel.phoneNumber ?: arguments?.getString("phoneNumber")
        val telecom = viewModel.Telecom ?: arguments?.getString("Telecom")
        val serviceAcct = viewModel.serviceAcct ?: arguments?.getString("service_acct")

        // Set the data in the views
        binding.txtcustName.text = "  ${custName}님, 안녕하세요. "

        // Set the data in the ViewModel
        viewModel.custName = custName
        viewModel.phoneNumber = phoneNumber
        viewModel.Telecom = telecom

        // 청구요금 조회 슬라이드 업 클릭 이벤트
        binding.txtBillDetail.setOnClickListener {
            val fragment: Fragment? = when (telecom) {
                "SKT" -> {
                    val sktBillDetailFragment = SktBillDetailFragment()
                    val bundle = Bundle()
                    bundle.putString("serviceAcct", serviceAcct)
                    sktBillDetailFragment.arguments = bundle

                    // Log the values for SKT
                    Log.d(TAG, "-------------------- SKT --> serviceAcct: $serviceAcct------ -")

                    sktBillDetailFragment
                }
                "KT" -> {
                    val ktBillDetailFragment = KtBillDetailFragment()
                    val bundle = Bundle()
                    bundle.putString("phoneNumber", phoneNumber)
                    ktBillDetailFragment.arguments = bundle

                    // Log the values for KT
                    Log.d(TAG, "-------------------- KT --> phoneNumber: $phoneNumber------------------ -")

                    ktBillDetailFragment
                }
                "LGT" -> {
                    val lgtBillDetailFragment = LgtBillDetailFragment()
                    val bundle = Bundle()
                    bundle.putString("custNm", custName)
                    bundle.putString("phoneNumber", phoneNumber)
                    lgtBillDetailFragment.arguments = bundle

                    // Log the values for LGT
                    Log.d(TAG, "-------------------- LGT --> custName: $custName--------------------- -")
                    Log.d(TAG, "-------------------- LGT --> phoneNumber: $phoneNumber------------------ -")

                    lgtBillDetailFragment
                }
                else -> {
                    Log.e(TAG, "Invalid Telecom value: $telecom--------")
                    null
                }
            }
            fragment?.let {
                requireFragmentManager().beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_up, // Animation for fragment enter
                        R.anim.slide_out_down, // Animation for fragment exit
                        R.anim.slide_in_up, // Animation for fragment pop-enter
                        R.anim.slide_out_down // Animation for fragment pop-exit
                    )
                    .add(id, it) // Use the ID of any existing container view in your layout
                    .addToBackStack(null)
                    .commit()
            }
        }

        // 이전 요금조회 슬라이드 업 클릭이벤트
        binding.txtPastBillCheck.setOnClickListener {
            val fragment: Fragment? = when (telecom) {
                "SKT" -> {
                    val sktPaymentDetailFragment = SktPaymentDetailFragment()
                    val bundle = Bundle()
                    bundle.putString("serviceAcct", serviceAcct)
                    sktPaymentDetailFragment.arguments = bundle

                    // Log the values for SKT
                    Log.d(TAG, "-------------------- SKT --> serviceAcct: $serviceAcct------ -")

                    sktPaymentDetailFragment
                }
                "KT" -> {
                    val ktPaymentDetailFragment = KtPaymentDetailFragment()
                    val bundle = Bundle()
                    bundle.putString("phoneNumber", phoneNumber)
                    ktPaymentDetailFragment.arguments = bundle

                    // Log the values for KT
                    Log.d(TAG, "-------------------- KT --> phoneNumber: $phoneNumber------------------ -")

                    ktPaymentDetailFragment
                }
                "LGT" -> {
                    val lgtPaymentDetailFragment = LgtPaymentDetailFragment()
                    val bundle = Bundle()
                    bundle.putString("custNm", custName)
                    bundle.putString("phoneNumber", phoneNumber)
                    lgtPaymentDetailFragment.arguments = bundle

                    // Log the values for LGT
                    Log.d(TAG, "-------------------- LGT --> custName: $custName--------------------- -")
                    Log.d(TAG, "-------------------- LGT --> phoneNumber: $phoneNumber------------------ -")

                    lgtPaymentDetailFragment
                }
                else -> {
                    Log.e(TAG, "Invalid Telecom value: $telecom--------")
                    null
                }
            }
            fragment?.let {
                requireFragmentManager().beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_up, // Animation for fragment enter
                        R.anim.slide_out_down, // Animation for fragment exit
                        R.anim.slide_in_up, // Animation for fragment pop-enter
                        R.anim.slide_out_down // Animation for fragment pop-exit
                    )
                    .add(id, it) // Use the ID of any existing container view in your layout
                    .addToBackStack(null)
                    .commit()
            }
        }

        // 부가서비스 메뉴 스크롤
        // Set the visibility of layoutDetailExtraServices, layoutBtn01, and textView13 based on telecom value
        binding.layoutDetailExtraServices.visibility = if (telecom == "KT" || telecom == "LGT" || telecom == "SKT") {
            View.GONE
        } else {
            View.VISIBLE
        }
        binding.layoutBtn01.visibility = if (telecom == "KT" || telecom == "LGT" || telecom == "SKT") {
            View.GONE
        } else {
            View.VISIBLE
        }
        binding.textView13.visibility = if (telecom == "KT" || telecom == "LGT" || telecom == "SKT") {
            View.GONE
        } else {
            View.VISIBLE
        }

        // 청구서 메뉴 스크롤
        binding.layout02.setOnClickListener {
            toggleVisibility(binding.layoutDetailBill, binding.layoutBtn02)
        }

        // 변경신청 메뉴 스크롤
        binding.layout03.setOnClickListener {
            toggleVisibility(binding.layoutDetailChange, binding.layoutBtn03)
        }

        // 정지 메뉴 시크롤
        binding.layout04.setOnClickListener {
            toggleVisibility(binding.layoutDetailPause, binding.layoutBtn04)
        }

        // 고객센터 메뉴 시크롤
        binding.layout05.setOnClickListener {
            toggleVisibility(binding.layoutDetailCS, binding.layoutBtn05)
        }

        // Click listeners for buttons in the menu fragment
        binding.btnMyInfo.setOnClickListener {
            it.findNavController().navigate(R.id.action_menuFragment_to_myInfoFragment)
        }

        binding.btnSetting.setOnClickListener {
            it.findNavController().navigate(R.id.action_menuFragment_to_settingFragment)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()

        // Save the data to SharedPreferences before the fragment view is destroyed
        sharedPrefs.custName = custName ?: ""
        sharedPrefs.phoneNumber = phoneNumber ?: ""
        sharedPrefs.telecom = telecom ?: ""

        _binding = null
    }
    private var isAnimating = false

    // 아래 스크롤 버튼 클릭 이벤트 : 요소들이 하나씩 펼쳐지고 접히는 애니메이션 효과
    private fun toggleVisibility(layout: View, button: View) {
        if (isAnimating) {
            return
        }

        if (layout.visibility == View.VISIBLE) {
            val foldAnimation = ValueAnimator.ofInt(layout.height, 0)
            foldAnimation.addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                val layoutParams = layout.layoutParams
                layoutParams.height = value
                layout.layoutParams = layoutParams
            }
            foldAnimation.apply {
                duration = 300
                interpolator = DecelerateInterpolator()
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        isAnimating = true
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        layout.visibility = View.GONE
                        isAnimating = false
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        isAnimating = false
                    }
                })
            }

            button.animate().apply {
                duration = 300
                rotation(0f)
            }

            foldAnimation.start()
        } else {
            layout.visibility = View.VISIBLE // Reset layout visibility

            // Measure the height to calculate the correct unfold height
            layout.measure(
                View.MeasureSpec.makeMeasureSpec(layout.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val unfoldHeight = layout.measuredHeight

            val unfoldAnimation = ValueAnimator.ofInt(0, unfoldHeight)
            unfoldAnimation.addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                val layoutParams = layout.layoutParams
                layoutParams.height = value
                layout.layoutParams = layoutParams
            }
            unfoldAnimation.apply {
                duration = 300
                interpolator = DecelerateInterpolator()
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        isAnimating = true
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        isAnimating = false
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        isAnimating = false
                    }
                })
            }

            button.animate().apply {
                duration = 300
                rotation(180f)
            }

            unfoldAnimation.start()
        }
    }

}
