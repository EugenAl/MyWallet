package dpr.svich.mywallet.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Entity

abstract class FirebaseRepository<Model> {

    protected lateinit var databaseReference : DatabaseReference
    protected lateinit var firebaseCallback: FirebaseDatabaseRepositoryCallback<Model>

    protected abstract fun getRootNode() : String

    private lateinit var mapper: FirebaseMapper<*, *>
    private lateinit var listener: BaseValueEventListener<*, *>

    fun FirebaseRepository(mapper: FirebaseMapper<*, *>){
        databaseReference = FirebaseDatabase.getInstance().getReference(getRootNode())
        this.mapper = mapper
    }

    public fun addListener(firebaseCallback: FirebaseDatabaseRepositoryCallback<Model>){
        this.firebaseCallback = firebaseCallback
        //listener = BaseValueEventListener<Model>(mapper, firebaseCallback)
        databaseReference.addValueEventListener(listener)
    }

    public interface FirebaseDatabaseRepositoryCallback<T>{
        fun onSuccess(result: List<T>)
        fun onError(e: Exception)
    }
}