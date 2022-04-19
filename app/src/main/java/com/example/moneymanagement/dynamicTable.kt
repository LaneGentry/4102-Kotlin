package com.example.termproject_a

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.moneymanagement.R
import kotlin.math.absoluteValue


class dynamicTable : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dynamic_table)

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

        println("HERE")
        println(this)

       val tl = findViewById<TableLayout>(R.id.displayTable)
       val tr = TableRow(this)
        val context = this
        val layoutP = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        tr.layoutParams = layoutP
        var tv = TextView(context)
        var tv2 = TextView(context)
        tv.setText("TESTING")
        tv2.setText("TEST 2")
        tr.addView(tv)
        println("ID: ${tr.id}")
        tr.addView(tv2)
        tl.addView(tr)

        val button = Button(this)
        button.text = "Simple Button"
        button.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)


        var rowData = mutableMapOf<TableRow, Budget.BudgetSection>()

        for (section in mmm.sections)
        {
            val tableRow = TableRow(context)
            tableRow.layoutParams = layoutP

            //add event handler to table row
            tableRow.isClickable = true
            tableRow.setOnClickListener{
                Toast.makeText(applicationContext, "You clicked me.", Toast.LENGTH_SHORT).show()
                println(rowData[it])
                val section = rowData[it]
                val tableRow = it
                section?.percentage = 0.42
                var count = 0
                for(data in section!!.getSectionData())
                {
                    count++
                    var tv = tableRow.findViewById<TextView>(count)
                    //var tv = TextView(context)
                    tv.setText(data)
                    //tableRow.addView(tv)
                }
            }

            //add current section keyed to current table row to the Map
            rowData[tableRow] = section

            //populate data from object
            var count = 0
            for(data in section.getSectionData())
            {
                var tv = TextView(context)
                tv.setText(data)
                count++
                tv.id = count
                tableRow.addView(tv)
            }

            //add row to table
            tl.addView(tableRow)
        }
    }
}