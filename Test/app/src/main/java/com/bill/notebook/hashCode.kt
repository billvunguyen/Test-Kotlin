package com.bill.notebook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
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

    var textViewStatusGood: TextView? = null
    var textViewStatusInfected: TextView? = null

    var textViewTotalScan: TextView? = null
    var textViewTotalPositives: TextView? = null

    var hashString: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hash_code)

        val getApplicationString:String = intent.getStringExtra("textHash").toString()

        val textViewFileName: TextView = findViewById(R.id.fileName)

        this.fileName = getApplicationString
        textViewFileName.text= "Hash : $fileName"
        Log.d("abc",fileName)

        val btnTestApplication = findViewById<Button>(R.id.btn_test)
        btnTestApplication.setOnClickListener {
            securityCheck()
        }

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        hashString = findViewById(R.id.hashString)

        textViewTotalScan = findViewById(R.id.TotalScan)
        textViewTotalPositives = findViewById(R.id.TotalScanPositives)

        textViewStatusGood = findViewById(R.id.status_good)
        textViewStatusInfected = findViewById(R.id.status_infected)

        textViewStatusGood?.visibility = View.GONE
        textViewStatusInfected?.visibility = View.GONE

    }

    // Calculate hash
    fun md5(str: String): ByteArray = MessageDigest.getInstance("MD5").digest(str.toByteArray(UTF_8))
    fun ByteArray.toHex() = joinToString(separator = "") { byte -> "%02x".format(byte) }

    private fun securityCheck() {
        // file check
//        var fileNameTest:String = "[Ljava.io.File;@ae13725"
        var hash = (md5(fileName).toHex())
        Log.d("hashcode",hash)
        Log.d("hashcode",fileName)

        hashString?.text = "Hash String : $hash"

        var totalScan : Int = 0
        var positivesScan : Int = 0

        // Request to Virus Total
        requestQueue = Volley.newRequestQueue(this)

        val url = "https://www.virustotal.com/vtapi/v2/file/report?apikey=$apiKey&resource=$hash&allinfo=false"
        val request = JsonObjectRequest(com.android.volley.Request.Method.GET, url, null, Response.Listener {
                response ->try {
            val jsonObject = JSONTokener(response.toString()).nextValue() as JSONObject

            // Total scan
            val total = jsonObject.getString("total")
            Log.i("Total Scan ", total)
            textViewTotalScan?.text = "Total Scan $total"
            totalScan = total.toInt()

            // Positives scan
            val positives = jsonObject.getString("positives")
            Log.i("Positives Scan : ", positives)
            textViewTotalPositives?.text = "Total Positives $positives"
            positivesScan = positives.toInt()


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        }, Response.ErrorListener { error -> error.printStackTrace() })
        requestQueue?.add(request)

        // Waiting Virustotal's response
        GlobalScope.launch {
            delay(2000L)
            var result: Boolean = calculateSafetyLevel(totalScan,positivesScan)
            // Fix thread conflict
            Handler(Looper.getMainLooper()).post(Runnable {
                if (result) {
                    Log.d("secu","Good")
                    textViewStatusGood?.visibility = View.VISIBLE

                } else if (!result && totalScan > 0) {
                    Log.d("secu","Infected")
                    textViewStatusInfected?.visibility = View.VISIBLE
                }
                else {
                    Log.d("secu", "Error")
                    textViewStatusInfected?.text = "ERROR"
                    textViewStatusInfected?.visibility = View.VISIBLE
                }
            })
        }
    }

    private fun calculateSafetyLevel(total: Int, positives: Int): Boolean {
        var result: Double = 0.0
        if (total<= 0) {
            return false
        } else {
            result = (positives / total).toDouble()
            if (result < 0.5) {
                return true
            } else {
                return false
            }
        }
    }

}


