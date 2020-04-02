package dpr.svich.mywallet.views

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

import dpr.svich.mywallet.R
import dpr.svich.mywallet.viewmodels.AddViewModel

class AddFragment : Fragment() {

    companion object {
        fun newInstance() = AddFragment()
    }

    private lateinit var viewModel: AddViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view =  inflater.inflate(R.layout.add_fragment, container, false)

        // init views
        var commentEditText = view.findViewById<EditText>(R.id.productEditText)
        var priceEditText = view.findViewById<EditText>(R.id.priceEditText)
        view.findViewById<Button>(R.id.addButton).also {
            it.setOnClickListener{
                if(priceEditText.text.isNotEmpty()){
                    viewModel.addTransaction(commentEditText.text.toString(),
                        priceEditText.text.toString())
                    commentEditText.text.clear()
                    priceEditText.text.clear()
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
