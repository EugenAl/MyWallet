package dpr.svich.mywallet.views

import android.graphics.Color
import android.graphics.Typeface
import android.icu.text.DateFormatSymbols
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_fragment.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StatisticActivity : AppCompatActivity(), ChartStatFragment.OnFragmentInteractionListener,
    ListStatFragment.OnFragmentInteractionListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistic)

        val ft = supportFragmentManager.beginTransaction()
        val chartFragment = ChartStatFragment()
        ft.replace(R.id.statistic_container, chartFragment)
        ft.commit()
    }

    override fun onFragmentInteraction(uri: Uri) {

    }
}
