package com.bill.notebook

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.lang.StringBuilder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import android.os.Environment
import android.view.View
import com.google.android.material.textfield.TextInputEditText
import java.io.File


class MainActivity : AppCompatActivity() {

    lateinit var listView: ListView
    var arrayAdapter: ArrayAdapter<*>? = null
    var inputText: TextInputEditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "Notebook"
        listView = findViewById(R.id.listView)

        inputText = findViewById(R.id.input_text)

        var btnView = findViewById(R.id.btn_viewList) as Button
        var btnSubmit = findViewById(R.id.btn_submit) as Button

        btnView.setOnClickListener {
            inputText?.visibility = View.GONE
            btnSubmit?.visibility = View.GONE
            installedApps()
        }
        btnSubmit.setOnClickListener {
            submitText()
        }

    }


    // Get installed Apps
    private fun installedApps() {
        val textView: TextView = findViewById(R.id.textView)
        val list = packageManager.getInstalledPackages(0)
        for (i in list.indices) {
            val packageInfo = list[i]
            if (packageInfo!!.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                val appName = packageInfo.applicationInfo.loadLabel(packageManager)
                Log.e("App List$i", appName.toString())

                arrayAdapter = ArrayAdapter(this,
                    R.layout.support_simple_spinner_dropdown_item, list as List<*>
                )
                listView.adapter = arrayAdapter


                // Handing click event in ListView to change Activity
                listView.setOnItemClickListener { parent, _, position, _ ->
                    val selectedItem = parent.getItemAtPosition(position)

                    val textHash = selectedItem.toString();
                    textView.text= "Hash $selectedItem"

                    val intent = Intent(this, hashCode::class.java)
                    intent.putExtra("textHash", textHash)
                    startActivity(intent)

                }
            }
        }
    }

    private fun submitText() {

        val textHash = inputText?.text.toString();
        Log.d("submit", textHash)
        val intent = Intent(this, hashCode::class.java)
        intent.putExtra("textHash", textHash)
        startActivity(intent)
    }


// Second option calculate hash
    fun md5(s: String): String {
        val MD5 = "MD5"
        try {
            // Create MD5 Hash
            val digest = MessageDigest
                .getInstance(MD5)
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String
            val hexString = StringBuilder()
            for (aMessageDigest in messageDigest) {
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while (h.length < 2) h = "0$h"
                hexString.append(h)
            }
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }
}