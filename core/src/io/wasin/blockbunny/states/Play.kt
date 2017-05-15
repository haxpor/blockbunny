package io.wasin.blockbunny.states

import com.badlogic.gdx.graphics.g2d.BitmapFont
import io.wasin.blockbunny.handlers.GameStateManager

/**
 * Created by haxpor on 5/16/17.
 */
class Play(gsm: GameStateManager) : GameState(gsm) {

    private var font: BitmapFont

    init {
        this.font = BitmapFont()
    }

    override fun handleInput() {
    }

    override fun update(dt: Float) {
    }

    override fun render() {
        sb.projectionMatrix = cam.combined
        sb.begin()
        font.draw(sb, "Play state", 100.0f, 100.0f)
        sb.end()
    }

    override fun dispose() {
    }
}