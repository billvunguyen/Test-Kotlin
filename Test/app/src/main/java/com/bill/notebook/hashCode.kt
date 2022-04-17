package com.bill.notebook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import okhttp3.OkHttpClient
import okhttp3.Request
import java.security.MessageDigest
import kotlin.math.log
import kotlin.text.Charsets.UTF_8
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener


class hashCode : AppCompatActivity() {

    private var apiKey: String = "a761671eabbe821281c9b8c3fccd149905f4df3a832605aa7f821b7a45dde6a6"

    var fileName:String = ""

    private var requestQueue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hash_code)

        val getApplicationString:String = intent.getStringExtra("installedApplication").toString()

        val textViewFileName: TextView = findViewById(R.id.fileName)

        this.fileName = getApplicationString
        textViewFileName.text= "Hash : $getApplicationString"

        val btnTestApplication = findViewById<Button>(R.id.btn_test)
        btnTestApplication.setOnClickListener {
            securityCheck()
        }

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

    }

    fun md5(str: String): ByteArray = MessageDigest.getInstance("MD5").digest(str.toByteArray(UTF_8))
    fun ByteArray.toHex() = joinToString(separator = "") { byte -> "%02x".format(byte) }

    private fun securityCheck() {
        val test = (md5("abc").toHex())
        Log.d("hashcode",test)

        var totalScan : Int = 0
        var positivesScan : Int = 0

        val textViewTotalScan: TextView = findViewById(R.id.TotalScan)
        val textViewTotalPositives: TextView = findViewById(R.id.TotalScanPositives)

        // Request to Virus Total
        requestQueue = Volley.newRequestQueue(this)

        val url = "https://www.virustotal.com/vtapi/v2/file/report?apikey=$apiKey&resource=$test&allinfo=false"
        val request = JsonObjectRequest(com.android.volley.Request.Method.GET, url, null, Response.Listener {
                response ->try {
            val jsonObject = JSONTokener(response.toString()).nextValue() as JSONObject

            // Total scan
            val total = jsonObject.getString("total")
            Log.i("Total Scan ", total)
            textViewTotalScan.text = "Total Scan $total"
            totalScan = total.toInt()

            // Positives scan
            val positives = jsonObject.getString("positives")
            Log.i("Positives Scan : ", positives)
            textViewTotalPositives.text = "Total Positives $positives"
            positivesScan = positives.toInt()


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        }, Response.ErrorListener { error -> error.printStackTrace() })
        requestQueue?.add(request)

        GlobalScope.launch {
            delay(2000L)
            Log.d("abc", totalScan.toString());
            Log.d("abc", positivesScan.toString());
            var result: Boolean = calculateSafetyLevel(totalScan,positivesScan)
            if (result) {
                Log.d("secu","Good")
            } else {
                Log.d("secu", "NO")
            }
        }

    }

    private fun calculateSafetyLevel(total: Int, positives: Int): Boolean {

        var result: Double = 0.0
        result = (positives / total).toDouble()
        if(result < 0.5) {
            return true
        } else {
            return false
        }
    }

}


