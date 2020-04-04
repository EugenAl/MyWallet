package dpr.svich.mywallet.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dpr.svich.mywallet.R
import dpr.svich.mywallet.model.Transaction
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = FirebaseAuth.getInstance().currentUser
        updateUI(user)

        // Backdrop
        val holder = findViewById<LinearLayout>(R.id.holderLayout)
        sheetBehavior = BottomSheetBehavior.from(holder)
        sheetBehavior.isHideable = false
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        val lanyard = findViewById<LinearLayout>(R.id.linearLayout).also {
            it.setOnClickListener{
                if (sheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    sheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                } else {
                    sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }
        var sym = 0
        val symTextView = findViewById<TextView>(R.id.priceTV)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val database = Firebase.database.reference.child(userId.orEmpty()).child("Transactions")
            .child(SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date()))
        database.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                sym = 0
                Log.d("DataSnapshot", "Root: ${p0.key}")
                for(post in p0.children){
                    Log.d("DataSnapshot", "${post.key}")
                    var trans = post.getValue(Transaction::class.java)
                    trans.let {
                        if(trans!!.isSpend!!) {
                            sym += trans.price!!.toInt()
                        } else {
                            sym -= trans.price!!.toInt()
                        }
                    }
                }
                symTextView.text = "${sym}\u20BD"
            }
        })
    }

    private fun updateUI(user : FirebaseUser?){
        if(user == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
