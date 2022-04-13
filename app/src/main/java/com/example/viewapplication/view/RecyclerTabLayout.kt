package com.example.viewapplication.view

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.viewapplication.R

/**
 * @author zhanzijian
 * @description
 * @date 2022/04/12 16:36
 */
class RecyclerTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {
    private var mIndicatorPaint: Paint
    private var mTabBackgroundResId = 0
    private var mTabOnScreenLimit = 0
    private var mTabMinWidth = 0
    private var mTabMaxWidth = 0
    private var mTabTextAppearance = 0
    private var mTabSelectedTextColor = 0
    private var mTabSelectedTextColorSet = false
    private var mTabPaddingStart = 0
    private var mTabPaddingTop = 0
    private var mTabPaddingEnd = 0
    private var mTabPaddingBottom = 0
    private var mIndicatorHeight = 0
    private var mLinearLayoutManager: LinearLayoutManager
    private var mRecyclerOnScrollListener: RecyclerOnScrollListener? = null
    private var mViewPager: ViewPager? = null
    private var mAdapter: Adapter<*>? = null
    private var mIndicatorPosition = 0
    private var mIndicatorGap = 0
    private var mIndicatorScroll = 0
    private var mOldPosition = 0
    private var mOldScrollOffset = 0
    private var mOldPositionOffset = 0f
    private var mPositionThreshold: Float
    private var mRequestScrollToTab = false
    private var mScrollEanbled = false
    private fun getAttributes(context: Context, attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.rtl_RecyclerTabLayout,
            defStyle, R.style.rtl_RecyclerTabLayout
        )
        setIndicatorColor(a.getColor(R.styleable.rtl_RecyclerTabLayout_rtl_tabIndicatorColor, 0))
        setIndicatorHeight(
            a.getDimensionPixelSize(
                R.styleable.rtl_RecyclerTabLayout_rtl_tabIndicatorHeight,
                0
            )
        )
        mTabTextAppearance = a.getResourceId(
            R.styleable.rtl_RecyclerTabLayout_rtl_tabTextAppearance,
            R.style.rtl_RecyclerTabLayout_Tab
        )
        mTabPaddingBottom = a
            .getDimensionPixelSize(R.styleable.rtl_RecyclerTabLayout_rtl_tabPadding, 0)
        mTabPaddingEnd = mTabPaddingBottom
        mTabPaddingTop = mTabPaddingEnd
        mTabPaddingStart = mTabPaddingTop
        mTabPaddingStart = a.getDimensionPixelSize(
            R.styleable.rtl_RecyclerTabLayout_rtl_tabPaddingStart, mTabPaddingStart
        )
        mTabPaddingTop = a.getDimensionPixelSize(
            R.styleable.rtl_RecyclerTabLayout_rtl_tabPaddingTop, mTabPaddingTop
        )
        mTabPaddingEnd = a.getDimensionPixelSize(
            R.styleable.rtl_RecyclerTabLayout_rtl_tabPaddingEnd, mTabPaddingEnd
        )
        mTabPaddingBottom = a.getDimensionPixelSize(
            R.styleable.rtl_RecyclerTabLayout_rtl_tabPaddingBottom, mTabPaddingBottom
        )
        if (a.hasValue(R.styleable.rtl_RecyclerTabLayout_rtl_tabSelectedTextColor)) {
            mTabSelectedTextColor = a
                .getColor(R.styleable.rtl_RecyclerTabLayout_rtl_tabSelectedTextColor, 0)
            mTabSelectedTextColorSet = true
        }
        mTabOnScreenLimit = a.getInteger(
            R.styleable.rtl_RecyclerTabLayout_rtl_tabOnScreenLimit, 0
        )
        if (mTabOnScreenLimit == 0) {
            mTabMinWidth = a.getDimensionPixelSize(
                R.styleable.rtl_RecyclerTabLayout_rtl_tabMinWidth, 0
            )
            mTabMaxWidth = a.getDimensionPixelSize(
                R.styleable.rtl_RecyclerTabLayout_rtl_tabMaxWidth, 0
            )
        }
        mTabBackgroundResId = a
            .getResourceId(R.styleable.rtl_RecyclerTabLayout_rtl_tabBackground, 0)
        mScrollEanbled = a.getBoolean(R.styleable.rtl_RecyclerTabLayout_rtl_scrollEnabled, true)
        a.recycle()
    }

    override fun onDetachedFromWindow() {
        if (mRecyclerOnScrollListener != null) {
            removeOnScrollListener(mRecyclerOnScrollListener!!)
            mRecyclerOnScrollListener = null
        }
        super.onDetachedFromWindow()
    }

    fun setIndicatorColor(color: Int) {
        mIndicatorPaint.color = color
    }

    fun setIndicatorHeight(indicatorHeight: Int) {
        mIndicatorHeight = indicatorHeight
    }

    fun setAutoSelectionMode(autoSelect: Boolean) {
        if (mRecyclerOnScrollListener != null) {
            removeOnScrollListener(mRecyclerOnScrollListener!!)
            mRecyclerOnScrollListener = null
        }
        if (autoSelect) {
            mRecyclerOnScrollListener = RecyclerOnScrollListener(this, mLinearLayoutManager)
            addOnScrollListener(mRecyclerOnScrollListener!!)
        }
    }

    fun setPositionThreshold(positionThreshold: Float) {
        mPositionThreshold = positionThreshold
    }

    fun setUpWithViewPager(viewPager: ViewPager?) {
        val adapter = DefaultAdapter(viewPager)
        adapter.setTabPadding(mTabPaddingStart, mTabPaddingTop, mTabPaddingEnd, mTabPaddingBottom)
        adapter.setTabTextAppearance(mTabTextAppearance)
        adapter.setTabSelectedTextColor(mTabSelectedTextColorSet, mTabSelectedTextColor)
        adapter.setTabMaxWidth(mTabMaxWidth)
        adapter.setTabMinWidth(mTabMinWidth)
        adapter.setTabBackgroundResId(mTabBackgroundResId)
        adapter.setTabOnScreenLimit(mTabOnScreenLimit)
        setUpWithAdapter(adapter)
    }

    fun setUpWithAdapter(adapter: Adapter<*>) {
        mAdapter = adapter
        mViewPager = adapter.viewPager
        requireNotNull(mViewPager!!.adapter) { "ViewPager does not have a PagerAdapter set" }
        mViewPager!!.addOnPageChangeListener(ViewPagerOnPageChangeListener(this))
        setAdapter(adapter)
        scrollToTab(mViewPager!!.currentItem)
    }

    fun setCurrentItem(position: Int, smoothScroll: Boolean) {
        if (mViewPager != null) {
            mViewPager!!.setCurrentItem(position, smoothScroll)
            scrollToTab(mViewPager!!.currentItem)
            return
        }
        if (smoothScroll && position != mIndicatorPosition) {
            startAnimation(position)
        } else {
            scrollToTab(position)
        }
    }

    private fun startAnimation(position: Int) {
        var distance = 1f
        val view = mLinearLayoutManager.findViewByPosition(position)
        if (view != null) {
            val currentX = view.x + view.measuredWidth / 2f
            val centerX = measuredWidth / 2f
            distance = Math.abs(centerX - currentX) / view.measuredWidth
        }
        val animator: ValueAnimator = if (position < mIndicatorPosition) {
            ValueAnimator.ofFloat(distance, 0f)
        } else {
            ValueAnimator.ofFloat(-distance, 0f)
        }
        animator.duration = DEFAULT_SCROLL_DURATION
        animator.addUpdateListener { animation ->
            scrollToTab(
                position,
                animation.animatedValue as Float,
                true
            )
        }
        animator.start()
    }

    private fun scrollToTab(position: Int) {
        scrollToTab(position, 0f, false)
        mAdapter!!.currentIndicatorPosition = position
        mAdapter!!.notifyDataSetChanged()
    }

    private fun scrollToTab(position: Int, positionOffset: Float, fitIndicator: Boolean) {
        var scrollOffset = 0
        val selectedView = mLinearLayoutManager.findViewByPosition(position)
        val nextView = mLinearLayoutManager.findViewByPosition(position + 1)
        if (selectedView != null) {
            val width = measuredWidth
            val sLeft: Float =
                if (position == 0) 0f else width / 2f - selectedView.measuredWidth / 2f // left edge of selected tab
            val sRight = sLeft + selectedView.measuredWidth // right edge of selected tab
            if (nextView != null) {
                val nLeft = width / 2f - nextView.measuredWidth / 2f // left edge of next tab
                val distance =
                    sRight - nLeft // total distance that is needed to distance to next tab
                val dx = distance * positionOffset
                scrollOffset = (sLeft - dx).toInt()
                if (position == 0) {
                    val indicatorGap =
                        ((nextView.measuredWidth - selectedView.measuredWidth) / 2).toFloat()
                    mIndicatorGap = (indicatorGap * positionOffset).toInt()
                    mIndicatorScroll =
                        ((selectedView.measuredWidth + indicatorGap) * positionOffset).toInt()
                } else {
                    val indicatorGap =
                        ((nextView.measuredWidth - selectedView.measuredWidth) / 2).toFloat()
                    mIndicatorGap = (indicatorGap * positionOffset).toInt()
                    mIndicatorScroll = dx.toInt()
                }
            } else {
                scrollOffset = sLeft.toInt()
                mIndicatorScroll = 0
                mIndicatorGap = 0
            }
            if (fitIndicator) {
                mIndicatorScroll = 0
                mIndicatorGap = 0
            }
        } else {
            if (measuredWidth > 0 && mTabMaxWidth > 0 && mTabMinWidth == mTabMaxWidth) { //fixed size
                val width = mTabMinWidth
                val offset = (positionOffset * -width).toInt()
                val leftOffset = ((measuredWidth - width) / 2f).toInt()
                scrollOffset = offset + leftOffset
            }
            mRequestScrollToTab = true
        }
        updateCurrentIndicatorPosition(
            position,
            positionOffset - mOldPositionOffset,
            positionOffset
        )
        mIndicatorPosition = position
        stopScroll()
        if (position != mOldPosition || scrollOffset != mOldScrollOffset) {
            mLinearLayoutManager.scrollToPositionWithOffset(position, scrollOffset)
        }
        if (mIndicatorHeight > 0) {
            invalidate()
        }
        mOldPosition = position
        mOldScrollOffset = scrollOffset
        mOldPositionOffset = positionOffset
    }

    private fun updateCurrentIndicatorPosition(position: Int, dx: Float, positionOffset: Float) {
        if (mAdapter == null) {
            return
        }
        var indicatorPosition = -1
        if (dx > 0 && positionOffset >= mPositionThreshold - POSITION_THRESHOLD_ALLOWABLE) {
            indicatorPosition = position + 1
        } else if (dx < 0 && positionOffset <= 1 - mPositionThreshold + POSITION_THRESHOLD_ALLOWABLE) {
            indicatorPosition = position
        }
        if (indicatorPosition >= 0 && indicatorPosition != mAdapter!!.currentIndicatorPosition) {
            mAdapter!!.currentIndicatorPosition = indicatorPosition
            mAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onDraw(canvas: Canvas) {
        val view = mLinearLayoutManager.findViewByPosition(mIndicatorPosition)
        if (view == null) {
            if (mRequestScrollToTab) {
                mRequestScrollToTab = false
                scrollToTab(mViewPager!!.currentItem)
            }
            return
        }
        mRequestScrollToTab = false
        val left: Int
        val right: Int
        if (isLayoutRtl) {
            left = view.left - mIndicatorScroll - mIndicatorGap
            right = view.right - mIndicatorScroll + mIndicatorGap
        } else {
            left = view.left + mIndicatorScroll - mIndicatorGap
            right = view.right + mIndicatorScroll + mIndicatorGap
        }
        val top = height - mIndicatorHeight
        val bottom = height
        canvas.drawRect(
            left.toFloat(),
            top.toFloat(),
            right.toFloat(),
            bottom.toFloat(),
            mIndicatorPaint
        )
    }

    private val isLayoutRtl: Boolean
        get() = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL

    private class RecyclerOnScrollListener(
        private var mRecyclerTabLayout: RecyclerTabLayout,
        private var mLinearLayoutManager: LinearLayoutManager
    ) : OnScrollListener() {
        var mDx = 0
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            mDx += dx
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            when (newState) {
                SCROLL_STATE_IDLE -> {
                    if (mDx > 0) {
                        selectCenterTabForRightScroll()
                    } else {
                        selectCenterTabForLeftScroll()
                    }
                    mDx = 0
                }
                SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING -> {}
            }
        }

        private fun selectCenterTabForRightScroll() {
            val first = mLinearLayoutManager.findFirstVisibleItemPosition()
            val last = mLinearLayoutManager.findLastVisibleItemPosition()
            val center = mRecyclerTabLayout.width / 2
            for (position in first..last) {
                val view = mLinearLayoutManager.findViewByPosition(position)
                if (view!!.left + view.width >= center) {
                    mRecyclerTabLayout.setCurrentItem(position, false)
                    break
                }
            }
        }

        private fun selectCenterTabForLeftScroll() {
            val first = mLinearLayoutManager.findFirstVisibleItemPosition()
            val last = mLinearLayoutManager.findLastVisibleItemPosition()
            val center = mRecyclerTabLayout.width / 2
            for (position in last downTo first) {
                val view = mLinearLayoutManager.findViewByPosition(position)
                if (view!!.left <= center) {
                    mRecyclerTabLayout.setCurrentItem(position, false)
                    break
                }
            }
        }
    }

    private class ViewPagerOnPageChangeListener(private val mRecyclerTabLayout: RecyclerTabLayout) :
        OnPageChangeListener {
        private var mScrollState = 0
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            mRecyclerTabLayout.scrollToTab(position, positionOffset, false)
        }

        override fun onPageScrollStateChanged(state: Int) {
            mScrollState = state
        }

        override fun onPageSelected(position: Int) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                if (mRecyclerTabLayout.mIndicatorPosition != position) {
                    mRecyclerTabLayout.scrollToTab(position)
                }
            }
        }
    }

    abstract class Adapter<T : ViewHolder?>(var viewPager: ViewPager?) : RecyclerView.Adapter<T>() {
        var currentIndicatorPosition = 0

    }

    class DefaultAdapter(viewPager: ViewPager?) : Adapter<DefaultAdapter.ViewHolder?>(viewPager) {
        private var mTabPaddingStart = 0
        private var mTabPaddingTop = 0
        private var mTabPaddingEnd = 0
        private var mTabPaddingBottom = 0
        private var mTabTextAppearance = 0
        private var mTabSelectedTextColorSet = false
        private var mTabSelectedTextColor = 0
        private var mTabMaxWidth = 0
        private var mTabMinWidth = 0
        private var mTabBackgroundResId = 0
        private var mTabOnScreenLimit = 0
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val tabTextView = TabTextView(parent.context)
            if (mTabSelectedTextColorSet) {
                tabTextView.setTextColor(
                    tabTextView.createColorStateList(
                        tabTextView.currentTextColor, mTabSelectedTextColor
                    )
                )
            }
            ViewCompat.setPaddingRelative(
                tabTextView, mTabPaddingStart, mTabPaddingTop,
                mTabPaddingEnd, mTabPaddingBottom
            )
            tabTextView.setTextAppearance(parent.context, mTabTextAppearance)
            tabTextView.gravity = Gravity.CENTER
            tabTextView.maxLines = MAX_TAB_TEXT_LINES
            tabTextView.ellipsize = TextUtils.TruncateAt.END
            if (mTabOnScreenLimit > 0) {
                val width = parent.measuredWidth / mTabOnScreenLimit
                tabTextView.maxWidth = width
                tabTextView.minWidth = width
            } else {
                if (mTabMaxWidth > 0) {
                    tabTextView.maxWidth = mTabMaxWidth
                }
                tabTextView.minWidth = mTabMinWidth
            }
            tabTextView.setTextAppearance(tabTextView.context, mTabTextAppearance)
            if (mTabSelectedTextColorSet) {
                tabTextView.setTextColor(
                    tabTextView.createColorStateList(
                        tabTextView.currentTextColor, mTabSelectedTextColor
                    )
                )
            }
            if (mTabBackgroundResId != 0) {
                tabTextView.setBackgroundDrawable(
                    AppCompatResources.getDrawable(tabTextView.context, mTabBackgroundResId)
                )
            }
            tabTextView.layoutParams = createLayoutParamsForTabs()
            return ViewHolder(tabTextView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val title = viewPager!!.adapter!!.getPageTitle(position)
            holder.title.text = title
            holder.title.isSelected = currentIndicatorPosition == position
        }

        override fun getItemCount(): Int {
            return viewPager!!.adapter!!.count
        }

        fun setTabPadding(
            tabPaddingStart: Int, tabPaddingTop: Int, tabPaddingEnd: Int,
            tabPaddingBottom: Int
        ) {
            mTabPaddingStart = tabPaddingStart
            mTabPaddingTop = tabPaddingTop
            mTabPaddingEnd = tabPaddingEnd
            mTabPaddingBottom = tabPaddingBottom
        }

        fun setTabTextAppearance(tabTextAppearance: Int) {
            mTabTextAppearance = tabTextAppearance
        }

        fun setTabSelectedTextColor(
            tabSelectedTextColorSet: Boolean,
            tabSelectedTextColor: Int
        ) {
            mTabSelectedTextColorSet = tabSelectedTextColorSet
            mTabSelectedTextColor = tabSelectedTextColor
        }

        fun setTabMaxWidth(tabMaxWidth: Int) {
            mTabMaxWidth = tabMaxWidth
        }

        fun setTabMinWidth(tabMinWidth: Int) {
            mTabMinWidth = tabMinWidth
        }

        fun setTabBackgroundResId(tabBackgroundResId: Int) {
            mTabBackgroundResId = tabBackgroundResId
        }

        fun setTabOnScreenLimit(tabOnScreenLimit: Int) {
            mTabOnScreenLimit = tabOnScreenLimit
        }

        private fun createLayoutParamsForTabs(): LayoutParams {
            return LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT
            )
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var title: TextView

            init {
                title = itemView as TextView
                itemView.setOnClickListener {
                    val pos = adapterPosition
                    if (pos != NO_POSITION) {
                        viewPager!!.setCurrentItem(pos, true)
                    }
                }
            }
        }

        companion object {
            private const val MAX_TAB_TEXT_LINES = 2
        }
    }

    class TabTextView(context: Context?) : AppCompatTextView(context!!) {
        fun createColorStateList(defaultColor: Int, selectedColor: Int): ColorStateList {
            val states = arrayOfNulls<IntArray>(2)
            val colors = IntArray(2)
            states[0] = SELECTED_STATE_SET
            colors[0] = selectedColor
            // Default enabled state
            states[1] = EMPTY_STATE_SET
            colors[1] = defaultColor
            return ColorStateList(states, colors)
        }
    }

    companion object {
        private const val DEFAULT_SCROLL_DURATION: Long = 200
        private const val DEFAULT_POSITION_THRESHOLD = 0.6f
        private const val POSITION_THRESHOLD_ALLOWABLE = 0.001f
    }

    init {
        setWillNotDraw(false)
        mIndicatorPaint = Paint()
        getAttributes(context, attrs, defStyle)
        mLinearLayoutManager = object : LinearLayoutManager(getContext()) {
            override fun canScrollHorizontally(): Boolean {
                return mScrollEanbled
            }
        }
        mLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager = mLinearLayoutManager
        itemAnimator = null
        mPositionThreshold = DEFAULT_POSITION_THRESHOLD
    }
}