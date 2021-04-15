package com.goazi.utility.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.goazi.utility.R

class GlideBindingAdapter {

    companion object {
        @BindingAdapter("imageUrl")
        fun setImageResource(view: ImageView, path: String) {
            val context = view.context

            Glide.with(context)
                .load(path)
                .placeholder(R.drawable.ic_menu_camera)
                .into(view)
        }
    }
}