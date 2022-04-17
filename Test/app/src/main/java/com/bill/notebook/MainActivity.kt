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
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    lateinit var listView: ListView
    var arrayAdapter: ArrayAdapter<*>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Notebook"
        listView = findViewById(R.id.listView)
        var btnView = findViewById(R.id.btn_viewList) as Button
        btnView.setOnClickListener {
            installedApps()
        }
    }


    // Get installed Apps
    private fun installedApps() {
        val textView: TextView = findViewById(R.id.textView)
        val list = packageManager.getInstalledPackages(0)
        for (i in list.indices) {
            val packageInfo = list[i]
            if (packageInfo!!.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                val appName = packageInfo.applicationInfo.loadLabel(packageManager).toString()
                Log.e("App List$i", appName)

                arrayAdapter = ArrayAdapter(this,
                    R.layout.support_simple_spinner_dropdown_item, list as List<*>
                )
                listView.adapter = arrayAdapter


                // Handing click event in ListView to change Activity
                listView.setOnItemClickListener { parent, _, position, _ ->
                    val selectedItem = parent.getItemAtPosition(position)
                    val testhash = selectedItem.toString();
                    textView.text= "Hash $selectedItem"

                    val intent = Intent(this, hashCode::class.java)
                    intent.putExtra("installedApplication", testhash)
                    startActivity(intent)

                }
            }
        }
    }
}