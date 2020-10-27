package com.droidplusplus.viewpage2example.viewpagerWay

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.droidplusplus.viewpage2example.R
import java.util.*

class ViewPagerActivity : AppCompatActivity() {
    private var mPager: ViewPager? = null
    private var currentPage = 0
    private var NUM_PAGES = 0
    private var IMAGES =
        arrayOf<Int>(R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.five)
    private val ImagesArray = ArrayList<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager)
        init()
    }

    private fun init() {
        for (i in IMAGES.indices) ImagesArray.add(IMAGES.get(i))
        mPager = findViewById<ViewPager>(R.id.pager)
        mPager?.setAdapter(SlidingImageAdapter(this, ImagesArray))
//        val indicator: CirclePageIndicator = findViewById(R.id.indicator) as CirclePageIndicator
//        indicator.setViewPager(mPager)
//        val density = resources.displayMetrics.density
//        indicator.setRadius(5 * density)
        NUM_PAGES = IMAGES.size


        // Auto start of viewpager
        val handler = Handler()
        val Update = Runnable {
            if (currentPage == NUM_PAGES) {
                currentPage = 0
            }
            mPager!!.setCurrentItem(currentPage++, true)
        }
        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
            }
        }, 3000, 3000)

        // Pager listener over indicator
//        indicator.setOnPageChangeListener(object : ViewPager.OnPageChangeListener() {
//            override fun onPageSelected(position: Int) {
//                currentPage = position
//            }
//
//            override fun onPageScrolled(pos: Int, arg1: Float, arg2: Int) {}
//            override fun onPageScrollStateChanged(pos: Int) {}
//        })
    }
}

class SlidingImageAdapter(private val context: Context, private val IMAGES: ArrayList<Int>) :
    PagerAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return IMAGES.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): View {
        val imageLayout: View =
            inflater.inflate(R.layout.slidingimages_layout, view, false)!!
        val imageView = imageLayout
            .findViewById<View>(R.id.image) as ImageView
        imageView.setImageResource(IMAGES[position])
        view.addView(imageLayout, 0)
        return imageLayout
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }
    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}
    override fun saveState(): Parcelable? {
        return null
    }

}
