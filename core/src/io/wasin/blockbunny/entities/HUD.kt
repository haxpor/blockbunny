package io.wasin.blockbunny.entities

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Disposable
import io.wasin.blockbunny.Game
import io.wasin.blockbunny.handlers.B2DVars
import kotlin.experimental.and

/**
 * Created by haxpor on 5/18/17.
 */
class HUD(player: Player): Disposable {
    private var player: Player = player
    private var blocks: Array<TextureRegion?> = Array(3, { i -> null })
    private var backBase: TextureRegion
    private var crystal: TextureRegion
    private var font: BitmapFont

    init {
        var tex = Game.res.getTexture("hud")

        for (i in 0..2) {
            blocks[i] = TextureRegion(tex, 32 + i*16, 0, 16, 16)
        }

        backBase = TextureRegion(tex, 0, 0, 32, 32)
        crystal = TextureRegion(tex, 80, 0, 16, 16)

        font = BitmapFont()
    }

    fun render(sb: SpriteBatch) {
        // draw base
        sb.draw(backBase, 40f - backBase.regionWidth/4f, 200f - backBase.regionHeight/4f)

        // draw current active platform switching
        val bits = player.body.fixtureList.first().filterData.maskBits

        if ((bits and B2DVars.BIT_RED) != 0.toShort()) {
            sb.draw(blocks[0], 40f, 200f)
        }
        if ((bits and B2DVars.BIT_GREEN) != 0.toShort()) {
            sb.draw(blocks[1], 40f, 200f)
        }
        if ((bits and B2DVars.BIT_BLUE) != 0.toShort()) {
            sb.draw(blocks[2], 40f, 200f)
        }

        // draw crystals
        sb.draw(crystal, 40f - backBase.regionWidth/4f + 66f + crystal.regionWidth/2f, 200f - backBase.regionHeight/4f + crystal.regionHeight/2f)

        // draw text
        font.draw(sb, "${player.getNumCrystals()} / ${player.getTotalCrystals()}", 40f - backBase.regionWidth/4f + 66f + crystal.regionWidth + 30f, 200f + backBase.regionHeight/4f + crystal.regionHeight/2f - 2f)
    }

    override fun dispose() {
        font.dispose()
    }
}