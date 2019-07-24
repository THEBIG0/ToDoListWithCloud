package gunt0023.example.grouptasklist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider


import kotlinx.android.synthetic.main.activity_google_login.*




class GoogleLoginActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var firebaseAuth: FirebaseAuth
    private val RC_SIGN_IN = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_login)
        setSupportActionBar(toolbar)

        configureGoogleSignIn()

        firebaseAuth = FirebaseAuth.getInstance()

        val currentUser = firebaseAuth.currentUser
        updateUI(currentUser)


       /* sign_in_button.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/
        setupUI()
        setupUI2()
    }

    override fun onStart(){
        super.onStart()
        FirebaseAuth.getInstance().signOut()
        val user = FirebaseAuth.getInstance().currentUser
        mGoogleSignInClient.signOut().addOnCompleteListener{

        }
        if (user != null) {
            startActivity(MainActivity.getLaunchIntent(this))
            finish()
        }
        Log.i("tag01",getString(R.string.default_web_client_id))
    }

    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, GoogleLoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    private fun setupUI() {
        google_button.setOnClickListener {
            signIn()
        }

    }

     fun setupUI2(){
        sign_out_button.setOnClickListener {
            signOut()
        }
    }

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            //.requestIdToken("138851531386-b27iglnl1h9mrndhrfpod9aoahu48gpl.apps.googleusercontent.com")
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

     /* fun onClick(view: View) {
          when (view.id) {
              R.id.sign_in_button -> signIn()
          }
      }*/
    private fun updateUI(currentUser: FirebaseUser?) {

        /*hideProgressDialog();
        if (user != null) {
            mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }*/
    }

   private fun signIn() {
       val signInIntent: Intent = mGoogleSignInClient.signInIntent
       startActivityForResult(signInIntent, RC_SIGN_IN)

       //startActivity(MainActivity.getLaunchIntent(this))
        /*val signInIntent = googleSignInClient.signInIntent

        startActivityForResult(signInIntent, RC_SIGN_IN)*/

    }

    fun signOut() {
        startActivity(GoogleLoginActivity.getLaunchIntent(this))
        FirebaseAuth.getInstance().signOut()
        mGoogleSignInClient.signOut().addOnCompleteListener{

        }
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            //has nothing to do with Task class
            //val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
                Log.w("tag", "Google sign in success")
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("tag", "Google sign in failed", e)
                // ...
            }
        }
    }

   private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        Log.d("tag", "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("tag", "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    //updateUI(user)
                   // val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        startActivity(MainActivity.getLaunchIntent(this))
                        finish()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("tag", "signInWithCredential:failure", task.exception)
                    Snackbar.make(google_button, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                    updateUI(null)
                }

                // ...
            }
    }


}
