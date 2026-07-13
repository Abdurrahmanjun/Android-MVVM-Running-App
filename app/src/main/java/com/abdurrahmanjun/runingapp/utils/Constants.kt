package com.abdurrahmanjun.runingapp.utils

object Constants {

    const val RUNNING_DATABASE_NAME = "running_db"

    const val REQUEST_CODE_LOCATION_PERMISSION = 0

    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

    const val TIMER_UPDATE_INTERVAL = 50L

    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_INTERVAL = 2000L

    // Momentum: glowing mint route. A wide translucent underlay + bright core fakes the neon glow.
    const val POLYLINE_COLOR = 0xFF5FE0C0.toInt()
    const val POLYLINE_GLOW_COLOR = 0x555FE0C0
    const val POLYLINE_WIDTH = 14f
    const val POLYLINE_GLOW_WIDTH = 34f
    const val MAP_ZOOM = 16f

    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Tracking"
    const val NOTIFICATION_ID = 1
}