package com.example.moneymanagement

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BudgetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BudgetFragment : Fragment() {


    private val ARG_PARAM1 = "income"
    private var income: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            income = it.getInt(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_budget, container, false)

        var toVisualizerButton : Button = v.findViewById(R.id.display_budget)
        var toBudgetGuideButton : Button = v.findViewById(R.id.button5)
        var toReasonsToSaveButton : Button = v.findViewById(R.id.button6)
        var toTipsForSavingButton : Button = v.findViewById(R.id.button7)
        var backButton :Button = v.findViewById(R.id.button8)

        toVisualizerButton.setOnClickListener{
            val intent = Intent(activity, DynamicTable()::class.java)
            intent.putExtra("income", income)
            startActivity(intent)
        }

        toBudgetGuideButton.setOnClickListener{

            // TEMP //
            val inten = Intent (this@BudgetFragment.requireContext(), BudgetGuide::class.java)
            startActivity(inten)

            // TEMP //

        }

        toReasonsToSaveButton.setOnClickListener{

            // TEMP //
            val inten = Intent (this@BudgetFragment.requireContext(), SavingReasons::class.java)
            startActivity(inten)

            // TEMP //

        }

        toTipsForSavingButton.setOnClickListener{

            // TEMP //
            val inten = Intent (this@BudgetFragment.requireContext(), TopTips::class.java)
            startActivity(inten)

            // TEMP //
        }

        backButton.setOnClickListener{

            val fragment = incomeFragment()

            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentContainerView, fragment)?.commit()

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
         * @return A new instance of fragment BudgetFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BudgetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }





}