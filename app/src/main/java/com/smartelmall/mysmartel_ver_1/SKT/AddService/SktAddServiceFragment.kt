package com.smartelmall.mysmartel_ver_1.SKT.AddService

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smartelmall.mysmartel_ver_1.R
import kotlinx.coroutines.*
import java.net.URL
import java.nio.charset.Charset

class SktAddServiceFragment : Fragment() {

    private lateinit var phoneNumber: String
    private lateinit var svcNum: String
    private lateinit var ifClCdR2: String

    lateinit var textView: TextView

    private lateinit var downButton: ImageButton

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_skt_add_service, container, false)

        downButton = view.findViewById(R.id.btn_pgDown)
        downButton.setOnClickListener {
            animateFragmentOut(view)
        }
        return view
    }

    private fun animateFragmentOut(view: View) { // 슬라이드 다운 애니메이션 효과
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down)
        transaction.remove(this@SktAddServiceFragment)
        transaction.commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)

        phoneNumber = arguments?.getString("phoneNumber") ?: ""
        svcNum = phoneNumber // Assuming phoneNumber is the same as svcNum based on your description
        ifClCdR2 = "R2"
        textView = view.findViewById(R.id.textView)

        // Call the API to get service details
        SktFetchAddServiceDetail()
    }

    private fun SktFetchAddServiceDetail() {
        // Use viewLifecycleOwner.lifecycleScope instead of GlobalScope
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val apiUrl = "https://www.mysmartel.com/api/sktGetInfo.php?svcNum=$svcNum&ifClCd=$ifClCdR2"
            try {
                val url = URL(apiUrl)
                val connection = url.openConnection()
                val contentType = connection.contentType
                val charset = contentType.split("charset=")
                    .lastOrNull { it.isNotBlank() }
                    ?.let { Charset.forName(it) }
                    ?: Charset.defaultCharset()

                val response = connection.getInputStream().bufferedReader(charset).use { it.readText() }

                // No need to convert the encoding, as we use detected charset from response
                val responseData = response

                // Update the UI in the main thread and call displayData function with the response data
                withContext(Dispatchers.Main) {
                    displayData(responseData)
                    Log.d("SktAddServiceFragment", "$responseData")
                }
            } catch (e: Exception) {
                Log.e("SktAddServiceFragment", "Error fetching service details: ${e.message}", e)
            }
        }
    }

    private fun displayData(data: String) {
        CoroutineScope(Dispatchers.Main).launch {
            // Remove the first 60 bytes from the data and create trueValue
            val trueValue = data.substring(60)

            // Helper function to consume bytes from the string
            var currentIndex = 0
            fun consumeBytes(count: Int): String {
                val subList = mutableListOf<Char>()
                var byteCounter = 0

                while (byteCounter < count && currentIndex < trueValue.length) {
                    val char = trueValue[currentIndex]
                    val byteSize = if (char.toInt() in 0xAC00..0xD7A3) 2 else 1

                    if (byteCounter + byteSize <= count) {
                        subList.add(char)
                        byteCounter += byteSize
                    } else {
                        break
                    }
                    currentIndex++
                }

                return subList.joinToString(separator = "")
            }

            // Parse the trueValue according to the format
            val opClCd = consumeBytes(1)
            val opTypCd = consumeBytes(2)
            val svcNum = consumeBytes(12)
            val svAcntNum = consumeBytes(11)
            val prodRecCnt = consumeBytes(5).trim().toInt()

            Log.d("SktAddServiceFragment", "OpClCd: $opClCd")
            Log.d("SktAddServiceFragment", "OpTypCd: $opTypCd")
            Log.d("SktAddServiceFragment", "SvcNum: $svcNum")
            Log.d("SktAddServiceFragment", "SvAcntNum: $svAcntNum")
            Log.d("SktAddServiceFragment", "ProdRecCnt: $prodRecCnt")

            // Iterate through the billing items

            var productList = mutableListOf<SktAddServiceItem.Product>()

            val stringBuilder = StringBuilder()
            for (i in 0 until prodRecCnt) {
                val prodId = consumeBytes(10)
                val prodScrbDt = consumeBytes(10)
                val prodNm = consumeBytes(50)
                val prodFeeAmt = consumeBytes(10)

                val displayProdFee = if (prodFeeAmt.trim() == "0" || prodFeeAmt.trim().isEmpty()) "무료" else prodFeeAmt

                Log.d("SktAddServiceFragment", "Product Id: $prodId")
                Log.d("SktAddServiceFragment", "Product Subscribe Date : $prodScrbDt")
                Log.d("SktAddServiceFragment", "Product Name: $prodNm")
                Log.d("SktAddServiceFragment", "Product Fee Amount: $prodFeeAmt")

                productList.add(SktAddServiceItem.Product(prodId,prodScrbDt,prodNm,displayProdFee))

                Log.d("ProductLengths",
                    "        Product ID Length: ${prodId.length}\n" +
                        "                      Product Subscribe Date Length: ${prodScrbDt.length}\n" +
                        "                      Product Name Length: ${prodNm.length}\n" +
                        "                      Product Fee Amount Length: ${prodFeeAmt.length}\n")


                //stringBuilder.append("가입일: $prodScrbDt\n")
                stringBuilder.append("$prodNm\n\n")
                stringBuilder.append("${displayProdFee.padStart(55)}\n\n\n\n")
            }
            textView.text = stringBuilder.toString()

            var sktAddServiceData = SktAddServiceItem(opClCd,opTypCd,svcNum,svAcntNum,prodRecCnt.toInt(),productList)


            recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = SktAddServiceAdapter(sktAddServiceData.products)
            }

            // Log to show the length of each parsed field
            Log.d("ParsedData",
                "            opClCd: ${opClCd.length}\n" +
                    "                      opTypCd: ${opTypCd.length}\n" +
                    "                      svcNum: ${svcNum.length}\n" +
                    "                      svAcntNum: ${svAcntNum.length}\n" +
                    "                      prodRecCnt: ${prodRecCnt}\n")
        }
    }


}



