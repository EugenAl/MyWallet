package dpr.svich.mywallet.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dpr.svich.mywallet.R
import dpr.svich.mywallet.model.Transaction
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TransactionListAdapter(private val context: Context):
    RecyclerView.Adapter<TransactionListAdapter.CustomViewHolder>() {

    private var mDataset: ArrayList<Transaction> = arrayListOf()

    class CustomViewHolder(itemView: View,private val context: Context): RecyclerView.ViewHolder(itemView){
        fun bindItem(item: Transaction){
            Log.d("Adapter", item.toString())
            val dayTV = itemView.findViewById<TextView>(R.id.day_text_view)
            val dateTV = itemView.findViewById<TextView>(R.id.date_text_view)
            val categoryTV = itemView.findViewById<TextView>(R.id.category_text_view)
            val commentTV = itemView.findViewById<TextView>(R.id.comment_text_view)
            val priceTV = itemView.findViewById<TextView>(R.id.price_text_view)

            val dayFormat = SimpleDateFormat("dd")
            val dateFormat = SimpleDateFormat("MM.yyyy")
            val transactionDate = item.timestamp?.let { Date(it) }
            dayTV.text = dayFormat.format(transactionDate)
            dateTV.text = dateFormat.format(transactionDate)
            if(item.isSpend!!){
                categoryTV.text = context.resources
                    .getStringArray(R.array.spend_categories)[item.category!!]
            } else {
                categoryTV.text = context.resources
                    .getStringArray(R.array.earn_categories)[item.category!!]
            }
            commentTV.text = item.comment.orEmpty()
            priceTV.text = "${item.price}\u20BD"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.transaction_list_layout, parent, false)
        return CustomViewHolder(v, context)
    }

    override fun getItemCount(): Int {
         return mDataset.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bindItem(mDataset[position])
    }

    fun setData(mDataset: ArrayList<Transaction>){
        this.mDataset = mDataset
        notifyDataSetChanged()
        Log.d("Adapter", "DataSet has been changed")
    }
}