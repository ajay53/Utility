package com.goazi.utility.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.goazi.utility.R
import com.goazi.utility.model.Unlock
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class UnlockAdapter(
    private val context: Context,
    private val unlocks: MutableList<Unlock>?,
    private val onUnlockCLickListener: OnUnlockCLickListener
) : RecyclerView.Adapter<UnlockAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.unlock_list_item, parent, false)

        return ViewHolder(itemView, onUnlockCLickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currItem = unlocks?.get(position)
        holder.tvDate.text = currItem?.date
        try {
            val f = File(currItem!!.path)
            val bitmap = BitmapFactory.decodeStream(FileInputStream(f))
            holder.imgUnlock.setImageBitmap(bitmap)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    fun updateList(unlocks: List<Unlock>) {
        this.unlocks?.clear()
        this.unlocks?.addAll(unlocks)
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        if (unlocks == null) {
            return 0
        }
        return unlocks.size
    }

    class ViewHolder(view: View, private val onUnlockCLickListener: OnUnlockCLickListener) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        var imgUnlock: ImageView = view.findViewById(R.id.img_unlock)
        var tvDate: TextView = view.findViewById(R.id.tv_date)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onUnlockCLickListener.onUnlockClick(adapterPosition)
        }
    }

    interface OnUnlockCLickListener {
        fun onUnlockClick(position: Int)
    }
}