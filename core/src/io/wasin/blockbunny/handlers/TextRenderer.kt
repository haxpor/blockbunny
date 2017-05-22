package io.wasin.blockbunny.handlers

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion

/**
 * Created by haxpor on 5/22/17.
 *
 * TextRenderer accepts
 * - sequence of number starting from 0-9
 * - slash (/)
 */
class TextRenderer(numberFontTextureRegions: Array<TextureRegion>, slashFontTextureRegion: TextureRegion) {
    var numberFontTextureRegions: Array<TextureRegion> = numberFontTextureRegions
        private set
    var slashFontTextureRegion: TextureRegion = slashFontTextureRegion
        private set

    var fontWidth: Int = numberFontTextureRegions[0].regionWidth
        private set
    var fontHeight: Int = numberFontTextureRegions[0].regionHeight
        private set
    var fontHalfWidth: Int = fontWidth/2
        private set
    var fontHalfHeight: Int = fontHeight/2
        private set

    fun renderNumber(number: Int, x: Float, y: Float, sb: SpriteBatch) {
        // use the same region width to offset
        // assume that it's equal in size for all individual font

        var numStr = number.toString()
        val length = numStr.length
        val startX = x - length/2f*fontWidth
        val posY = y - fontHeight/2f

        for (i in 0..length-1) {
            val regionIndex = numStr.get(i).toInt() - 48
            sb.draw(numberFontTextureRegions[regionIndex], startX + (i * fontWidth), posY)
        }
    }

    fun renderSlash(x: Float, y: Float, sb: SpriteBatch) {
        sb.draw(slashFontTextureRegion, x, y)
    }
}