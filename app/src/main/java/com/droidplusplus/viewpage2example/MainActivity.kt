package com.droidplusplus.viewpage2example

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    lateinit var mViewPager2: ViewPager2

    val mSliderHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mViewPager2 = findViewById(R.id.imageSliderViewPager2)

        val dataList = mutableListOf<SliderItem>()
        dataList.add(SliderItem("#465959"))
        dataList.add(SliderItem("#000000"))
        dataList.add(SliderItem("#FFBB86FC"))
        dataList.add(SliderItem("#FF03DAC5"))

        val mAdapter = SliderAdapter()
        mAdapter.mViewPager2 = mViewPager2
        mAdapter.submitList(dataList)
        mViewPager2.adapter = mAdapter
        mViewPager2.clipToPadding = false
        mViewPager2.clipChildren = false
        mViewPager2.offscreenPageLimit = 3
        mViewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val mCompositePageTransformer = CompositePageTransformer()
        mCompositePageTransformer.addTransformer(MarginPageTransformer(40))
        mCompositePageTransformer.addTransformer(ViewPager2.PageTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        })

        mViewPager2.setPageTransformer(mCompositePageTransformer)

        mViewPager2.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mSliderHandler.removeCallbacks(mSliderRunnable)
                mSliderHandler.postDelayed(mSliderRunnable, 3000) //slider duration 3 Sec
            }
        })
    }

    private val mSliderRunnable = Runnable() {
        mViewPager2.currentItem = mViewPager2.currentItem +1
    }

    override fun onPause() {
        super.onPause()
        mSliderHandler.removeCallbacks(mSliderRunnable)
    }

    override fun onPostResume() {
        super.onPostResume()
        mSliderHandler.postDelayed(mSliderRunnable, 3000)
    }
}