package com.goazi.utility.misc

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import com.goazi.utility.R
import com.google.android.material.snackbar.Snackbar

class Util {
    companion object {
        private const val TAG = "Util"

        fun showSnackBar(view: View, message: String) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
        }
    }
}