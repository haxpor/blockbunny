package io.wasin.blockbunny.entities

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import io.wasin.blockbunny.Game
import io.wasin.blockbunny.handlers.B2DVars
import kotlin.experimental.and

/**
 * Created by haxpor on 5/18/17.
 */
class HUD(player: Player) {
    private var player: Player = player
    private var blocks: Array<TextureRegion?> = Array(3, { i -> null })

    init {
        var tex = Game.res.getTexture("hud")

        for (i in 0..2) {
            blocks[i] = TextureRegion(tex, 32 + i*16, 0, 16, 16)
        }
    }

    fun render(sb: SpriteBatch) {
        val bits = player.body.fixtureList.first().filterData.maskBits

        sb.begin()
        if ((bits and B2DVars.BIT_RED) != 0.toShort()) {
            sb.draw(blocks[0], 40f, 200f)
        }
        if ((bits and B2DVars.BIT_GREEN) != 0.toShort()) {
            sb.draw(blocks[1], 40f, 200f)
        }
        if ((bits and B2DVars.BIT_BLUE) != 0.toShort()) {
            sb.draw(blocks[2], 40f, 200f)
        }
        sb.end()
    }
}