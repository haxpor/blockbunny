package io.wasin.blockbunny

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.wasin.blockbunny.handlers.GameStateManager

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
    }

    override fun create() {
        sb = SpriteBatch()
        cam = OrthographicCamera()
        cam.setToOrtho(false, V_WIDTH, V_HEIGHT)
        hudCam = OrthographicCamera()
        hudCam.setToOrtho(false, V_WIDTH, V_HEIGHT)
        gsm = GameStateManager(this)
    }

    override fun render() {
        accum += Gdx.graphics.deltaTime
        while(accum >= STEP) {
            accum -= STEP
            gsm.update(STEP)
            gsm.render()
        }
    }

    override fun dispose() {
    }
}
