package com.example.moneymanagement

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.termproject_a.dynamicTable

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "income"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BudgetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BudgetFragment : Fragment() {
    //income passed from incomeFragment.kt
    private var income: Int? = null
    //private var param2: String? = "param2 default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            income = it.getInt(ARG_PARAM1)
            //param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_budget, container, false)

        //event listener for the visualizer button - starts the dynamicTable.kt activity
        val displayBudgetBtn = view.findViewById<Button>(R.id.display_budget)
        displayBudgetBtn.setOnClickListener {
            //pass income to the dynamicTable (which will use it to create the default Budget object)
            val intent = Intent(activity, dynamicTable()::class.java)
            intent.putExtra("income", income)
            startActivity(intent)
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BudgetFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(income: Int) =
            BudgetFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, income)
                    //putString(ARG_PARAM2, param2)
                }
            }
    }
}