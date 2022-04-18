package com.example.moneymanagement
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SavingReasons : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.saving_reasons)

        val title = "Reasons that you Should Save!"
        val para1 = "Everyone should try to set aside money from their income to be saved for many " +
                "different reasons. Saving can be done personally or by opening a savings account, " +
                "which can be really useful as it will grow over time. It can be difficult to save" +
                "with spending too much money on wants and credit being so easy to get these days." +
                "Though, saving money can be an important part of your life as it provides a" +
                "level of security and allows for you to make larger purchases such as a house" +
                "or a new car."
        val para2 = "Firstly, saving provides security for any emergencies that arise. There can be " +
                "many emergencies that come throughout your life, namely medical emergencies, family " +
                "emergencies, car or home repairs, and much more. It is very useful to have money set" +
                "aside to be able to afford these expenses when they do arise without having to go into " +
                "debt. Saving money in general is good for emergencies, but setting aside emergency funds " +
                "even within that savings can be even better. Having money available to afford these " +
                "unexpected expenses will save you the headache."
        val para3 = "Next, saving money can also be put forward in ways to improve your life such as purchasing " +
                "a new house or car. Purchasing a house will probably be the largest purchase of your life and" +
                "the amount of money that is needed will definitely require you to set aside money to make the " +
                "upfront cost and make the subsequent payments. This also applies for a car, but purchasing a car " +
                "is something more commonly done every few years and at less expense than a house. There are also " +
                "other big-ticket purchases to consider such as a boat, camper/RV, housing appliances, and more. " +
                "You also must consider the upkeep costs that come over time with these expenses. Saving can be " +
                "very important to allow you to make larger and more significant financial decisions to change your " +
                "lifestyle."
        val para4 = "There are many other important reasons to save such as traveling, investing, retirement, planning " +
                "for children schooling, financial independence, and more. These are just two of the main reason that " +
                "I wished to touch on in this document as they are very important reasons to save money. In conclusion " +
                "saving is very important in many aspects, namely pertaining to the future, to live the lifestyle that " +
                "you wish and being prepared for the unexpected that can happen at anytime. I hope that this document " +
                "has made you consider setting aside money whether in a personal safe or in a bank account to have at " +
                "the ready for whenever you need it."

        /// Declaring viewText from the layout
        val viewText = findViewById<TextView>(R.id.textView)
        val viewText2 = findViewById<TextView>(R.id.textView2)
        val viewText3 = findViewById<TextView>(R.id.textView3)
        val viewText4 = findViewById<TextView>(R.id.textView4)
        val viewText5 = findViewById<TextView>(R.id.textView5)

        //Viewing text from the two strings
        viewText.text = title
        viewText2.text = para1
        viewText3.text = para2
        viewText4.text = para3
        viewText5.text = para4
    }
}