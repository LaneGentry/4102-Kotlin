package com.example.moneymanagement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.termproject_a.Budget

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [incomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class incomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_income, container, false)
        val context = this
        var incomeEditText : EditText = v.findViewById(R.id.editTextTextIncome) //Makes Edit TextBox Object
        var buttonSaveAndNext : Button = v.findViewById(R.id.to_table_display)
        var income : Int

        // When the save/next button is clicked the income is taken from the edittextIncome
        buttonSaveAndNext.setOnClickListener{
            println("save and next event...")
            // validate income - income cannot be null or blank. if it is display toast message and do not allow
            //  the rest of the function to execute until it is valid
            if(!(incomeEditText.getText().toString()).equals("")) {
                income =
                    Integer.parseInt(incomeEditText.text.toString())   //Converts entered income to text, then to string


                // D bait line //
                //var budgetObject = Budget(income)  // parseInt Function turns string value into int value

                val fragment = BudgetFragment()
                val bundle = Bundle()
                bundle.putInt("income", income)
                fragment.arguments = bundle

                val transaction = fragmentManager?.beginTransaction()
                transaction?.replace(R.id.fragmentContainerView, fragment)?.commit()
            }
            else{
                incomeEditText.setError("Please enter your income!")
            }
        }


        return v
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment incomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            incomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

