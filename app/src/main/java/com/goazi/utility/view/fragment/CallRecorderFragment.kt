package com.goazi.utility.view.fragment

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.goazi.utility.R
import com.goazi.utility.adapter.CallAdapter
import com.goazi.utility.background.deviceManager.AdminReceiver
import com.goazi.utility.databinding.FragmentCallRecorderBinding
import com.goazi.utility.misc.Constant.Companion.CAMERA_REQUEST_CODE
import com.goazi.utility.misc.Constant.Companion.UNLOCK_DIRECTORY
import com.goazi.utility.misc.Util
import com.goazi.utility.model.Call
import com.goazi.utility.viewmodel.CallViewModel
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class CallRecorderFragment : Fragment(), CallAdapter.OnCallCLickListener,
    View.OnClickListener {
    companion object {
        private const val TAG = "CallRecorderFragment"
    }

    private lateinit var fragmentActivity: FragmentActivity
    private lateinit var applicationContext: Context
    private var binding: FragmentCallRecorderBinding? = null
    private lateinit var viewModel: CallViewModel
    private var calls: MutableList<Call>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        fragmentActivity = requireActivity()
        applicationContext = fragmentActivity.applicationContext
        viewModel = ViewModelProvider(this).get(CallViewModel::class.java)
        calls = viewModel.getAllCalls.value
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: ")
        //inflating to set related xml file(icon next to class name)
        inflater.inflate(R.layout.fragment_call_recorder, container, false)
        binding = FragmentCallRecorderBinding.inflate(layoutInflater)
        initViews()
        return binding!!.root
    }

    private fun initViews() {
        //set recycler view
        var adapter =
            CallAdapter(applicationContext, viewModel.getAllCalls.value, this)

        viewModel.getAllCalls.observe(viewLifecycleOwner, { list ->
            if (calls == null) {
                calls = list
                adapter = CallAdapter(applicationContext, viewModel.getAllCalls.value, this)
                binding?.rvCall?.adapter = adapter
                binding?.rvCall?.layoutManager = LinearLayoutManager(applicationContext)
                binding?.rvCall?.setHasFixedSize(false)
            } else {
                adapter.updateList(list)
            }
        })
        //set the rest
        binding?.btnCall?.setOnClickListener(this)
        binding?.btnEnd?.setOnClickListener(this)

        getManifestPermissions()
        getSpecialPermissions()
    }

    private fun getCalls(): List<Call> {
        val calls: MutableList<Call> = ArrayList<Call>().toMutableList()

        for (i in 1..5) {
            val call = Call(i, "name $i", "date $i", "duration $i")
            calls += call
        }
        return calls
    }

    override fun onCallClick(position: Int) {
        Util.showSnackBar(
            binding!!.root.findViewById<RelativeLayout>(R.id.fragment_call),
            "call $position"
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onClick(v: View?) {
        val id = v?.id

        if (id == R.id.btn_call) {
            viewModel.insert(Call(0, "testName", "testDate", "testDuration"))

            /* if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
                 == PackageManager.PERMISSION_GRANTED
             ) {
                 val intent = Intent()
                 intent.setClass(applicationContext, CameraService::class.java)
                 applicationContext.startService(intent)
             }*/
        } else if (id == R.id.btn_end) {
            try {
                val f = File("/data/user/0/com.goazi.utility/app_unlock_directory", "profile.jpg")
                val j = File(applicationContext.filesDir.path, "$UNLOCK_DIRECTORY/profile.jpg")
                val bitmap = BitmapFactory.decodeStream(FileInputStream(f))
                binding?.imgFront?.setImageBitmap(bitmap)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    private fun getManifestPermissions() {
        requestPermissions(
            Array(1) { Manifest.permission.CAMERA },
            CAMERA_REQUEST_CODE
        )
        /*requestPermissions(
            Array(1) {Manifest.permission.BIND_DEVICE_ADMIN },
            Constant.adminRequestCode
        )*/
    }

    private fun getSpecialPermissions() {
        val cn = ComponentName(applicationContext, AdminReceiver::class.java)

        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn)
        intent.putExtra(
            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
            "For Testing"
        )
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_REQUEST_CODE -> if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                /*Util.showSnackBar(
                    viewBinding!!.root.findViewById<RelativeLayout>(R.id.fragment_call),
                    "Camera Permission Granted"
                )*/
            } else {
                Util.showSnackBar(
                    binding!!.root.findViewById<RelativeLayout>(R.id.fragment_call),
                    "Camera Permission Denied"
                )
            }
            else -> { // Note the block
                Log.d(TAG, "onRequestPermissionsResult: Default case")
            }
        }
    }

    /*override fun onCaptureDone(pictureData: ByteArray) {
        fragmentActivity.runOnUiThread {
            val bitmap = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.size)
            viewBinding?.imgFront?.setImageBitmap(bitmap)
        }
    }*/
}