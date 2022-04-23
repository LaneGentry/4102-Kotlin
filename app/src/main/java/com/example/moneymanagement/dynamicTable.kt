package com.example.termproject_a

import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.example.moneymanagement.R
import kotlin.math.absoluteValue


/*GENERAL TO DO'S:
*    add an element to the XML to display whether or not user is over budget -DONE
*        add functionality to check this (see methods in Budget.kt) when user updates section values -DONE
*        (see updateBtn event listener)
*            if over budget update element with $ amount over budget -DONE
*   add an element to the XML to display the income -DONE
*       add functionality for income to be changed
*           add function to Budget to re calc amounts
*           current idea: easy but less intuitive is just an editable decimal numeric textedit with an event listener attached
*   add functionality for adding and removing sections
*       adding it on this screen will probably make it very cluttered but I (LK) don't know shit about how to make multiple screens
*       interact and pass information so if y'all have figured it out it would be awesome otherwise it'll end up on this page
*
*/
class dynamicTable : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dynamic_table)

        //just to have a budget object to test with
        val income = intent.extras!!.getInt("income")
        val budget = Budget(income)

        //initialize current section to one of the defaults since it has to be initialized in order to be passed to a function
        //TO DO: currently relies on there always being a 'savings' section in the budget - change to use whatever the first budget section
        var currSection = budget.getSection("savings")
        println(budget.getSection("savings").percentage)

        //FOR TESTING ONLY - plz do not leave this here
        budget.getSection("savings").percentage = 0.01
        println("unallocated: ${budget.getFreeAmount()} ")

        //get TextView for displaying income amount and use regex to replace the number portion with the budgets income
        val incomeDisp = findViewById<TextView>(R.id.display_incomeAmt)
        incomeDisp.text =  incomeDisp.text.replace("\\d+\\.\\d+".toRegex(), budget.income.toString())

        //get TextView that displays if the user is over budget/ amount unallocated and change the '$0.0 Unallocated' template to have the actual budgets unallocated amount
        val overBudgetDisp = findViewById<TextView>(R.id.display_overBudget)


        displayAllocationInfo(budget)

        //bind table rows to BudgetSection objects - done once on initial table gen
        var rowData = mutableMapOf<TableRow, Budget.BudgetSection>()

        //add event listener for update button
        findViewById<Button>(R.id.updateBtn).setOnClickListener { updateSection(currSection, rowData) }

        //TO DO: when radio button choice changes change the hint for EditText R.id.dataInput to reflect that change
        //EX: amount button checked -> hint = Amount
        //Here is the on change listener for the radio button group it just needs the function
        //findViewById<RadioGroup>(R.id.inputType).setOnCheckedChangeListener()


        //get XML table object
        val tl = findViewById<TableLayout>(R.id.displayTable)

        //get context
        val context = this

        //set a layout for table rows (not sure if it's a good layout but it seems to work for now)
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
                //TO DO: proper null checking - it could actually be null
                currSection = rowData[it]!!

                //updates sectionName textview with name of section (table row) chosen by the user
                //so that the user has a visual queue for which row they selected
                findViewById<TextView>(R.id.sectionName).text = currSection.name
            }

            //add current section keyed to current table row to the rowData Map
            rowData[tableRow] = section

            //populate data from object - function creates new TextViews for the row as needed
            //see populateRow for more detailed explanation
            populateRow(tableRow, section)

            //TESTING: println("AFTER POP: ${tableRow.children}")
            //TESTING: println(section.getSectionData()[1])

            //add row to table
            tl.addView(tableRow)

        }//end of budget sections loop
    }// end of on create



    /*
    *  updateSection retrieves user input from datainput EditText, checks it is not null/empty and if valid checks
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
        println("updateSection is updating the section: $section")

        //if input can't be parsed to a double newData will be null this takes care of blanks and any funky input
        val newData = findViewById<EditText>(R.id.dataInput).text.toString().toDoubleOrNull()
        //println("newData: $newData")

        //if new data is null (parsing to double above failed) make toast and do not execute the rest of the function
        //TO DO: add proper toast message if input is left blank
        if (newData == null)
        {
            Toast.makeText(applicationContext, "Empty",Toast.LENGTH_SHORT).show()
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
                percentBtnId -> section.percentage = newData //TO DO: make sure newData is in 0.44 form and / 100 if not
                amountBtnId -> section.amount = newData
                else -> println("NOOOOOOOOO")
            }
            //println("section after update:\n$section")
            //println(rowData.filterValues { it === section }.toString())

            //search rowData Map for value that matches section and return key (table row)
            val tableRow = rowData.filterValues { it === section }.keys.elementAt(0)
            //println("TABLE ROW: ${tableRow}")

            //pass the row whose data should be updated and the section that row displays
            //(section has been updated in the above when block and so already has the updated data tableRow needs to be updated with)
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
        var textColor = "#000000"
        val newDispText: String

        //checks if the budget has empty space (based on percentage - see Budget.kt for details)
        //this function newDispAmt
        when (budget.hasEmptySpace()) {
            true ->
            {
                //println("HAS EMPTY SPACE")
                newDispText = "You have $$newDispAmt Unallocated"
                textColor = "#5cb038"

            }
            else  ->
            {
                //println("NO EMPTY SPACE")
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
    fun populateRow(row: TableRow, section: Budget.BudgetSection)
    {
        section.getSectionData().forEachIndexed { index, data ->
            val colPos = index + 1 //gives error when ids are 0 indexed so table cell ids start at 1
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
            //multiplying to get the ##.####% un rounds so round function must be reapplied (bottom of the Budget.kt file)
            when (colPos)
            {
                1   -> tableCell.text = data
                2   -> tableCell.text = "${round(data.toDouble() * 100, 4)}%"
                3   -> tableCell.text = "\$${data.toDouble()}"
                else -> println("turns out there are consequences to your actions :(")
            }//end of when
        }//end of forEachIndexed


        //updates the TextView displaying information about whether user is over/under budget
        //re-calculates amount over/under budget and if a switch has been made from under to over budget or vice versa
        //text message will also switch accordingly (it actually does it every time that's just the only time you see it lol)
        //gets budget object the section calling the method belongs to - see Budget.kt for details
        //displayAllocationInfo(section.getBudgetObj())
        displayAllocationInfo(section.getBudgetObj())

    }//end of populateRow function
}//end of dynamicTable class