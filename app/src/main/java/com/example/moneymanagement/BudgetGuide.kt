package com.example.moneymanagement
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BudgetGuide : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.budget_guide)

        //Strings and array that store text to be displayed
        val title = "How to create a budget"
        val text = "It can be very useful to see and understand where your money is going" +
                "each month. Creating a budget can help you feel more in control of your " +
                "finances and make saving easier. Below is a list of how you can create a budget" +
                "with our application."
        val list = arrayOf("Select Start New", "Enter your income", "Enter your expenses in the " +
                "required fields", "Select Calculate and await the results", "Check the table for " +
                "the percentage of your income and amount", "Compare that to the recommended budgeting" +
                "percentages", "Make adjustments to budget to better fit recommendations")

        /// Declaring viewText from the layout
        val viewText = findViewById<TextView>(R.id.textView)
        val viewText2 = findViewById<TextView>(R.id.textView2)

        //Viewing text from the two strings
        viewText.text = title
        viewText2.text = text

        //Adapter to view array
        val adapter = ArrayAdapter(this,
            R.layout.list_item, list)

        //Viewing text from the array
        val listView: ListView = findViewById(R.id.listview)
        listView.setAdapter(adapter)

    }
}