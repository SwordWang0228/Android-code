package com.lee.calendar.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * @author jv.lee
 * @date 2020/11/10
 * @description 日历容器 - 日历View的滑动交互必须在当前view中完成
 */
class CalendarLinearLayout(context: Context, attributeSet: AttributeSet) :
    LinearLayout(context, attributeSet) {

    private val TAG = "CalendarLinearLayout"

    //当前日历view 是否展开标识
    private var expansionEnable = true

    //当前是否为滑动状态 控制是否在滑动结束后没有到达目标高度 使用动画设置 min/max 高度
    private var isScrollTouch = false
    private var viewHeight: Int = 0

    private var startRawY = 0F
    private var startY = 0f
    private var startX = 0f
    private var mTouchSlop = 0

    //临时记录当前平移值
    private var tempTranslate = 0F

    private val mAnimation = PagerAnimation()
    private var mCalendarView: CalendarView? = null
    private var mRecyclerView: RecyclerView? = null

    init {
        //初始化系统拖动阈值
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_DOWN) {
            startRawY = e.rawY
            startY = e.y
            startX = e.x
            setAnimViewHeight()
        } else if (e.action == MotionEvent.ACTION_MOVE) {
            // 获取当前手指位置
            val endY: Float = e.y
            val endX: Float = e.x
            val distanceX = abs(endX - startX)
            val distanceY = abs(endY - startY)

            val scrollTop = startY > e.y

            //横向滚动 交给子view处理
            if (distanceX > mTouchSlop && distanceX > distanceY) {
                return false
                //垂直滚动 处理滑动
            } else if (distanceY > mTouchSlop && distanceY > distanceX) {
                //事件列表在顶部 日历为展开状态 向上滑动 return true->交给父容器处理
                if (isEventListTop() && expansionEnable && scrollTop) {
                    return true
                    //事件列表在顶部 日历为非展开状态 向上滑动 return false->交给子View处理
                } else if (isEventListTop() && !expansionEnable && scrollTop) {
                    return false
                    //事件列表在顶部 日历为非展开状态 向下滑动 return true->交给父容器处理
                } else if (isEventListTop() && !expansionEnable && !scrollTop) {
                    return true
                    //事件列表在底部 日历为非展开状态 向下滑动 return false->交给子View处理
                } else if (isEventListBottom() && !expansionEnable && !scrollTop) {
                    return false
                }
                return true
            }

        }
        return super.onInterceptTouchEvent(e)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        mCalendarView?.let {
            val rowIndex = it.getMonthAdapter()?.getRowIndex() ?: 0
            Log.i(TAG, "onTouchEvent: $rowIndex")

            if (event.action == MotionEvent.ACTION_MOVE) {
                isScrollTouch = true
                expansionEnable = event.rawY > startRawY

                //开始滑动即为展开状态显示 monthViewPager
                it.switchMonthOrWeekPager(true)

                val newHeight = (viewHeight + (event.rawY - startRawY)).toInt()

                val itemTranslationY = -(((it.getMaxHeight() - newHeight) / 5) * rowIndex).toFloat()

                if (newHeight in it.getMinHeight()..it.getMaxHeight()) {
                    tempTranslate = itemTranslationY
                    it.getMonthPagerView().translationY = itemTranslationY
                    it.layoutParams.height = newHeight
                    it.requestLayout()

                } else if (newHeight < it.getMinHeight()) {
                    it.getMonthPagerView().translationY =
                        -(((it.getMinHeight() - it.getWeekLayoutHeight()) * rowIndex)).toFloat()
                    expansionEnable = false
                    it.layoutParams.height = it.getMinHeight()
                    it.requestLayout()
                    //当前状态该为非展开状态 显示WeekViewPager
                    it.switchMonthOrWeekPager(false)

                } else if (newHeight > it.getMaxHeight()) {
                    it.getMonthPagerView().translationY = 0f
                    expansionEnable = true
                    it.layoutParams.height = it.getMaxHeight()
                    it.requestLayout()
                }

                return true

            } else if (event.action == MotionEvent.ACTION_UP) {
                mCalendarView?.let {
                    if (it.height != it.getMinHeight() && it.height != it.getMaxHeight() && isScrollTouch) {
                        isScrollTouch = false
                        mAnimation.setDimensions(
                            if (expansionEnable) it.getMaxHeight() else it.getMinHeight(),
                            it.height
                        )
                        mAnimation.setTranslationDimensions(if (expansionEnable) 0F else -((it.getMinHeight() - it.getWeekLayoutHeight()) * rowIndex).toFloat(), tempTranslate, expansionEnable)
                        mAnimation.duration = 200
                        it.startAnimation(mAnimation)
                    }
                }
                return true
            }

        }
        return super.onTouchEvent(event)
    }

    fun bindEventView(calendarView: CalendarView, recyclerView: RecyclerView?) {
        this.mCalendarView = calendarView
        this.mRecyclerView = recyclerView
    }

    /**
     * 设置动画view 高度
     */
    private fun setAnimViewHeight() {
        mCalendarView?.run { viewHeight = bottom - top }
    }

    /**
     * 当前列表是否为顶部
     */
    private fun isEventListTop(): Boolean {
        mRecyclerView?.let {
            val linearLayoutManager: LinearLayoutManager
            if (it.layoutManager is LinearLayoutManager) {
                linearLayoutManager = it.layoutManager as LinearLayoutManager
            } else {
                return false
            }
            return linearLayoutManager.findFirstVisibleItemPosition() == 0 || linearLayoutManager.itemCount == 0
        }
        return false
    }

    /**
     * 当前列表是否为底部
     */
    private fun isEventListBottom(): Boolean {
        mRecyclerView?.let {
            val linearLayoutManager: LinearLayoutManager
            if (it.layoutManager is LinearLayoutManager) {
                linearLayoutManager = it.layoutManager as LinearLayoutManager
            } else {
                return false
            }
            return linearLayoutManager.findLastVisibleItemPosition() == linearLayoutManager.itemCount - 1
        }
        return false
    }

    private inner class PagerAnimation : Animation() {
        private var targetHeight = 0
        private var currentHeight = 0
        private var heightChange = 0

        fun setDimensions(targetHeight: Int, currentHeight: Int) {
            this.targetHeight = targetHeight
            this.currentHeight = currentHeight
            heightChange = targetHeight - currentHeight
        }

        private var targetTranslationY = 0F
        private var currentTranslationY = 0F
        private var translationYChange = 0F
        private var expansionEnable = false

        fun setTranslationDimensions(
            targetTranslation: Float,
            currentTranslation: Float,
            expansionEnable: Boolean
        ) {
            this.targetTranslationY = targetTranslation
            this.currentTranslationY = currentTranslation
            this.expansionEnable = expansionEnable
            translationYChange = targetTranslationY - currentTranslationY
        }

        override fun applyTransformation(
            interpolatedTime: Float,
            t: Transformation
        ) {
            mCalendarView?.let {
                if (interpolatedTime >= 1) {
                    it.layoutParams.height = targetHeight

                    it.getMonthPagerView().translationY = targetTranslationY

                    it.switchMonthOrWeekPager(expansionEnable)
                    setAnimViewHeight()
                } else {
                    val stepHeight = (heightChange * interpolatedTime).toInt()
                    it.layoutParams.height = currentHeight + stepHeight

                    val stepTranslation = (Math.abs(translationYChange) * interpolatedTime)
                    val endTranslation = if (expansionEnable) -(Math.abs(currentTranslationY) -
                            stepTranslation) else -(Math.abs(currentTranslationY) + stepTranslation)
                    it.getMonthPagerView().translationY = endTranslation

                }
                it.requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

}