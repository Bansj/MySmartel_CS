package com.smartel.mysmartel_ver_1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysmartel_ver_1.R

class LgtDeductDetailViewFragment : Fragment() {

    private lateinit var deductionDetails: List<LgtRemainInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        deductionDetails = arguments?.getSerializable("deductionDetails") as List<LgtRemainInfo>
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lgt_deduct_detail_view, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.deductionDetailsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = DeductionDetailsAdapter(deductionDetails)

        return view
    }

    companion object {
        fun newInstance(deductionDetails: List<LgtRemainInfo>): LgtDeductDetailViewFragment {
            val fragment = LgtDeductDetailViewFragment()
            val args = Bundle()
            args.putSerializable("deductionDetails", ArrayList(deductionDetails))
            fragment.arguments = args
            return fragment
        }
    }
}











