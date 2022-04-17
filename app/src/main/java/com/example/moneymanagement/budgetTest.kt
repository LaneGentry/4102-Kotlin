package com.example.termproject_a

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.moneymanagement.R
import java.lang.Exception
import kotlin.math.absoluteValue

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mmm = Budget(13000)
        val BT = "budgetTest"
        mmm.getSection("savings").amount = 33.0

        //use named argument annotation (param name = param value to be passed) when leaving out some of the optional params
        mmm.addSection("amount based", amount = 54.8)
        mmm.addSection("percentage based", percentage = 0.12)
        Log.i(BT, mmm.toString())
        try
        {
            mmm.getSection("savings").amount = 44.0
            //mmm.addSection("savings", .25)
            //mmm.addSection("bad section", 0.323, 683.0)
            mmm.getSection("not a valid section")

        }
        catch (e: Exception)
        {
            println(e.message)
        }

        mmm.removeSection("housing")
        Log.i(BT, "\n ${mmm.toString()}")

        Log.i(BT, "\n ${mmm.getSection("utilities").toString()}")

        if (mmm.hasEmptySpace())
        {
            Log.i(BT, "has ${mmm.getFreePercentage() * 100} space")
            Log.i(BT, "\$${mmm.getFreeAmount()} unused")
        }
        else
        {
            Log.i(BT, "Full: ${mmm.getFreePercentage() * 100}% free")
            Log.i(BT, "\$${mmm.getFreeAmount().absoluteValue} over budget")
        }
    }
}