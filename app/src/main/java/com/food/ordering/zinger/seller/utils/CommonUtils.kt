package com.food.ordering.zinger.seller.utils

import java.text.SimpleDateFormat
import java.util.*

object CommonUtils {

    fun TimeConversion24to12(hours: Int,mins: Int): String{

        try {
            var _24HourTime = hours.toString()+":"+mins

            val _24HourSDF = SimpleDateFormat("HH:mm", Locale.US)
            val _12HourSDF = SimpleDateFormat("hh:mm a",Locale.US)
            val _24HourDt: Date = _24HourSDF.parse(_24HourTime)

            return _12HourSDF.format(_24HourDt)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    fun getStatus(status: String): String{
        when(status){
            AppConstants.STATUS.CANCELLED_BY_SELLER.name -> return "CANCELLED BY SELLER"
            AppConstants.STATUS.CANCELLED_BY_USER.name -> return "CANCELLED BY USER"
            AppConstants.STATUS.OUT_FOR_DELIVERY.name -> return "OUT FOR DELIVERY"
            AppConstants.STATUS.REFUND_INITIATED.name -> return "REFUND INITIATED"
            AppConstants.STATUS.REFUND_COMPLETED.name -> return "REFUND COMPLETED"
            AppConstants.STATUS.TXN_FAILURE.name -> return "TRANSACTION FAILURE"
        }
        return status
    }
}