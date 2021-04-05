package com.goazi.utility.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.goazi.utility.R
import com.goazi.utility.model.Call

class CallAdapter(
    private val context: Context,
    private val calls: MutableList<Call>?,
    private val onCallCLickListener: OnCallCLickListener
) : RecyclerView.Adapter<CallAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.call_list_item, parent, false)

        return ViewHolder(itemView, onCallCLickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currItem = calls?.get(position)

        holder.tvName.text = currItem?.name
        holder.tvDate.text = currItem?.date
        holder.tvDuration.text = currItem?.duration
    }

    fun updateList(calls: List<Call>) {
        this.calls?.clear()
        this.calls?.addAll(calls)
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        if (calls == null) {
            return 0
        }
        return calls.size
    }

    class ViewHolder(view: View, private val onCallCLickListener: OnCallCLickListener) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        var tvName: TextView = view.findViewById(R.id.tv_name)
        var tvDate: TextView = view.findViewById(R.id.tv_date)
        var tvDuration: TextView = view.findViewById(R.id.tv_duration)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onCallCLickListener.onCallClick(adapterPosition)
        }
    }

    interface OnCallCLickListener {
        fun onCallClick(position: Int)
    }
}