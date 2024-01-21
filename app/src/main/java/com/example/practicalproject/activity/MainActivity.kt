package com.example.practicalproject.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.practicalproject.R
import com.example.practicalproject.databinding.ActivityMainBinding
import com.example.practicalproject.fragment.SplashFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var progressDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        changeFragment(SplashFragment())
        setUpDialog()
    }

    fun changeFragment(
        fragment: Fragment,
        addToBackStack: Boolean = false
    ) {
        val transaction = supportFragmentManager.beginTransaction()

        transaction.setCustomAnimations(
            R.anim.slide_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.slide_out
        ).replace(R.id.container, fragment)

        if (addToBackStack)
            transaction.addToBackStack(fragment::class.java.simpleName)

        transaction.commit()
    }

    private fun setUpDialog() {
        val isTransParentBackgroundForLoaderDialog = true
        val dialogBuilder = AlertDialog.Builder(this, R.style.AlertDialog)
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.progress_bar_layout, null, false)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)
        progressDialog = dialogBuilder.create()
        if (isTransParentBackgroundForLoaderDialog) {
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            progressDialog.window?.let {
                with(it) {
                    setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
                    clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                }
            }
        }
    }

    fun showLoader() {
        if (::progressDialog.isInitialized) {
            progressDialog.show()
        } else {
            setUpDialog()
            progressDialog.show()
        }
    }

    fun hideLoader() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }
}