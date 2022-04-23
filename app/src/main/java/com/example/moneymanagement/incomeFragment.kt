package com.example.moneymanagement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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
        val v = inflater.inflate(R.layout.fragment_income, container, false)

        var incomeEditText : EditText = v.findViewById(R.id.editTextTextIncome) //Makes Edit TextBox Object
        var gasEditText : EditText = v.findViewById(R.id.editTextTextGas)
        var funEditText : EditText = v.findViewById(R.id.editTextTextPersonName4)
        var misEditText : EditText = v.findViewById(R.id.editTextTextPersonName5)
        var mortEditText : EditText = v.findViewById(R.id.editTextTextPersonName6)
        var rentEditText : EditText = v.findViewById(R.id.editTextTextPersonName7)
        var phoneEditText : EditText = v.findViewById(R.id.editTextTextPersonName8)
        var utilEditText : EditText = v.findViewById(R.id.editTextTextPersonName9)
        var insuranceEditText : EditText = v.findViewById(R.id.editTextTextPersonName10)


        var buttonSaveAndNext : Button = v.findViewById(R.id.button3)

        var firstTextBox : TextView = v.findViewById(R.id.textView2)





        buttonSaveAndNext.setOnClickListener{

            if(incomeEditText.text.toString() == "" || gasEditText.text.toString() == "" || funEditText.text.toString() == "" ||
                misEditText.text.toString() == "" || mortEditText.text.toString() == "" || rentEditText.text.toString() == "" ||
                phoneEditText.text.toString() == "" || utilEditText.text.toString() == "" || insuranceEditText.text.toString() == "") {

                val text = "Must Input data for all Instances"
                val duration = Toast.LENGTH_SHORT

                val toast = Toast.makeText(v.context, text, duration)
                toast.show()

            }else{

                val fragment = BudgetFragment()
                val transaction = fragmentManager?.beginTransaction()
                transaction?.replace(R.id.fragmentContainerView, fragment)?.commit()
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