package com.cookandroid.lowest_price_alert

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {
    // variables for login
    private lateinit var emailEditText : EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginBtn : Button
    private lateinit var google_sign_in_button : SignInButton

    // declare nullable object for Firebase auth
    private var auth: FirebaseAuth? = null

    // declare nullable object for google login
    private var googleSignInClient : GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginBtn = findViewById(R.id.loginBtn)
        google_sign_in_button = findViewById(R.id.google_sign_in_button)

        //auth 객체 초기화, 인스턴스 get
        auth = FirebaseAuth.getInstance()

        // login with email
        loginBtn.setOnClickListener{
            emailLogin()
        }

        google_sign_in_button.setOnClickListener {
            googleLogin()
        }

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

    } // onCreate
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth?.currentUser
        Toast.makeText(this, "사용자 로그인정보 있음", Toast.LENGTH_SHORT).show()
        updateUI(currentUser)
    }
    private fun emailLogin() {
        auth?.signInWithEmailAndPassword(emailEditText.text.toString(),passwordEditText.text.toString())
            ?.addOnCompleteListener{//통신 완료가 된 후 무슨일을 할지
                    task->
                if(task.isSuccessful){
                    //로그인 처리를 해주면 됨!
                    Toast.makeText(this, "login", Toast.LENGTH_LONG).show()
                }
                else{
                    // 오류가 난 경우!
                    Toast.makeText(this,task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
        //email, password null인 경우 예외 처리 해주기
    }
    private fun googleLogin() {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                Toast.makeText(this, "firebaseAuthWithGoogle:" + account.id, Toast.LENGTH_SHORT).show()
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.d(TAG, "Google sign in failed", e)
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth?.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }
    private fun updateUI(user: FirebaseUser?) {
        Toast.makeText(this, user.toString(), Toast.LENGTH_SHORT).show()
        //startActivity(...)
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}