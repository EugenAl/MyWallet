package dpr.svich.mywallet.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dpr.svich.mywallet.model.Transaction
import java.sql.Timestamp

class AddViewModel : ViewModel() {

    private var database : DatabaseReference = Firebase.database.reference

    val userId = FirebaseAuth.getInstance().currentUser?.uid

    fun addTransaction(comment: String, price: String){
        var transaction = Transaction(comment, price, Timestamp(System.currentTimeMillis()))
        // write to db
        database.child(userId.orEmpty()).child("Transactions").push().setValue(transaction)
            .addOnSuccessListener{
                Log.d("transaction", "transaction successful")
            }
            .addOnFailureListener{
                Log.d("transition", "transition failed: ${it.message}")
            }
    }
}
