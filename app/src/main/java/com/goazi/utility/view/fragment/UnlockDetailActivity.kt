package com.goazi.utility.view.fragment

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.goazi.utility.R
import com.goazi.utility.databinding.ActivityUnlockDetailBinding
import com.goazi.utility.model.Unlock

class UnlockDetailActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "UnlockDetailActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        val binding: ActivityUnlockDetailBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_unlock_detail)
        val unlock: Unlock? = intent.extras?.getParcelable<Unlock>("unlockObj")
        binding.unlock = unlock
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        return if (id == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}