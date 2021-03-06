package dpr.svich.mywallet.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dpr.svich.mywallet.model.Transaction
import java.lang.Exception
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class AddViewModel : ViewModel() {

    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    private val error: MutableLiveData<String> by lazy { MutableLiveData<String>()}

    private var database : DatabaseReference = Firebase.database.reference.child(userId.orEmpty()).child("Transactions")
        .child(SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date()))

    fun addTransaction(comment: String, price: String, isSpend: Boolean, category:Int){
        val transaction = Transaction(comment, price, System.currentTimeMillis(), isSpend, category)
        // write to db
        try {
            database.push().setValue(transaction)
                .addOnSuccessListener{
                    Log.d("transaction", "transaction successful")
                }
                .addOnFailureListener{
                    Log.d("transition", "transition failed: ${it.message}")
                }
        } catch (e: Exception){
            error.value = e.message
        }
    }

    fun getError(): LiveData<String>{
        return error
    }
}
