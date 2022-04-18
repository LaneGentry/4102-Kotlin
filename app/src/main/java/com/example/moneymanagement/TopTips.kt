package com.example.moneymanagement
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import java.io.*

class TopTips : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.top_tips)

        /// Declaring viewText from the layout
        val viewText = findViewById<TextView>(R.id.textView)

        // Stores text from the file
        val output: String

        // Reads data from the text file
        val myInputStream: InputStream

        // Try to open the text file, reads the data and stores it in the string
        try {
            myInputStream = assets.open("TopTips.txt")
            val size: Int = myInputStream.available()
            val buffer = ByteArray(size)
            myInputStream.read(buffer)
            output = String(buffer)

            // Sets the TextView with the string to display text
            viewText.text = output

        } catch (e: IOException) {
            // Exception
            e.printStackTrace()
        }
    }
}