package dpr.svich.mywallet.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dpr.svich.mywallet.R

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
    }

    private fun updateUI(user : FirebaseUser?){
        if(user == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
