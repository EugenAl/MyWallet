package dpr.svich.mywallet.repository

import com.google.firebase.database.DataSnapshot
import android.graphics.ColorSpace.Model
import com.google.firebase.database.DatabaseReference
import java.lang.reflect.ParameterizedType


abstract class FirebaseMapper<Entity, Model> : MapperInterface<Entity, Model> {

    private fun map(dataSnapshot: DataSnapshot) : Model? {
        val entity = dataSnapshot.getValue(getEntityClass())
        return entity?.let { map(it) }
    }

    fun mapList(dataSnapshot: DataSnapshot) : List<Model?>{
        val list = mutableListOf<Model?>()
        for (item in dataSnapshot.children){
            list += map(item)
        }
        return list
    }

    private fun getEntityClass(): Class<Entity> {
        val superclass = javaClass.genericSuperclass as ParameterizedType
        return (superclass.actualTypeArguments[0]) as Class<Entity>
    }
}