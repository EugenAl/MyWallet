package dpr.svich.mywallet.views

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.widget.Adapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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

class MainActivity : AppCompatActivity() {

    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>

    private lateinit var listDataset: MutableLiveData<ArrayList<Transaction>>

    private val TRANSACTIONS = "Transactions"

    val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = FirebaseAuth.getInstance().currentUser
        updateUI(user)

        Firebase.database.setPersistenceEnabled(true)

        // BarChart init
        val chartView = findViewById<BarChart>(R.id.mainChartView)
        chartView.setScaleEnabled(false)
        chartView.setTouchEnabled(false)
        chartView.description = null
        chartView.legend.isEnabled = false
        // BarChart axis
        chartView.axisRight.isEnabled = false
        val chartYAxis = chartView.getAxis(YAxis.AxisDependency.LEFT)
        chartYAxis.setDrawGridLines(false)
        chartYAxis.setDrawAxisLine(false)
        chartYAxis.setDrawLabels(false)
        val chartXAxis = chartView.xAxis
        chartXAxis.position = XAxis.XAxisPosition.BOTTOM
        chartXAxis.textColor = Color.WHITE
        chartXAxis.setDrawGridLines(false)
        chartXAxis.setDrawAxisLine(false)

        // TextView init
        val spendMonthTV = findViewById<TextView>(R.id.month_spend_tv)
        val spendAverageTV = findViewById<TextView>(R.id.average_spend_tv)


        // Backdrop
        val holder = findViewById<LinearLayout>(R.id.holderLayout)
        sheetBehavior = BottomSheetBehavior.from(holder)
        sheetBehavior.isHideable = false
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        findViewById<LinearLayout>(R.id.linearLayout).also {
            it.setOnClickListener {
                if (sheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    sheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                } else {
                    sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }
        listDataset = MutableLiveData()

        // Recycler view list of transition
        val recycleTransactionView = findViewById<RecyclerView>(R.id.transaction_list)
        val recycleLayoutManager = LinearLayoutManager(applicationContext)
        recycleTransactionView.layoutManager = recycleLayoutManager
        recycleTransactionView.itemAnimator = DefaultItemAnimator()
        val adapter = TransactionListAdapter(applicationContext)
        recycleTransactionView.adapter = adapter

        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                listDataset.value?.get(position)?.id?.let {
                    Firebase.database.reference.child(userId.orEmpty()).child(TRANSACTIONS)
                        .child(SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date()))
                        .child(it).removeValue()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recycleTransactionView)

        // today transaction listener
        val todayDate = Date()
        var sym: Int
        val symTextView = findViewById<TextView>(R.id.priceTV)
        val database = Firebase.database.reference.child(userId.orEmpty()).child(TRANSACTIONS)
            .child(SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(todayDate))
        database.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val dataArray = ArrayList<Transaction>()
                sym = 0
                Log.d("DataSnapshot", "Root: ${p0.key}")
                for (post in p0.children) {
                    Log.d("DataSnapshot", "${post.key}")
                    val trans = post.getValue(Transaction::class.java)
                    trans.let {
                        if (trans!!.isSpend!!) {
                            sym += trans.price!!.toInt()
                        } else {
                            sym -= trans.price!!.toInt()
                        }
                        trans.id = post.key
                        dataArray.add(trans)
                    }
                }
                symTextView.text = "${sym}\u20BD"

                listDataset.value = dataArray
            }
        })
        // month transaction listener
        val monthDatabase = Firebase.database.reference.child(userId.orEmpty()).child(TRANSACTIONS)
            .child(SimpleDateFormat("yyyy/MM", Locale.getDefault()).format(todayDate))
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    val chartEntries = ArrayList<BarEntry>()
                    var monthSpend = 0
                    for (snapshot in p0.children) {
                        Log.d("DataSnapshot", "Day: ${snapshot.key}")
                        chartEntries.add(BarEntry(snapshot.key!!.toFloat(), 0f))
                        for (post in snapshot.children) {
                            val price = (post.getValue(Transaction::class.java))?.price?.toInt()!!
                            chartEntries[chartEntries.lastIndex].y += price
                            monthSpend += price
                        }
                    }
                    val avg = monthSpend / (SimpleDateFormat(
                        "dd",
                        Locale.getDefault()
                    ).format(todayDate)).toInt()
                    val chartSet = BarDataSet(
                        chartEntries, SimpleDateFormat(
                            "MMM",
                            Locale.getDefault()
                        ).format(todayDate)
                    )
                    chartSet.color = resources.getColor(R.color.colorDeepOrange)
                    chartSet.axisDependency = YAxis.AxisDependency.LEFT
                    chartSet.valueTextColor = Color.WHITE
                    val chartData = BarData(chartSet)
                    chartView.data = chartData
                    chartView.invalidate()
                    chartView.animateY(500, Easing.EaseInBack)

                    spendMonthTV.text = getString(R.string.month_spend, monthSpend)
                    spendAverageTV.text = "Average spend: ${avg}\u20BD"
                }
            })

        listDataset.observe(this, androidx.lifecycle.Observer {
            adapter.setData(it)
        })

        val linearLayout = findViewById<LinearLayout>(R.id.revenueCardView)
        linearLayout.setOnClickListener {
            val intent = Intent(this, StatisticActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

}
