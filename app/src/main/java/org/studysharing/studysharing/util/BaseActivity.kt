package org.studysharing.studysharing.util

import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View?) {}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return false
    }
}