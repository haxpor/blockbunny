package io.wasin.blockbunny.handlers

/**
 * Created by haxpor on 5/16/17.
 */
class B2DVars {
    companion object {
        // pixel per meters
        const val PPM: Float = 100f

        // category bits
        const val BIT_PLAYER: Short = 2
        const val BIT_RED: Short = 4
        const val BIT_GREEN: Short = 8
        const val BIT_BLUE: Short = 16
        const val BIT_CRYSTAL: Short = 32
        const val BIT_TOP_PLATFORM: Short = 64
        const val BIT_BOTTOM_PLATFORM: Short = 128
        const val BIT_LINE1_BLOCK: Short = 256
        const val BIT_LINE2_BLOCK: Short = 512
    }
}