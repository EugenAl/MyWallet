package dpr.svich.mywallet.views

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

import dpr.svich.mywallet.R
import dpr.svich.mywallet.adapter.TransactionListAdapter
import dpr.svich.mywallet.model.Transaction
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ListStatFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 */
class ListStatFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null

    private lateinit var toolbar: Toolbar

    private lateinit var toolbarText: TextView
    private val TRANSACTIONS = "Transactions"

    private lateinit var datasetList: ArrayList<Transaction>
    private var adapter: TransactionListAdapter? = null

    val userId = FirebaseAuth.getInstance().currentUser?.uid

    private var categoryIndex: Int = 0
    private var monthIndex: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list_stat, container, false)
        // toolbar init
        toolbar = view.findViewById(R.id.toolbar_stat_list)
        toolbarText = view.findViewById(R.id.toolbar_stat_list_textView)
        activity!!.setActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.arrow_back)
        toolbar.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        activity!!.actionBar.setDisplayShowTitleEnabled(false)
        // get arguments
        val bundle = this.arguments
        bundle?.let {
            categoryIndex = it.getInt("index")
            monthIndex = it.getInt("month")
        }

        // Recycler view list of transition
        val recycleTransactionView = view.findViewById<RecyclerView>(R.id.transaction_list_stat)
        val recycleLayoutManager = LinearLayoutManager(context)
        recycleTransactionView.layoutManager = recycleLayoutManager
        recycleTransactionView.itemAnimator = DefaultItemAnimator()
        adapter = context?.let { TransactionListAdapter(it) }
        recycleTransactionView.adapter = adapter
        datasetList = ArrayList()

        toolbarText.text =
            resources.getStringArray(R.array.moths)[monthIndex] + " · " + resources.getStringArray(R.array.spend_categories)[categoryIndex]

        //month transaction listener
        Firebase.database.reference.child(userId.orEmpty())
            .child(TRANSACTIONS).child(
                SimpleDateFormat(
                    "yyyy",
                    Locale.getDefault()
                ).format(Date())
            ).child(String.format("%02d", monthIndex + 1))
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    updateList(p0)
                }
            })
        return view
    }

    private fun updateList(p0: DataSnapshot){
        for (snapshot in p0.children) {
            Log.d("DataSnapshot stat", "Day: ${snapshot.key}")
            for (post in snapshot.children) {
                val transaction = post.getValue(Transaction::class.java)
                transaction?.let{
                    if(it.category!!.toInt() == categoryIndex)
                        datasetList.add(transaction)
                }
            }
        }
        adapter?.setData(datasetList)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

}
