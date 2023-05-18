package com.example.mysmartel_ver_1

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import java.util.*
import kotlin.collections.ArrayList


class MyInfoFragment : Fragment() {

    private lateinit var phoneNumTextView: TextView
    private lateinit var customerNameTextView: TextView
    private lateinit var telcoTextView: TextView

    private lateinit var viewPager2: ViewPager2
    private lateinit var dotsIndicator: DotsIndicator
    private var currentPage = 0
    private val handler = Handler()
    private lateinit var timer: Timer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_my_info, container, false)

        // Find the ProgressBar by ID
        val progressBar = rootView.findViewById<ProgressBar>(R.id.progressBar_leftData)

        // Load the fade-in animation from res/anim folder
        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_anim)

        // Start the animation on the ProgressBar
        progressBar.startAnimation(anim)

        viewPager2 = rootView.findViewById(R.id.viewPager2)
        dotsIndicator = rootView.findViewById(R.id.dotsIndicator)

        // Declare fragments
        val fragment1 = banner1Fragment()
        val fragment2 = banner2Fragment()
        val fragment3 = banner3Fragment()
        val fragment4 = banner4Fragment()
        val fragment5 = banner5Fragment()
        val fragment6 = banner6Fragment()

        // Register the fragments to the list
        val fragments = ArrayList<Fragment>()
        fragments.add(fragment1)
        fragments.add(fragment2)
        fragments.add(fragment3)
        fragments.add(fragment4)
        fragments.add(fragment5)
        fragments.add(fragment6)

        // Create a FragmentStateAdapter to bind the fragments to ViewPager2
        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return fragments.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }
        }

        // Apply adapter to ViewPager2
        viewPager2.adapter = adapter

        // Apply dotsIndicator to ViewPager2
        dotsIndicator.setViewPager2(viewPager2)

        // Set click listener for btn_menu button
        rootView.findViewById<ImageButton>(R.id.btn_menu).setOnClickListener {
            it.findNavController().navigate(R.id.action_myInfoFragment_to_menuFragment)
        }

        // Set click listener for btn_benefit button
        rootView.findViewById<ImageButton>(R.id.btn_benefit).setOnClickListener {
            it.findNavController().navigate(R.id.action_myInfoFragment_to_benefitFragment)
        }

        // Start auto sliding every 2 seconds
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post {
                    if (currentPage == fragments.size) {
                        currentPage = 0
                    }
                    viewPager2.setCurrentItem(currentPage++, true)
                }
            }
        }, 2000, 2000)

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Cancel the timer when the view is destroyed to avoid memory leaks
        timer.cancel()
    }
}