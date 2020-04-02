package dpr.svich.mywallet.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputLayout
import dpr.svich.mywallet.R
import dpr.svich.mywallet.enums.Status
import dpr.svich.mywallet.viewmodels.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private var registerForm: Boolean = false
    // textInputLayout for email
    private lateinit var emailInput: TextInputLayout
    // textInputLayout for password
    private lateinit var passwdInput: TextInputLayout
    // textInputLayout for confirm password
    private lateinit var confirmInput: TextInputLayout
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // UI init
        emailInput = findViewById(R.id.textInputLayout)
        passwdInput = findViewById(R.id.textInputLayout2)
        confirmInput = findViewById(R.id.textInputLayout3)
        loginButton = findViewById(R.id.loginButton)

        val model: LoginViewModel = ViewModelProviders.of(this)
            .get(LoginViewModel::class.java)

        model.getStatus().observe(this, Observer<Status> { status ->
            when (status) {
                Status.SUCCESS -> {hideError(); updateUI()}
                Status.LOGIN_INVALID -> {
                    hideError(); emailInput.error = "Login invalid!"
                }
                Status.PASWD_INVALID -> {
                    hideError(); passwdInput.error = "Password invalid!"
                }
                Status.PASWD_MISMATCH -> {
                    hideError(); confirmInput.error = "Password mismatch!"
                }
                else -> {
                    hideError()
                }
            }
        })

        val registerTextView = findViewById<TextView>(R.id.textViewRegister)
        registerTextView.setOnClickListener {
            registerForm = !registerForm
            if (registerForm) {
                registerTextView.text = getString(R.string.already_registered_login_me)
                loginButton.text = getString(R.string.register)
                confirmInput.visibility = View.VISIBLE
            } else {
                registerTextView.text = getString(R.string.no_account_yet_create_one)
                loginButton.text = getString(R.string.sign_in)
                confirmInput.visibility = View.GONE
            }
        }

        loginButton.setOnClickListener {
            if (registerForm) {
                model.onRegisterClick(
                    emailInput.editText?.text.toString(),
                    passwdInput.editText?.text.toString(),
                    confirmInput.editText?.text.toString()
                )
            } else {
                model.onLoginClick(
                    emailInput.editText?.text.toString(),
                    passwdInput.editText?.text.toString()
                )
            }
        }
    }

    private fun updateUI() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun hideError() {
        emailInput.error = ""
        passwdInput.error = ""
        confirmInput.error = ""
    }
}
