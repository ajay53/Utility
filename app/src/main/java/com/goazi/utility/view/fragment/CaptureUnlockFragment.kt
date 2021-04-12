package com.goazi.utility.view.fragment

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.goazi.utility.model.Unlock
import com.goazi.utility.viewmodel.UnlockViewModel

class CaptureUnlockFragment : Fragment() {

    private lateinit var fragmentActivity: FragmentActivity
    private lateinit var applicationContext: Context
    private lateinit var viewModel: UnlockViewModel
    private var unlocks: MutableList<Unlock>? = null
    private lateinit var root: View
}