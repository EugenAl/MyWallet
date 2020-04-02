package dpr.svich.mywallet.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dpr.svich.mywallet.enums.Status
import dpr.svich.mywallet.model.User

class LoginViewModel : ViewModel() {
    private lateinit var user: User

    private val status: MutableLiveData<Status> by lazy { MutableLiveData<Status>() }

    private val auth: MutableLiveData<FirebaseAuth> by lazy { MutableLiveData<FirebaseAuth>() }

    fun getStatus(): LiveData<Status>{
        return status
    }

    fun getAuth(): LiveData<FirebaseAuth>{
        return auth
    }

    /**
     * Авторизация существующего пользователя
     */
    fun onLoginClick(email: String, password: String) {
        when {
            email.isEmpty() -> status.value = Status.LOGIN_INVALID
            password.length < 6 -> status.value = Status.PASWD_INVALID
            else -> {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)?.addOnCompleteListener{task ->
                    if(task.isSuccessful){
                        status.value = Status.SUCCESS
                    } else {
                        //TODO Firebase exceptions
                    }
                }
            }
        }
        auth.value = FirebaseAuth.getInstance()
    }

    /**
     * Регистрация нового пользователя
     */
    fun onRegisterClick(email: String, password: String, confirmPassword: String) {
        when {
            email.isEmpty() -> status.value = Status.LOGIN_INVALID
            password.length < 6 -> status.value = Status.PASWD_INVALID
            password != confirmPassword -> status.value = Status.PASWD_MISMATCH
            else -> {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)?.addOnCompleteListener{ task ->
                    if(task.isSuccessful){
                        status.value = Status.SUCCESS
                    } else {

                        //TODO Firebase exceptions
                    }
                }
            }
        }
    }
}