package dpr.svich.mywallet.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel: ViewModel() {

    val currentState: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
}