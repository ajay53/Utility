package com.goazi.utility.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.goazi.utility.R
import com.goazi.utility.adapter.CallAdapter
import com.goazi.utility.databinding.FragmentCallRecorderBinding
import com.goazi.utility.misc.Util
import com.goazi.utility.model.Call
import com.goazi.utility.viewmodel.CallViewModel

class CallRecorderFragment : Fragment(), CallAdapter.OnCallCLickListener, View.OnClickListener {
    private val TAG = "CallRecorderFragment"

    //    variables
    private lateinit var fragmentActivity: FragmentActivity
    private lateinit var applicationContext: Context
    private var viewBinding: FragmentCallRecorderBinding? = null
    private lateinit var viewModel: CallViewModel
    private var calls: MutableList<Call>? = null

    //    widgets
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
        Util.showSnackBar(root.findViewById<RelativeLayout>(R.id.fragment_call), "call $position")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    override fun onClick(v: View?) {
        val id = v?.id

        if (id == R.id.btn_call) {
            viewModel.insert(Call(0, "testName", "testDate", "testDuration"))
        }
    }
}