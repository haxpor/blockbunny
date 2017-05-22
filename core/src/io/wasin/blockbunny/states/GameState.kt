package io.wasin.blockbunny.states

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.wasin.blockbunny.Game
import io.wasin.blockbunny.handlers.GameStateManager
import io.wasin.blockbunny.interfaces.ScreenSizeChangedUpdatable

/**
 * Created by haxpor on 5/14/17.
 */
abstract class GameState(gsm: GameStateManager): ScreenSizeChangedUpdatable {
    protected var gsm: GameStateManager

    // for convenient in reference and use in derived class
    protected val game: Game
    protected val sb: SpriteBatch
    protected val cam: OrthographicCamera
    protected val hudCam: OrthographicCamera

    init {
        this.gsm = gsm
        this.game = gsm.game
        this.sb = gsm.game.sb
        this.cam = gsm.game.cam
        this.hudCam = gsm.game.hudCam
    }

    abstract fun handleInput()
    abstract fun update(dt: Float)
    abstract fun render()
    abstract fun dispose()
}