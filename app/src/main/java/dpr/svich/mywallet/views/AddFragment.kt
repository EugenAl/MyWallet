package dpr.svich.mywallet.views

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

import dpr.svich.mywallet.R
import dpr.svich.mywallet.viewmodels.AddViewModel
import kotlinx.android.synthetic.main.add_fragment.*

class AddFragment : Fragment() {

    companion object {
        fun newInstance() = AddFragment()
    }

    private lateinit var viewModel: AddViewModel

    private var selectedPosition=-1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.add_fragment, container, false)
        // init list
        val categoriesSpend = resources.getStringArray(R.array.spend_categories)
        val categoriesEarn = resources.getStringArray(R.array.earn_categories)

        // init views
        var chipGroup = view.findViewById<ChipGroup>(R.id.chipGroup)
        // Checked change listener for chip group
        val onCheckedListener = CompoundButton.OnCheckedChangeListener{buttonView, isChecked ->
            selectedPosition = if(isChecked) categoriesSpend.indexOf((buttonView as Chip).text)
            else -1
        }
        for(category in categoriesSpend){
            val chip = Chip(context)
            chip.text = category
            chip.isCheckable = true
            chip.setOnCheckedChangeListener(onCheckedListener)
            chipGroup.addView(chip)
        }

        val categorySpinner = view.findViewById<Spinner>(R.id.spinner)
        if(categorySpinner != null) {
            val arrayAdapter =
                ArrayAdapter(activity!!.applicationContext, R.layout.spinner_item, categoriesSpend)
            arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            categorySpinner.adapter = arrayAdapter
            val commentEditText = view.findViewById<EditText>(R.id.productEditText)
            val priceEditText = view.findViewById<EditText>(R.id.priceEditText)
            val spendRadioButton = view.findViewById<RadioButton>(R.id.radioButtonSpend).also {
                it.setOnCheckedChangeListener { v, state ->
                    when (state) {
                        true -> {
                            val arrayAdapter = ArrayAdapter(
                                activity!!.applicationContext,
                                R.layout.spinner_item,
                                categoriesSpend
                            )
                            arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                            categorySpinner.adapter = arrayAdapter
                        }
                        false -> {
                            val arrayAdapter = ArrayAdapter(
                                activity!!.applicationContext,
                                R.layout.spinner_item,
                                categoriesEarn
                            )
                            arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                            categorySpinner.adapter = arrayAdapter
                        }
                    }
                }
            }
            /* Add button handler */
            view.findViewById<Button>(R.id.addButton).also {
                it.setOnClickListener {
                    if (priceEditText.text.isNotEmpty() and (selectedPosition >= 0)) {
                        viewModel.addTransaction(
                            commentEditText.text.toString(),
                            priceEditText.text.toString(), spendRadioButton.isChecked,
                            selectedPosition
                        )
                        commentEditText.text.clear()
                        priceEditText.text.clear()
                        chipGroup.clearCheck()
                    } else if(priceEditText.text.isEmpty()){
                        Toast.makeText(context, getString(R.string.empty_price),
                            Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, getString(R.string.empty_category),
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = of(requireActivity()).get(AddViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
