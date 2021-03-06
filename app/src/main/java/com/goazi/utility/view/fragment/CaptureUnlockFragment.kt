package com.goazi.utility.view.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.goazi.utility.adapter.UnlockAdapter
import com.goazi.utility.background.BackgroundService
import com.goazi.utility.databinding.FragmentUnlockBinding
import com.goazi.utility.misc.Constant.Companion.CAPTURE_IMAGE
import com.goazi.utility.view.activity.NavigationActivity
import com.goazi.utility.view.activity.UnlockDetailActivity
import com.goazi.utility.viewmodel.UnlockViewModel

class CaptureUnlockFragment : Fragment(), UnlockAdapter.OnUnlockCLickListener {

    private lateinit var fragmentActivity: FragmentActivity
    private lateinit var applicationContext: Context
    private lateinit var viewModel: UnlockViewModel
    private var binding: FragmentUnlockBinding? = null
    private var unlockCount: Int = 0

    companion object {
        private const val TAG = "CaptureUnlockFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        fragmentActivity = requireActivity()
        applicationContext = fragmentActivity.applicationContext
        viewModel = ViewModelProvider(this).get(UnlockViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: ")
        binding = FragmentUnlockBinding.inflate(layoutInflater)
        initViews()
        return binding!!.root
    }

    private fun initViews() {
        /*val sharedPref: SharedPreferences =
            applicationContext.getSharedPreferences(DEFAULT, Context.MODE_PRIVATE)*/
        val editor: SharedPreferences.Editor = NavigationActivity.preferences.edit()
        //set recycler view
        var adapter =
            UnlockAdapter(applicationContext, viewModel.getAllUnlocks.value, this)

        viewModel.getAllUnlocks.observe(viewLifecycleOwner, { unlocks ->
            if (unlockCount == 0) {
                unlockCount = unlocks.size
                adapter =
                    UnlockAdapter(applicationContext, viewModel.getAllUnlocks.value, this)
                binding!!.rvUnlock.adapter = adapter
                binding!!.rvUnlock.layoutManager = LinearLayoutManager(applicationContext)
                binding!!.rvUnlock.setHasFixedSize(false)
            } else {
                adapter.updateList(unlocks)
            }
        })

        binding?.swCaptureUnlock?.setOnCheckedChangeListener { compoundButton: CompoundButton, isChecked: Boolean ->
            val intent = Intent()
                .setClass(applicationContext, BackgroundService::class.java)

            if (isChecked) {
                editor.putBoolean(CAPTURE_IMAGE, true)
                editor.apply()
                fragmentActivity.startService(intent)
            } else {
                editor.putBoolean(CAPTURE_IMAGE, false)
                editor.apply()
                fragmentActivity.stopService(intent)
            }
        }
    }

    override fun onUnlockClick(position: Int) {
        Log.d(TAG, "onUnlockClick: ")
        val unlockIntent = Intent()
            .setClass(applicationContext, UnlockDetailActivity::class.java)
            .putExtra("unlockObj", viewModel.getAllUnlocks.value?.get(position))
        fragmentActivity.startActivity(unlockIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}