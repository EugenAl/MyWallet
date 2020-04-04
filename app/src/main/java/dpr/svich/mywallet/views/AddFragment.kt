package dpr.svich.mywallet.views

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import dpr.svich.mywallet.R
import dpr.svich.mywallet.viewmodels.AddViewModel
import kotlinx.android.synthetic.main.add_fragment.*

class AddFragment : Fragment() {

    companion object {
        fun newInstance() = AddFragment()
    }

    private lateinit var viewModel: AddViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.add_fragment, container, false)
        // init list
        val categoriesSpend = resources.getStringArray(R.array.spend_categories)
        val categoriesEarn = resources.getStringArray(R.array.earn_categories)

        // init views
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
                            if (categorySpinner != null) {
                                val arrayAdapter = ArrayAdapter(
                                    activity!!.applicationContext,
                                    R.layout.spinner_item,
                                    categoriesSpend
                                )
                                arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                                categorySpinner.adapter = arrayAdapter
                            }
                        }
                        false -> {
                            if (categorySpinner != null) {
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
            }
            view.findViewById<Button>(R.id.addButton).also {
                it.setOnClickListener {
                    if (priceEditText.text.isNotEmpty()) {
                        viewModel.addTransaction(
                            commentEditText.text.toString(),
                            priceEditText.text.toString(), spendRadioButton.isChecked
                        )
                        commentEditText.text.clear()
                        priceEditText.text.clear()
                    }
                }
            }
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
