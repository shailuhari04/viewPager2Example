package com.droidplusplus.viewpage2example.viewpagerWay

import android.content.Context
import android.graphics.Camera
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import kotlin.math.min

class ViewPagerActivity : AppCompatActivity() {
    private lateinit var mPager: ViewPager
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
        mPager.adapter = SlidingImageAdapter(this, ImagesArray)
//        val indicator: CirclePageIndicator = findViewById(R.id.indicator) as CirclePageIndicator
//        indicator.setViewPager(mPager)
//        val density = resources.displayMetrics.density
//        indicator.setRadius(5 * density)
        NUM_PAGES = IMAGES.size


        // Auto start of viewpager
        val handler = Handler(Looper.getMainLooper())
        val mRunnable = Runnable {
            if (currentPage == NUM_PAGES) {
                currentPage = 0
            }
            mPager.setCurrentItem(currentPage++, true)
        }
        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(mRunnable)
            }
        }, 2000, 2000)

        // Pager listener over indicator
//        indicator.setOnPageChangeListener(object : ViewPager.OnPageChangeListener() {
//            override fun onPageSelected(position: Int) {
//                currentPage = position
//            }
//
//            override fun onPageScrolled(pos: Int, arg1: Float, arg2: Int) {}
//            override fun onPageScrollStateChanged(pos: Int) {}
//        })

        //mPager.setPageTransformer(true, TabletPageTransformer())
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

class ZoomOutPageTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val pageWidth = page.width
        val pageHeight = page.height
        if (position < -1) { // [ -Infinity,-1 )
            // This page is way off-screen to the left.
            page.alpha = 0f
        } else if (position <= 1) { // [ -1,1 ]
            // Modify the default slide transition to shrink the page as well
            val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
            val vertMargin = pageHeight * (1 - scaleFactor) / 2
            val horzMargin = pageWidth * (1 - scaleFactor) / 2
            if (position < 0) {
                page.translationX = horzMargin - vertMargin / 2
            } else {
                page.translationX = -horzMargin + vertMargin / 2
            }

            // Scale the page down ( between MIN_SCALE and 1 )
            page.scaleX = scaleFactor
            page.scaleY = scaleFactor

            // Fade the page relative to its size.
            page.alpha = MIN_ALPHA +
                    (scaleFactor - MIN_SCALE) /
                    (1 - MIN_SCALE) * (1 - MIN_ALPHA)
        } else { // ( 1,+Infinity ]
            // This page is way off-screen to the right.
            page.alpha = 0f
        }
    }

    companion object {
        private const val MIN_SCALE = 0.85f
        private const val MIN_ALPHA = 0.5f
    }
}

class DepthPageTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val pageWidth = page.width
        if (position < -1) { // [ -Infinity,-1 )
            // This page is way off-screen to the left.
            page.alpha = 0f
        } else if (position <= 0) { // [-1,0]
            // Use the default slide transition when moving to the left page
            page.alpha = 1f
            page.translationX = 0f
            page.scaleX = 1f
            page.scaleY = 1f
        } else if (position <= 1) { // (0,1]
            // Fade the page out.
            page.alpha = 1 - position

            // Counteract the default slide transition
            page.translationX = pageWidth * -position

            // Scale the page down ( between MIN_SCALE and 1 )
            val scaleFactor = (MIN_SCALE
                    + (1 - MIN_SCALE) * (1 - Math.abs(position)))
            page.scaleX = scaleFactor
            page.scaleY = scaleFactor
        } else { // ( 1, +Infinity ]
            // This page is way off-screen to the right.
            page.alpha = 0f
        }
    }

    companion object {
        private const val MIN_SCALE = 0.75f
    }
}

class ZoomInTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, pos: Float) {
        val scale = if (pos < 0) pos + 1f else Math.abs(1f - pos)
        page.scaleX = scale
        page.scaleY = scale
        page.pivotX = page.width * 0.5f
        page.pivotY = page.height * 0.5f
        page.alpha = if (pos < -1f || pos > 1f) 0f else 1f - (scale - 1f)
    }

    companion object {
        const val MAX_ROTATION = 90.0f
    }
}

class CubeInPageTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        // Rotate the fragment on the left or right edge
        page.pivotX = (if (position > 0) 0f else page.width.toFloat())
        page.pivotY = 0f
        page.rotationY = -90f * position
    }
}

class CubeOutPageTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, pos: Float) {
        page.pivotX = (if (pos < 0f) page.width.toFloat() else 0f)
        page.pivotY = page.height * 0.5f
        page.rotationY = 90f * pos
    }
}

class FlipHorizontalPageTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, pos: Float) {
        val rotation = 180f * pos
        page.alpha = (if (rotation > 90f || rotation < -90f) 0f else 1.toFloat())
        page.pivotX = page.width * 0.5f
        page.pivotY = page.height * 0.5f
        page.rotationY = rotation
    }
}

class FlipVerticalPageTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, pos: Float) {
        val rotation = -180f * pos
        page.alpha = if (rotation > 90f || rotation < -90f) 0f else 1f
        page.pivotX = page.width * 0.5f
        page.pivotY = page.height * 0.5f
        page.rotationX = rotation
    }
}

class ForegroundToBackgroundPageTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, pos: Float) {
        val height = page.height.toFloat()
        val width = page.width.toFloat()
        val scale: Float = min(if (pos > 0) 1f else Math.abs(1f + pos), 1f)
        page.scaleX = scale
        page.scaleY = scale
        page.pivotX = width * 0.5f
        page.pivotY = height * 0.5f
        page.translationX = if (pos > 0) width * pos else -width * pos * 0.25f
    }
}

class BackgroundToForegroundPageTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, pos: Float) {
        val height = page.height.toFloat()
        val width = page.width.toFloat()
        val scale: Float = min(if (pos < 0) 1f else Math.abs(1f - pos), 1f)
        page.scaleX = scale
        page.scaleY = scale
        page.pivotX = width * 0.5f
        page.pivotY = height * 0.5f
        page.translationX = if (pos < 0) width * pos else -width * pos * 0.25f
    }
}

class RotateUpPageTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, pos: Float) {
        val width = page.width.toFloat()
        val height = page.height.toFloat()
        val rotation = ROTATION * pos * -1.25f
        page.pivotX = width * 0.5f
        page.pivotY = height
        page.rotation = rotation
    }

    companion object {
        private const val ROTATION = -15f
    }
}

class RotateDownPageTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val width = page.width.toFloat()
        val rotation = ROTATION * position
        page.pivotX = width * 0.5f
        page.pivotY = 0f
        page.translationX = 0f
        page.rotation = rotation
    }

    companion object {
        private const val ROTATION = -15f
    }
}

class TabletPageTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, pos: Float) {
        val rotation = (if (pos < 0) 30f else -30f) * Math.abs(pos)
        page.translationX = getOffsetX(rotation, page.width, page.height)
        page.pivotX = page.width * 0.5f
        page.pivotY = 0f
        page.rotationY = rotation
    }

    private fun getOffsetX(rotation: Float, width: Int, height: Int): Float {
        MATRIX_OFFSET.reset()
        CAMERA_OFFSET.save()
        CAMERA_OFFSET.rotateY(Math.abs(rotation))
        CAMERA_OFFSET.getMatrix(MATRIX_OFFSET)
        CAMERA_OFFSET.restore()
        MATRIX_OFFSET.preTranslate(-width * 0.5f, -height * 0.5f)
        MATRIX_OFFSET.postTranslate(width * 0.5f, height * 0.5f)
        TEMP_FLOAT_OFFSET[0] = width.toFloat()
        TEMP_FLOAT_OFFSET[1] = height.toFloat()
        MATRIX_OFFSET.mapPoints(TEMP_FLOAT_OFFSET)
        return (width - TEMP_FLOAT_OFFSET[0]) * if (rotation > 0.0f) 1.0f else -1.0f
    }

    companion object {
        private val MATRIX_OFFSET: Matrix = Matrix()
        private val CAMERA_OFFSET: Camera = Camera()
        private val TEMP_FLOAT_OFFSET = FloatArray(2)
    }
}