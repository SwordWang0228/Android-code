package com.lee.calendar.adapter

import com.lee.calendar.R
import com.lee.calendar.entity.DateData
import com.lee.calendar.entity.DateEntity
import com.lee.calendar.widget.MonthView

/**
 * @author jv.lee
 * @date 2020/11/15
 * @description
 */
class MonthAdapter2(data:ArrayList<DateEntity>) :BaseCalendarPageAdapter2(data){
    override fun monthMode(): Int {
        return MonthView.MonthMode.MODE_MONTH
    }

    override fun getItemLayout(): Int {
        return R.layout.item_month_view
    }

    fun updateDayStatus(position: Int, arrayList: ArrayList<DateData>) {
        if (getData().isEmpty() || getData().size < position) {
            return
        }
        val monthEntity = getData()[position]
        for ((index, item) in arrayList.withIndex()) {
            if (monthEntity.dayList.size > index + monthEntity.startIndex) {
                val dayEntity = monthEntity.dayList[index + monthEntity.startIndex]
                dayEntity.dayStatus = item.status
                dayEntity.backgroundStatus = item.backgroundStatus
            }
        }
        notifyItemChanged(position)
    }

}