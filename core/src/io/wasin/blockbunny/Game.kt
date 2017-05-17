package io.wasin.blockbunny

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.wasin.blockbunny.handlers.Content
import io.wasin.blockbunny.handlers.GameStateManager
import io.wasin.blockbunny.handlers.MyInput
import io.wasin.blockbunny.handlers.MyInputProcessor

class Game : ApplicationAdapter() {
    lateinit var sb: SpriteBatch
        private set
    lateinit var cam: OrthographicCamera
        private set
    lateinit var hudCam: OrthographicCamera
        private set
    lateinit var gsm: GameStateManager
        private set

    private var accum: Float = 0.0f

    companion object {
        const val TITLE = "Block Bunny"
        const val V_WIDTH = 320f
        const val V_HEIGHT = 240f
        const val SCALE = 2
        const val STEP = 1 / 60f

        var res: Content = Content()
    }

    override fun create() {

        Gdx.input.inputProcessor = MyInputProcessor()

        sb = SpriteBatch()
        cam = OrthographicCamera()
        cam.setToOrtho(false, V_WIDTH, V_HEIGHT)
        hudCam = OrthographicCamera()
        hudCam.setToOrtho(false, V_WIDTH, V_HEIGHT)
        gsm = GameStateManager(this)

        res.loadTexture("images/bunny.png", "bunny")

        // set to begin with Play state
        gsm.pushState(GameStateManager.PLAY)
    }

    override fun render() {
        accum += Gdx.graphics.deltaTime
        while(accum >= STEP) {
            accum -= STEP
            gsm.update(STEP)
            gsm.render()
            MyInput.update()
        }
    }

    override fun dispose() {
    }
}
