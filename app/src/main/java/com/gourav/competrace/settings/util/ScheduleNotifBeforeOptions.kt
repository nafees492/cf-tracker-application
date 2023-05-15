package com.gourav.competrace.settings.util

enum class ScheduleNotifBeforeOptions(val option: String, val value: Int) {
    TenMinutes(option = "10 Minutes", value = 10),
    ThirtyMinutes(option = "30 Minutes", value = 30),
    OneHour(option = "1 Hour", value = 60),
    TwoHour(option = "2 Hours", value = 120), ;

    companion object {
        fun getOption(value: Int) = values().find { it.value == value }?.option ?: OneHour.option

        fun getValue(option: String) = values().find { it.option == option }?.value ?: OneHour.value
    }
}
