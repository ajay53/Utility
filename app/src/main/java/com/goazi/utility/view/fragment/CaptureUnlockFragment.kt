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
import com.goazi.utility.R
import com.goazi.utility.adapter.UnlockAdapter
import com.goazi.utility.databinding.FragmentUnlockBinding
import com.goazi.utility.misc.Constant.Companion.CAPTURE_IMAGE
import com.goazi.utility.misc.Constant.Companion.NO
import com.goazi.utility.misc.Constant.Companion.YES
import com.goazi.utility.model.Unlock
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
//        unlocks = viewModel.getAllUnlocks.value
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: ")
        //inflating to set related xml file(icon next to class name)
        inflater.inflate(R.layout.fragment_unlock, container, false)
        binding = FragmentUnlockBinding.inflate(layoutInflater)
        initViews()
        return binding!!.root
    }

    private fun initViews() {
        val sharedPref: SharedPreferences = fragmentActivity.getPreferences(Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        //set recycler view
        var adapter =
            UnlockAdapter(applicationContext, getCalls(), this)

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
            if (isChecked) {
                editor.putString(CAPTURE_IMAGE, YES)
            } else {
                editor.putString(CAPTURE_IMAGE, NO)
            }
            editor.apply()
        }
    }

    private fun getCalls(): MutableList<Unlock> {
        val calls: MutableList<Unlock> = ArrayList<Unlock>().toMutableList()

        for (i in 1..5) {
            val call = Unlock(
                i,
                "/data/user/0/com.goazi.utility/app_unlock_directory/profile.jpg",
                "time: $i"
            )
            calls += call
        }
        return calls
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