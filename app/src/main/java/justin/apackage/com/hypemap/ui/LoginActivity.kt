package justin.apackage.com.hypemap.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import justin.apackage.com.hypemap.HypeMapConstants.Companion.INSTAGRAM_COOKIE_KEY
import justin.apackage.com.hypemap.HypeMapConstants.Companion.HYPEMAP_SHARED_PREF

/**
 * A login activity to start instagram session
 *
 * @author Justin Wong
 */
class LoginActivity : AppCompatActivity(), AuthenticationListener {

    companion object {
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(justin.apackage.com.hypemap.R.layout.activity_login)
    }

    fun onClick(view: View) {
        val dialog = AuthenticationDialog(this, this)
        dialog.setCancelable(true)
        dialog.show()
    }

    override fun onCookieReceived(cookie: String) {
        Log.d(TAG, "Received cookie: $cookie")
        val preferences = applicationContext.getSharedPreferences(HYPEMAP_SHARED_PREF, Context.MODE_PRIVATE)
        preferences.edit().putString(INSTAGRAM_COOKIE_KEY, cookie).apply()
        val myIntent = Intent(this, MapsActivity::class.java)
        startActivity(myIntent)
    }
}