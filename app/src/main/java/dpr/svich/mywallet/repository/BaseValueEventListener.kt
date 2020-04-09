package dpr.svich.mywallet.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BaseValueEventListener<Model, Entity>(
    private var mapper: FirebaseMapper<Entity, Model>,
    private var callback: FirebaseRepository.FirebaseDatabaseRepositoryCallback<Model>
) : ValueEventListener {

    override fun onCancelled(p0: DatabaseError) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDataChange(p0: DataSnapshot) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}