package com.smartel.mysmartel_ver_1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mysmartel_ver_1.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.Charset

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

                // Update the UI in the main thread
                launch(Dispatchers.Main) {
                    textView.text = responseData
                }
            } catch (e: Exception) {
                Log.e("SktAddServiceFragment", "Error fetching service details: ${e.message}", e)
            }
        }
    }
}


