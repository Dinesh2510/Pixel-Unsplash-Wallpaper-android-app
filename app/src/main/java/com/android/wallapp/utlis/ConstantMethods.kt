package com.android.wallapp.utlis

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity

class ConstantMethods {
    var activity: AppCompatActivity? = null

    companion object {

        @JvmField
        var bitmap: Bitmap? = null

        @JvmField
        var GIF_NAME:String = ""
        @JvmField
        var GIF_PATH :String= ""

        const val DELAY_REFRESH = 100

        const val WALLPAPER_SQUARE = 0
        const val WALLPAPER_RECTANGLE = 1

        const val PAGER_NUMBER = 3

        var CURRENT_POSITION = -1
        //public static int SPAN_COUNT;

        const val GRID_SMALL = "sm_grid"
        const val GRID_MEDIUM = "md_grid"

        const val CIRCULAR = "circular"
        const val ROUNDED = "rounded"

        const val THUMBNAIL_WIDTH = 150
        const val THUMBNAIL_HEIGHT = 270

        const val YOUTUBE_IMAGE_FRONT = "http://img.youtube.com/vi/"
        const val YOUTUBE_IMAGE_BACK_MQ = "/mqdefault.jpg"
        const val YOUTUBE_IMAGE_BACK_HQ = "/hqdefault.jpg"

        const val ONLINE_JSON = 1
        const val OFFLINE_STRINGS = 0

        const val POST_PER_PAGE = 10

        const val MAX_RELATED_POSTS = 10

        const val WALLPAPER_2_COLUMNS = 2
        const val WALLPAPER_3_COLUMNS = 3
        const val WALLPAPER_4_COLUMNS = 4

        const val CATEGORY_LIST = 0
        const val CATEGORY_GRID_2 = 1
        const val CATEGORY_GRID_3 = 2

        //delay splash when remote config finish loading
        const val SPLASH_DURATION = 1000

        const val FONT_SIZE_XSMALL = 12
        const val FONT_SIZE_SMALL = 14
        const val FONT_SIZE_MEDIUM = 16
        const val FONT_SIZE_LARGE = 18
        const val FONT_SIZE_XLARGE = 20

        const val MAX_RETRY_TOKEN = 10

        const val DOWNLOAD = "download"
        const val SHARE = "share"
        const val SET_WITH = "setWith"
        const val SET_GIF = "setGif"

        const val HOME_SCREEN = "home_screen"
        const val LOCK_SCREEN = "lock_screen"
        const val BOTH = "both"



        const val IMMEDIATE_APP_UPDATE_REQ_CODE = 124

        const val DELAY_SET = 2500
    }

}