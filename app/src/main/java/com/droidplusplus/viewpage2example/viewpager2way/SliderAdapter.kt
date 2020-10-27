package com.droidplusplus.viewpage2example.viewpager2way

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.droidplusplus.viewpage2example.R
import com.google.android.material.card.MaterialCardView

class SliderAdapter : ListAdapter<SliderItem, SliderAdapter.MViewHolder>(MDiffUtilCallback()) {

    lateinit var mViewPager2: ViewPager2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        return MViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.slider_item_container, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        holder.mCardView.setCardBackgroundColor(Color.parseColor(getItem(position).bgColor))
    }

    class MViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mCardView: MaterialCardView = view.findViewById(R.id.cardView)
    }

    private class MDiffUtilCallback : DiffUtil.ItemCallback<SliderItem>() {
        override fun areItemsTheSame(p0: SliderItem, p1: SliderItem): Boolean =
            p0.bgColor == p1.bgColor

        override fun areContentsTheSame(p0: SliderItem, p1: SliderItem): Boolean = p0.equals(p1)

    }


}