package com.example.viewapplication.enumeration

enum class ArrowDirection {
    PV_TO_INVERTER, // PV - 逆变器
    INVERTER_TO_CENTER, // 逆变器 - 中间
    CENTER_TO_TOP, // 中间 - 顶部
    TOP_TO_CENTER, // 顶部 - 中间
    CENTER_TO_AC, // 中间 - AC Couple
    AC_TO_CENTER, // AC Couple - 中间
    CENTER_TO_GRID_LOAD, // 中间 - 电网负载
    BATTERY_TO_AC, // 电池 - AC Couple
    AC_TO_BATTERY, // AC Couple - 电池
    AC_TO_BACK_UP_LOAD, // AC Couple - Backup 负载
    TOP_CENTER_TO_GRID, // 中间顶部 - 电网
    GRID_TO_TOP_CENTER, // 电网 - 中间顶部

    // *********** 多个AC的情况 ********** //
    TOP_AC_TO_BOTTOM_AC, // 上AC - 下AC
    BOTTOM_AC_TO_TOP_AC, // 下AC - 上AC
}