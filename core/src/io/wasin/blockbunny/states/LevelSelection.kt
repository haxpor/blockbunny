package io.wasin.blockbunny.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.TextureRegion
import io.wasin.blockbunny.Game
import io.wasin.blockbunny.handlers.Background
import io.wasin.blockbunny.handlers.GameStateManager
import io.wasin.blockbunny.handlers.TextRenderer

/**
 * Created by haxpor on 5/22/17.
 */
class LevelSelection(gsm: GameStateManager): GameState(gsm) {

    private var bg: Background
    lateinit private var textRenderer: TextRenderer

    init {
        var bgTexture = Game.res.getTexture("bgs")!!
        var bgTextureRegion = TextureRegion(bgTexture, 0, 0, bgTexture.width, bgTexture.height / 3)
        bg = Background(bgTextureRegion, hudCam, 0f)

        createTextRenderer()
    }

    fun createTextRenderer() {
        val texture = Game.res.getTexture("hud")!!
        var numberRegions = arrayListOf<TextureRegion>()

        // fixed size for font
        val fontWidth = 9
        val fontHeight = 9

        // 1. number regions
        // top row for 6 elements
        for (i in 0..5) {
            numberRegions.add(TextureRegion(texture, 32 + (i*fontWidth), 16, fontWidth, fontHeight))
        }

        // bottom row for 4 elements
        for (i in 0..3) {
            numberRegions.add(TextureRegion(texture, 32 + (i*fontWidth), 25, fontWidth, fontHeight))
        }

        // 2. slash region
        var slashRegion = TextureRegion(texture, 68, 25, fontWidth, fontHeight)

        // create text renderer
        textRenderer = TextRenderer(numberRegions.toTypedArray(), slashRegion)
    }

    override fun handleInput() {
    }

    override fun update(dt: Float) {
        handleInput()
        bg.update(dt)
    }

    override fun render() {
        // clear screen
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT)

        sb.projectionMatrix = hudCam.combined
        bg.render(sb)
    }

    override fun dispose() {

    }

    override fun updateScreenSize(width: Int, height: Int) {
        bg.updateScreenSize(width, height)
    }
}