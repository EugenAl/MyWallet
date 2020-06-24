package dpr.svich.mywallet.views

import android.icu.text.DateFormatSymbols
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.Toolbar
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dpr.svich.mywallet.R
import dpr.svich.mywallet.model.Transaction
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_fragment.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StatisticActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var toolbarSpinner: Spinner
    private lateinit var currentDate: Date

    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart

    private val TRANSACTIONS = "Transactions"

    val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistic)

        currentDate = Date()
        // toolbar init
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.arrow_back)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { finish() }

        // BarChart init
        barChart = findViewById(R.id.barChart)

        // PieChart init
        pieChart = findViewById(R.id.pieChart)
        pieChart.setCenterTextSize(25f)

        // spinner init
        toolbarSpinner = findViewById(R.id.month_toolbar_spinner)
        val spinnerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.moths))
        toolbarSpinner.adapter = spinnerAdapter
        toolbarSpinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d("DataSnapshot stat", String.format("%02d", position+1))
                //month transaction listener
                Firebase.database.reference.child(userId.orEmpty())
                    .child(TRANSACTIONS).child(SimpleDateFormat("yyyy",
                        Locale.getDefault()).format(currentDate)).child(String.format("%02d", position+1))
                    .addValueEventListener(object :ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            updateCharts(p0)
                        }
                    })
            }
        }

        toolbarSpinner.setSelection(currentDate.month)
    }

    // update PieChart and BarChart views
    private fun updateCharts(p0: DataSnapshot){
        val pieData = ArrayList<PieEntry>()
        val barData = ArrayList<BarEntry>()
        val pieCategories = floatArrayOf(0f,0f,0f,0f,0f,0f,0f, 0f)
        val labels = resources.getStringArray(R.array.spend_categories)
        var sum = 0
        for (snapshot in p0.children) {
            Log.d("DataSnapshot stat", "Day: ${snapshot.key}")
            for (post in snapshot.children) {
                val transaction = post.getValue(Transaction::class.java)
                if(transaction?.isSpend!!){
                    pieCategories[transaction.category!!.toInt()] +=
                        transaction.price!!.toFloat()
                    barData.add(BarEntry(snapshot.key!!.toFloat(),
                        transaction.price!!.toFloat()))
                    sum += transaction.price!!.toInt()
                }
            }
        }
        for(i in pieCategories.indices){
            if(!pieCategories[i].equals(0f)){
                pieData.add(PieEntry(pieCategories[i], labels[i]))
            }
        }

        val pieDataSet = PieDataSet(pieData, "Categories")
        pieDataSet.colors = (ColorTemplate.MATERIAL_COLORS.toMutableList())
        pieChart.data = PieData(pieDataSet)
        pieChart.centerText = sum.toString() + "\u20BD"
        pieChart.invalidate()
        val barDataSet = BarDataSet(barData,
            resources.getStringArray(R.array.moths)[currentDate.month])
        barDataSet.color = resources.getColor(R.color.colorDeepOrange)
        barChart.data = BarData(barDataSet)
        barChart.invalidate()
    }
}
