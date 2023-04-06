package uz.jalil.viewpagerindicator

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback


/**
 *    Created by Jalil Boynazarov on 06.04.2023.
 *
 */

class DotsIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var dots: MutableList<ImageView>? = null
    private var viewPager2: ViewPager2? = null
    private var dotsSize = 0f
    private var dotsCornerRadius = 0f
    private var dotsSpacing = 0f
    private var currentPage = 0
    private var dotsWidthFactor = 0f
    private var dotsColor = 0
    private var selectedDotColor = 0
    private var dotsClickable = false
    private var isAllDot = false
    private var pageChangedListener2: OnPageChangeCallback? = null

    /**
     * Initiate views & attributes
     */
    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        dots = ArrayList()
        orientation = HORIZONTAL

        dotsSize = dpToPx(16).toFloat()
        dotsSpacing = dpToPx(14).toFloat()
        dotsCornerRadius = dotsSize / 2
        dotsClickable = true
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.DotsIndicator)

            /*   selectedDotColor =
                   a.getColor(R.styleable.DotsIndicator_selectedDotColor, DEFAULT_POINT_COLOR)
               dotsColor = a.getColor(R.styleable.DotsIndicator_dotsColor, DEFAULT_POINT_COLOR)*/
            dotsWidthFactor = a.getFloat(R.styleable.DotsIndicator_dotsWidthFactor, 2.5f)
            if (dotsWidthFactor < 1) {
                dotsWidthFactor = 2.5f
            }
            dotsSize = a.getDimension(R.styleable.DotsIndicator_dotsSize, dotsSize)
            dotsCornerRadius =
                a.getDimension(R.styleable.DotsIndicator_dotsCornerRadius, dotsSize / 2).toInt()
                    .toFloat()
            dotsSpacing = a.getDimension(R.styleable.DotsIndicator_dotsSpacing, dotsSpacing)
            isAllDot = a.getBoolean(R.styleable.DotsIndicator_dots_all, false)
            a.recycle()
        }
        if (isInEditMode) {
            addDots2(5)
            setUpSelectedColors(0)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        refreshDots2()
    }

    /**
     * Refresh dot for Viewpager2
     */
    private fun refreshDots2() {
        if (viewPager2 != null && viewPager2!!.adapter != null) {
            // Check if we need to refresh the dots count
            if (dots!!.size < viewPager2!!.adapter!!.itemCount) {
                addDots2(viewPager2!!.adapter!!.itemCount - dots!!.size)
            } else if (dots!!.size > viewPager2!!.adapter!!.itemCount) {
                removeDots(dots!!.size - viewPager2!!.adapter!!.itemCount)
            }
            setUpSelectedColors(currentPage)
            setUpDotsAnimators2()
        } else {
            Log.e(
                DotsIndicator::class.java.simpleName,
                "You have to set an adapter to the view pager before !"
            )
        }
    }

    /**
     * Add dot for Viewpager2
     */
    private fun addDots2(count: Int) {
        for (i in 0 until count) {
            val imageView: ImageView = createDotImage()
            imageView.setOnClickListener {
                if (dotsClickable && viewPager2 != null && viewPager2!!.adapter != null && i < viewPager2!!.adapter!!.itemCount) {
                    viewPager2!!.setCurrentItem(i, true)
                }
            }
            dots!!.add(imageView)
            addView(imageView)
            val lp = imageView.layoutParams as LayoutParams
            lp.setMargins(dotsSpacing.toInt(), 0, dotsSpacing.toInt(), 0)
        }
    }

    /**
     * Remove dot
     */
    private fun removeDots(count: Int) {
        for (i in 0 until count) {
            removeViewAt(childCount - 1)
            dots!!.removeAt(dots!!.size - 1)
        }
    }


    /**
     * Set Animation for Viewpager2
     */
    private fun setUpDotsAnimators2() {
        if (viewPager2 != null && viewPager2!!.adapter != null && viewPager2!!.adapter!!.itemCount > 0) {
            currentPage = viewPager2!!.currentItem
            if (currentPage >= dots!!.size) {
                currentPage = dots!!.size - 1
                viewPager2!!.setCurrentItem(currentPage, false)
            }
            if (pageChangedListener2 != null) {
                viewPager2!!.unregisterOnPageChangeCallback(pageChangedListener2!!)
            }
            setUpOnPageChangedListener2()
            viewPager2!!.registerOnPageChangeCallback(pageChangedListener2!!)
        }
    }

    /**
     * Set Page change listener for Viewpager2
     */
    private fun setUpOnPageChangedListener2() {
        pageChangedListener2 = object : OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
                calculateDotWidth(position, positionOffset)
            }

            override fun onPageSelected(position: Int) {
                setUpSelectedColors(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        }
    }

    /**
     * Calculate image width
     * @param position current position
     * @param positionOffset Value from [0, 1) indicating the offset from the page at position.
     */
    private fun calculateDotWidth(position: Int, positionOffset: Float) {
        if (position != currentPage && positionOffset == 0f || currentPage < position) {
            setDotWidth(dots!![currentPage], dotsSize.toInt())
            currentPage = position
        }
        if (Math.abs(currentPage - position) > 1) {
            setDotWidth(dots!![currentPage], dotsSize.toInt())
            currentPage = position
        }
        var dot: View = dots!![currentPage]
        var nextDot: View? = null
        if (currentPage == position && currentPage + 1 < dots!!.size) {
            nextDot = dots!![currentPage + 1]
        } else if (currentPage > position) {
            nextDot = dot
            dot = dots!![currentPage - 1]
        }
        val dotWidth = (dotsSize + dotsSize * (dotsWidthFactor - 1) * (1 - positionOffset)).toInt()
        setDotWidth(dot, dotWidth)
        if (nextDot != null) {
            val nextDotWidth =
                (dotsSize + dotsSize * (dotsWidthFactor - 1) * positionOffset).toInt()
            setDotWidth(nextDot, nextDotWidth)
        }
    }

    /**
     * Set dot width
     */
    private fun setDotWidth(dot: View, dotWidth: Int) {
        val dotParams: ViewGroup.LayoutParams = dot.layoutParams
        dotParams.width = if (isAllDot) dotsSize.toInt() else dotWidth
        dot.layoutParams = dotParams
    }

    /**
     * Set selected dot circle(stroke) color
     */
    private fun setUpSelectedColors(position: Int) {
        if (dots != null && dots!!.size > 0) {
            for (elevationItem in dots!!) {
                elevationItem.setImageResource(R.drawable.ic_dot)
            }
            dots!![position].setImageResource(R.drawable.ic_dot2)
        }
    }

    /**
     * Setup dot with ViewPager2
     */
    private fun setUpViewPager2() {
        if (viewPager2!!.adapter != null) {
            viewPager2!!.adapter!!.registerAdapterDataObserver(object : AdapterDataObserver() {
                override fun onChanged() {
                    super.onChanged()
                    refreshDots2()
                }
            })
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (context.resources.displayMetrics.density * dp).toInt()
    }

    /**
     * Attach ViewPager2
     */
    fun setViewPager2(viewPager2: ViewPager2?) {
        this.viewPager2 = viewPager2
        setUpViewPager2()
        refreshDots2()

    }

    fun createDotImage(): ImageView {
        val view = ImageView(context)
        view.setImageResource(R.drawable.ic_dot)
        return view
    }

}
