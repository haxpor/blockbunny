package io.wasin.blockbunny.states

import com.badlogic.gdx.graphics.g2d.TextureRegion
import io.wasin.blockbunny.Game
import io.wasin.blockbunny.handlers.BBInput
import io.wasin.blockbunny.handlers.GameStateManager

/**
 * Created by haxpor on 5/30/17.
 */
class Mainmenu(gsm: GameStateManager): GameState(gsm) {

    private var bg: TextureRegion

    init {
        val texture = Game.res.getTexture("menu")!!
        bg = TextureRegion(texture, texture.width, texture.height)
    }

    override fun handleInput() {
        if (BBInput.isPressed(BBInput.BUTTON1) || BBInput.isPressed(BBInput.BUTTON2)) {
            // go back to level selection
            gsm.setState(GameStateManager.LEVEL_SELECTION)
        }
    }

    override fun update(dt: Float) {
        handleInput()
    }

    override fun render() {
        sb.begin()
        sb.projectionMatrix = cam.combined
        sb.draw(bg, 0f, 0f)
        sb.end()
    }

    override fun dispose() {

    }
}