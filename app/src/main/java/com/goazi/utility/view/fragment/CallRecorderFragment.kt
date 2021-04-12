package com.goazi.utility.view.fragment

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.goazi.utility.R
import com.goazi.utility.adapter.CallAdapter
import com.goazi.utility.background.captureImage.CameraService
import com.goazi.utility.background.deviceManager.AdminReceiver
import com.goazi.utility.databinding.FragmentCallRecorderBinding
import com.goazi.utility.misc.Constant.Companion.cameraRequestCode
import com.goazi.utility.misc.Util
import com.goazi.utility.model.Call
import com.goazi.utility.viewmodel.CallViewModel

class CallRecorderFragment : Fragment(), CallAdapter.OnCallCLickListener,
    View.OnClickListener {
    private val TAG = "CallRecorderFragment"

    private lateinit var fragmentActivity: FragmentActivity
    private lateinit var applicationContext: Context
    private var viewBinding: FragmentCallRecorderBinding? = null
    private lateinit var viewModel: CallViewModel
    private var calls: MutableList<Call>? = null
    private lateinit var root: View

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
        viewBinding = FragmentCallRecorderBinding.inflate(layoutInflater)
        initViews()
        return viewBinding!!.root
    }

    private fun initViews() {
        //set recycler view
        var adapter =
            CallAdapter(applicationContext, viewModel.getAllCalls.value, this)

        viewModel.getAllCalls.observe(viewLifecycleOwner, { list ->
            if (calls == null) {
                calls = list
                adapter = CallAdapter(applicationContext, viewModel.getAllCalls.value, this)
                viewBinding?.rvCall?.adapter = adapter
                viewBinding?.rvCall?.layoutManager = LinearLayoutManager(applicationContext)
                viewBinding?.rvCall?.setHasFixedSize(false)
            } else {
                adapter.updateList(list)
            }
        })
        //set the rest
        viewBinding?.btnCall?.setOnClickListener(this)
        viewBinding?.btnEnd?.setOnClickListener(this)

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
            viewBinding!!.root.findViewById<RelativeLayout>(R.id.fragment_call),
            "call $position"
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    override fun onClick(v: View?) {
        val id = v?.id

        if (id == R.id.btn_call) {
//            viewModel.insert(Call(0, "testName", "testDate", "testDuration"))

            if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                /*val captureImage: ACameraService =
                    CameraService(fragmentActivity as Activity, applicationContext)
                captureImage.startCapturing(this)*/
                val intent = Intent()
                intent.setClass(applicationContext, CameraService::class.java)
                applicationContext.startService(intent)
            }
        } else if (id == R.id.btn_end) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val display = fragmentActivity.display
                Log.d(TAG, "onClick: $display")
            }
        }
    }

    private fun getManifestPermissions() {
        requestPermissions(
            Array(1) { Manifest.permission.CAMERA },
            cameraRequestCode
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
            cameraRequestCode -> if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                /*Util.showSnackBar(
                    viewBinding!!.root.findViewById<RelativeLayout>(R.id.fragment_call),
                    "Camera Permission Granted"
                )*/
            } else {
                Util.showSnackBar(
                    viewBinding!!.root.findViewById<RelativeLayout>(R.id.fragment_call),
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