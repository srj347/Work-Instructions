package com.example.workinstructions

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workinstructions.data.RepositoryWI
import com.example.workinstructions.listeners.OnItemClickListener
import com.example.workinstructions.model.Step
import com.example.workinstructions.model.WorkInstruction

class WIActivity : AppCompatActivity(), OnItemClickListener {

    var wiList = ArrayList<WorkInstruction>()
    lateinit var wiRecyclerView: RecyclerView
    var wiAdapter: WIAdapter?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wiactivity)

        wiRecyclerView = findViewById(R.id.wi_recycler)

        initWIData()
    }

    private fun initRecylerView() {
        wiAdapter = WIAdapter(wiList, this)
        wiRecyclerView.layoutManager = LinearLayoutManager(this)
        wiRecyclerView.adapter = wiAdapter
    }

    private fun initWIData() {
        val repositoryWI = RepositoryWI()
        repositoryWI.fetchAllWorkInstruction(
            { workInstructions: List<WorkInstruction?> ->
                wiList = workInstructions as ArrayList<WorkInstruction>
                initRecylerView()
            }
        ) { exception: Exception ->
            Log.d(
                "CurrentDebugg",
                "Failure => $exception"
            )
        }
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, WIDetailActivity::class.java)
        val currentSteps: ArrayList<Step> = ArrayList(wiList[position].steps)
        intent.putExtra("steps", currentSteps)
        startActivity(intent)
    }
}