package com.example.moneymanagement

import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.termproject_a.Budget
import com.example.termproject_a.round
import kotlin.math.absoluteValue

/*
* generates a table to display data from each section of the budget in a table row.
* Each row has an event handler to set a "current section" variable so that when a user enters an amount/percentage
*   and presses the update button the attached function knows which BudgetSection object to take data from and
*   which of the XML table row elements needs to be updated.
* Radio buttons control whether the user input is treated as a percentage or a decimal.
* Upon initial generation (onCreate) and at the end of every update information about whether the user is over/under
* budget and by how much is calculated and the display is updated
*
* updateSection - function attached to the update button event listener
* displayAllocationInfo - gets info about whether user is over/under budget and by how much and updates the display accordingly
* populateRow - puts BudgetSection's data into the specified table row
*
* Author: Lauren Stewart
* */
class DynamicTable : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dynamic_table)

        //creates a Budget object with income param passed to this activity from BudgetFragment
        //there is validation to check that income is valid in IncomeFragment.kt so the null safe call and elvis to set a default
        //are just there so the app doesn't crash if something goes really wrong and an income isn't passed correctly
        val income = intent.extras?.getInt("income") ?: 0
        val budget = Budget(income)

        //initialize current section to one of the defaults since it has to be initialized in order to be passed to a function
        var currSection = budget.getSection("savings")
        println(budget.getSection("savings").percentage)

        //get TextView for displaying income amount and uses regex to replace the number portion with the budgets income
        val incomeDisp = findViewById<TextView>(R.id.display_incomeAmt)
        incomeDisp.text =  incomeDisp.text.replace("\\d+\\.\\d+".toRegex(), budget.income.toString())

        //replaces template string with actual information about if user is over/under budget and by how much
        displayAllocationInfo(budget)

        //Map used as a way to bind XML table row elements to BudgetSection objects - that way if you have one you can easily find the other
        val rowData = mutableMapOf<TableRow, Budget.BudgetSection>()

        //add event listener for update button
        findViewById<Button>(R.id.updateBtn).setOnClickListener{ updateSection(currSection, rowData) }

        //get XML table object
        val tl = findViewById<TableLayout>(R.id.displayTable)

        //get context
        val context = this

        //set a layout for table rows
        val layoutP = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        //loop through each section of the budget
        for (section in budget.sections)
        {
            //create new row and give it the params from above
            val tableRow = TableRow(context)
            tableRow.layoutParams = layoutP

            //add event handler to table row
            tableRow.isClickable = true
            tableRow.setOnClickListener{
                //sets currSection to the BudgetSection mapped to the row clicked
                //must be set so that the updateBtn event handler has the right object
                //to change the data and update the correct table row
                currSection = rowData[it]!!

                //updates sectionName textview with name of section (table row) chosen by the user
                //so that the user has a visual queue for which row they selected
                findViewById<TextView>(R.id.sectionName).text = currSection.name
            }

            //add current section keyed to current table row to the rowData Map
            rowData[tableRow] = section

            //populate data from object - function creates new TextViews for each column in the row if they do not exist
            //see populateRow for more detailed explanation
            populateRow(tableRow, section)

            //add row to table
            tl.addView(tableRow)

        }//end of budget sections loop
    }// end of on create



    /*
    *  updateSection retrieves user input from dataInput EditText, checks it is not null/empty and if valid checks
    *   which radio button has been selected (percentage/amount) to determine whether input should be treated as a new percent or a new amount
    *   the appropriate property in the BudgetSection passed as param 'section' is updated.
    *   the table row that displays that sections data is then retrieved from the rowData Map (TableRow that displays section data -> BudgetSection obj with said data)
    *   TableRow and BudgetSection are then passed to populateRow which updates the data in the actual table display
    * @param section - the section corresponding to the row selected by the user
    *   (section stored in currSection set from tableRow event handler)
    * @param rowData - map containing addresses to each table row and the BudgetSection obj containing the data that populated that row
    * @return unit - no specified return
    * */
    private fun updateSection(section: Budget.BudgetSection, rowData: Map<TableRow, Budget.BudgetSection>)
    {

        //if input can't be parsed to a double newData will be null this takes care of blanks and any funky input
        val newData = findViewById<EditText>(R.id.dataInput).text.toString().toDoubleOrNull()

        //if new data is null (parsing to double above failed) make toast and do not execute the rest of the function
        if (newData == null)
        {
            Toast.makeText(applicationContext, "No changes made to ${section.name}",Toast.LENGTH_SHORT).show()
        }
        else
        {
            //get radio button ids and the id of whichever button is selected at the time of the update button being pressed
            val percentBtnId = findViewById<RadioButton>(R.id.percentage).id
            val amountBtnId = findViewById<RadioButton>(R.id.amount).id
            val checkedBtnId = findViewById<RadioGroup>(R.id.inputType).checkedRadioButtonId

            //update BudgetSection object - selecting amount or percentage to update based on which radio button was checked
            when (checkedBtnId)
            {
                //make sure newData is in 0.44 form and / 100 if not (NOTE: this does still allow users to enter values over 100%)
                percentBtnId -> section.percentage = if (newData > 1) newData / 100 else newData
                amountBtnId -> section.amount = newData
            }

            //search rowData Map for value that matches section and return key (table row)
            val tableRow = rowData.filterValues { it === section }.keys.elementAt(0)

            //pass the row whose data should be updated and the section that row displays
            //(section has been updated in the above when block and so already has the updated data the tableRow needs to be updated with)
            populateRow(tableRow, section)

        }//end of else
    }//end of updateSection function


    /*
    * displayAllocationInfo is called from the function attached to the 'update' button event handler
    *   It takes the entire budget as a parameter and uses the Budget class' methods to check whether or not the
    *   user is over budget and changes the display message and text color accordingly. The display is also updated with how much the user is
    *   over/under budget.
    * NOTE: this function must be called AFTER populateRow is called or from within at the bottom of the populateRow function so that the data
    *           in Budget Obj will reflect the change to one of its section
    * @param budget - the Budget Obj for the current budget
    * @return unit - no return value specified
    * */
    private fun displayAllocationInfo(budget: Budget)
    {
        //gets the XML TextView that displays over/under budget information
        val overBudgetDisp = findViewById<TextView>(R.id.display_overBudget)

        //gets $ amount unallocated in the budget (will be negative if over budget)
        val newDispAmt = budget.getFreeAmount().absoluteValue
        val textColor: String
        val newDispText: String

        //checks if the budget has empty space (based on percentage - see Budget.kt for details)
        when (budget.hasEmptySpace()) {
            true ->
            {
                newDispText = "You have $$newDispAmt Unallocated"
                textColor = "#5cb038"

            }
            else  ->
            {
                newDispText = "You are $$newDispAmt Over Budget"
                textColor = "#FF0000"
            }
        }
        overBudgetDisp.text = newDispText
        overBudgetDisp.setTextColor(Color.parseColor(textColor))
    }

    /*
    * takes data from section passed and adds each piece to the row in a TextView
    * creates TextViews as needed - such as upon initial generation.
    * when created TextViews are given id=colPos where colPos is the index + 1 of the data it
    * displays
    *   getSectionData returns a list ("name", "0.45", "4500")
    *   so that:
    *       TextView for "name" has an id=1
    *       TextView for "0.45" has an id=2
    *       TextView for "4500" has an id=3
    * this way all TextViews are created and updated in this function and all based on the same
    * colPos' calculated from the getSectionData list
    * At the end of the function displayAllocationInfo is called which updates display with
    * over/under budget info
    *
    * @param row the XML table row to be populated
    * @param section the BudgetSection object whose data should populate the row param
    * @return unit - no specific return value specified
    * */
    private fun populateRow(row: TableRow, section: Budget.BudgetSection)
    {
        section.getSectionData().forEachIndexed { index, data ->
            val colPos = index + 1 //gives error when XML element ids are 0 indexed so table cell ids start at 1
            var tableCell = row.findViewById<TextView>(colPos)

            //if there is no existing textView for the column in the current row (as will be the case during initial table gen)
            //create it here, set id to the colPos so it can later be retrieved with this same function to update its data
            if (tableCell == null)
            {
                tableCell = TextView(this)
                tableCell.id = colPos
                row.addView(tableCell)
            }

            //set text for each column in the row
            //multiplying to get the ##.####% after parsing to double un-rounds so round function must be reapplied (located at the bottom of the Budget.kt file)
            when (colPos)
            {
                1   -> tableCell.text = data
                2   -> tableCell.text = "${round(data.toDouble() * 100, 4)}%"
                3   -> tableCell.text = "\$${data.toDouble()}"
            }//end of when
        }//end of forEachIndexed


        //updates the TextView displaying information about whether user is over/under budget
        //re-calculates amount over/under budget and if a switch has been made from under to over budget or vice versa
        //text message will also switch accordingly (it actually does it every time that's just the only time you see it)
        //gets budget object the section calling the method belongs to - see Budget.kt for details
        displayAllocationInfo(section.getBudgetObj())

    }//end of populateRow function
}//end of dynamicTable class