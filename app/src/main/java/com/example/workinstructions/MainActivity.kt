package com.example.workinstructions

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.workinstructions.data.WIRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var wiRepository: WIRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        @SuppressLint("MissingInflatedId", "LocalSuppress") val tv = findViewById<TextView>(R.id.tv)
        @SuppressLint("MissingInflatedId", "LocalSuppress") val prompt =
            findViewById<TextView>(R.id.prompt)
        @SuppressLint("MissingInflatedId", "LocalSuppress") val wi = findViewById<TextView>(R.id.wi)

        tv.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                coroutineScope.launch {
//                    try{
//                        val database = MongoDBClient.setupConnection(
//                            "Innovapptive",
//                            MongoDBClient.CONNECTION_STRING
//                        )
////                        val database = MongoDBClient.connect()
//                        if (database != null) {
//                            wiRepository = WIRepository(database)
//                            val list = wiRepository.fetchInstructions()
//                            Log.d("CurrentDebugg", list.toList().toString())
//                        } else {
//                            Log.d("CurrentDebugg", "Instructions failure")
//                        }
//                    }
//                    catch (e: Exception){
//                        e.printStackTrace()
//                    }
                    val wiRepo = WIRepository()
                    wiRepo.fetchAllWorkInstruction(
                        onSuccess = { workInstructions ->
                            Log.d("CurrentDebugg", "Success => ${workInstructions}")
                        },
                        onFailure = { exception ->
                            Log.d("CurrentDebugg", "Failure => ${exception}")
                        }
                    )

                }
            }
        })

        prompt.setOnClickListener{
            startActivity(Intent(this@MainActivity, WIChatActivity::class.java))
        }
        wi.setOnClickListener {
            startActivity(Intent(this@MainActivity, WIActivity::class.java))
        }
    }
}