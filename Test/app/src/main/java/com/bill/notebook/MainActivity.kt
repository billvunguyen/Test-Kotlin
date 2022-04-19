package com.bill.notebook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
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
//            installedApps()
            getDownloadedFile()
        }
        btnSubmit.setOnClickListener {
            submitText()
        }

    }

    // Get Downloaded File
    private fun getDownloadedFile() {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
        Log.d("Files", "Path: $path")
        val directory = File(path)
        val files = directory.listFiles()
        if (directory.canRead() && files != null) {
            Log.d("Files", "Size: " + files.size)
            val list: MutableList<File> = ArrayList()
            for (file in files) {
                list.add(file)
                arrayAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, list as List<*>)
                listView.adapter = arrayAdapter

                Log.d("Files",file.absolutePath)
                Log.d("Files", file.name)

                // Handing click event in ListView to change Activity
                listView.setOnItemClickListener { parent, _, position, _ ->
                    val selectedItem:File = parent.getItemAtPosition(position) as File

                    var hashFile = md5(selectedItem)
                    Log.d("md5",hashFile)

                    val intent = Intent(this, securityCheck::class.java)
                    intent.putExtra("textHash", hashFile)
                    intent.putExtra("fileName",selectedItem.toString())
                    startActivity(intent)

                }
            }
        } else Log.d("Null", "it is null")
    }

    private fun submitText() {

        val textHash = inputText?.text.toString();
        Log.d("submit", textHash)
        var hashString = (md5String(textHash).toHex())
        Log.d("submit", hashString)
        val intent = Intent(this, securityCheck::class.java)
        intent.putExtra("textHash", hashString)
        intent.putExtra("fileName",textHash)
        startActivity(intent)
    }

    // Calculate hash text
    private fun md5String(str: String): ByteArray = MessageDigest.getInstance("MD5").digest(str.toByteArray(
        Charsets.UTF_8
    ))
    private fun ByteArray.toHex() = joinToString(separator = "") { byte -> "%02x".format(byte) }


    //Calculate hash a file
    private fun md5(s: File): String {
        val MD5 = "MD5"
        try {
            // Create MD5 Hash
            val bytes = ByteArray(s.length().toInt())
            val digest = MessageDigest
                .getInstance(MD5)
            digest.update(bytes)
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

    // Get installed Apps
//    private fun installedApps() {
//        val textView: TextView = findViewById(R.id.textView)
//        val list = packageManager.getInstalledPackages(0)
//        for (i in list.indices) {
//            val packageInfo = list[i]
//            if (packageInfo!!.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
//                val appName = packageInfo.applicationInfo.loadLabel(packageManager)
//                Log.e("App List$i", appName.toString())
//
//                arrayAdapter = ArrayAdapter(this,
//                    R.layout.support_simple_spinner_dropdown_item, list as List<*>
//                )
//                listView.adapter = arrayAdapter
//
//
//                // Handing click event in ListView to change Activity
//                listView.setOnItemClickListener { parent, _, position, _ ->
//                    val selectedItem = parent.getItemAtPosition(position)
//
//                    val textHash = selectedItem.toString();
//                    textView.text= "Hash $selectedItem"
//
//                    val intent = Intent(this, hashCode::class.java)
//                    intent.putExtra("textHash", textHash)
//                    startActivity(intent)
//
//                }
//            }
//        }
//    }
}