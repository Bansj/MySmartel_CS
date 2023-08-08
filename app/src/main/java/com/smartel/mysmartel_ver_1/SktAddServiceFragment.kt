package com.smartel.mysmartel_ver_1

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mysmartel_ver_1.R
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.Charset
import java.util.Collections.min
import kotlin.math.min

class SktAddServiceFragment : Fragment() {

    private lateinit var phoneNumber: String
    private lateinit var svcNum: String
    private lateinit var ifClCd: String

    lateinit var textView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_skt_add_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        phoneNumber = arguments?.getString("phoneNumber") ?: ""
        svcNum = phoneNumber // Assuming phoneNumber is the same as svcNum based on your description
        ifClCd = "R2"
        textView = view.findViewById(R.id.textView)

        // Call the API to get service details
        getServiceDetails()
    }

    private fun getServiceDetails() {
        // Use viewLifecycleOwner.lifecycleScope instead of GlobalScope
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val apiUrl = "https://www.mysmartel.com/api/sktGetInfo.php?svcNum=$svcNum&ifClCd=$ifClCd"
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

                Log.d("ProductLengths",
                    "        Product ID Length: ${prodId.length}\n" +
                        "                      Product Subscribe Date Length: ${prodScrbDt.length}\n" +
                        "                      Product Name Length: ${prodNm.length}\n" +
                        "                      Product Fee Amount Length: ${prodFeeAmt.length}\n")


                //stringBuilder.append("가입일: $prodScrbDt\n")
                stringBuilder.append("$prodNm\n\n")
                stringBuilder.append("${displayProdFee.padStart(75)}\n\n\n\n")
            }
            textView.text = stringBuilder.toString()

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



