package dpr.svich.mywallet.views

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

import dpr.svich.mywallet.R
import dpr.svich.mywallet.model.Transaction
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class ChartStatFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null

    private lateinit var toolbar: Toolbar
    private lateinit var toolbarSpinner: Spinner
    private lateinit var currentDate: Date

    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart

    private val TRANSACTIONS = "Transactions"

    val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chart_stat, container, false)

        currentDate = Date()
        // toolbar init
        toolbar = view.findViewById(R.id.toolbar_stat_chart)
        activity!!.setActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.arrow_back)
        activity!!.actionBar.setDisplayShowTitleEnabled(false)

        // BarChart init
        barChart = view.findViewById(R.id.barChart)
        barChart.setScaleEnabled(false)
        barChart.setTouchEnabled(false)
        barChart.description = null
        barChart.legend.isEnabled = false
        // BarChart axis
        barChart.axisRight.isEnabled = false
        val chartYAxis = barChart.getAxis(YAxis.AxisDependency.LEFT)
        chartYAxis.setDrawGridLines(true)
        chartYAxis.setDrawAxisLine(false)
        chartYAxis.setDrawLabels(false)
        val chartXAxis = barChart.xAxis
        chartXAxis.position = XAxis.XAxisPosition.BOTTOM
        chartXAxis.textColor = Color.WHITE
        chartXAxis.setDrawGridLines(false)
        chartXAxis.setDrawAxisLine(true)

        // PieChart init
        pieChart = view.findViewById(R.id.pieChart)
        pieChart.setCenterTextSize(25f)
        pieChart.holeRadius = 80f
        pieChart.transparentCircleRadius = 84f
        pieChart.setCenterTextColor(resources.getColor(R.color.colorWhite))
        pieChart.setTransparentCircleColor(resources.getColor(R.color.colorPrimary))
        pieChart.setHoleColor(resources.getColor(R.color.colorPrimary))
        pieChart.setEntryLabelTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL))
        pieChart.description = null
        pieChart.legend.isEnabled = false

        // spinner init
        toolbarSpinner = view.findViewById(R.id.month_toolbar_spinner)
        val spinnerAdapter = ArrayAdapter<String>(
            context, android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.moths)
        )
        toolbarSpinner.adapter = spinnerAdapter
        toolbarSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d("DataSnapshot stat", String.format("%02d", position + 1))
                //month transaction listener
                Firebase.database.reference.child(userId.orEmpty())
                    .child(TRANSACTIONS).child(
                        SimpleDateFormat(
                            "yyyy",
                            Locale.getDefault()
                        ).format(currentDate)
                    ).child(String.format("%02d", position + 1))
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            updateCharts(p0)
                        }
                    })
            }
        }

        toolbarSpinner.setSelection(currentDate.month)
        return view
    }

    // update PieChart and BarChart views
    private fun updateCharts(p0: DataSnapshot) {
        val pieData = ArrayList<PieEntry>()
        val barData = ArrayList<BarEntry>()
        val pieCategories = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        val labels = resources.getStringArray(R.array.spend_categories)
        var sum = 0
        for (snapshot in p0.children) {
            Log.d("DataSnapshot stat", "Day: ${snapshot.key}")
            var spend = 0f
            for (post in snapshot.children) {
                val transaction = post.getValue(Transaction::class.java)
                if (transaction?.isSpend!!) {
                    pieCategories[transaction.category!!.toInt()] +=
                        transaction.price!!.toFloat()
                    spend += transaction.price!!.toFloat()
                    sum += transaction.price!!.toInt()
                }
            }
            barData.add(BarEntry(snapshot.key!!.toFloat(), spend))
        }
        for (i in pieCategories.indices) {
            if (!pieCategories[i].equals(0f)) {
                pieData.add(PieEntry(pieCategories[i], labels[i]))
            }
        }

        val pieDataSet = PieDataSet(pieData, "Categories")
        pieDataSet.colors = (ColorTemplate.PASTEL_COLORS.toMutableList())
        pieDataSet.valueTextColor = resources.getColor(R.color.colorWhite)
        pieDataSet.valueTextSize = 14f
        pieDataSet.valueTypeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
        pieChart.data = PieData(pieDataSet)
        pieChart.centerText = sum.toString() + "\u20BD"
        pieChart.invalidate()
        pieChart.animateY(1000, Easing.EaseInCubic)
        pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {
            }

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                // get index of category
                val index = labels.indexOf((e as PieEntry).label)
                if (index >= 0) {
                    // view statistic about category in selected month
                    openCategory(index)
                }
            }
        })
        val barDataSet = BarDataSet(
            barData,
            resources.getStringArray(R.array.moths)[currentDate.month]
        )
        barDataSet.color = resources.getColor(R.color.colorDeepOrange)
        barDataSet.axisDependency = YAxis.AxisDependency.LEFT
        barDataSet.valueTextColor = Color.WHITE
        barChart.data = BarData(barDataSet)
        barChart.invalidate()
        barChart.animateY(1000, Easing.EaseInOutExpo)
    }

    // method for open ListStatFragment
    // use category index and selected month
    private fun openCategory(index: Int) {
        val bundle = Bundle()
        bundle.putInt("index", index)
        bundle.putInt("month", toolbarSpinner.selectedItemPosition)
        try {
            this.findNavController()
                .navigate(R.id.action_chartStatFragment_to_listStatFragment, bundle)
        } catch (e:Exception){
            val alertDialogBuilder = AlertDialog.Builder(context!!)
            alertDialogBuilder.setMessage(e.message)
            alertDialogBuilder.setPositiveButton("OK", DialogInterface.OnClickListener{
                dialog, _ -> dialog.dismiss()
            })
            val dialog = alertDialogBuilder.create()
            dialog.setTitle("Something went wrong")
            dialog.show()
        }
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
