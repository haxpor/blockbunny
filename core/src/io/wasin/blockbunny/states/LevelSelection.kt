package io.wasin.blockbunny.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.TextureRegion
import io.wasin.blockbunny.Game
import io.wasin.blockbunny.handlers.*

/**
 * Created by haxpor on 5/22/17.
 */
class LevelSelection(gsm: GameStateManager): GameState(gsm) {

    companion object {
        // FIXME: This value is fixed as of now due to algorithm to render takes time to make it right, don't change this value!
        const val LEVEL_NUM: Int = 15
    }

    private var bg: Background
    lateinit private var textRenderer: TextRenderer
    lateinit private var levelButtons: Array<LevelButton>

    init {
        var bgTexture = Game.res.getTexture("bgs")!!
        var bgTextureRegion = TextureRegion(bgTexture, 0, 0, bgTexture.width, bgTexture.height / 3)
        bg = Background(bgTextureRegion, hudCam, 0f)

        createTextRenderer()
        createLevelButtons(LEVEL_NUM)
    }

    private fun createTextRenderer() {
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

    private fun createLevelButtons(forNumLevel: Int) {
        val tex = Game.res.getTexture("hud")!!
        val baseTexRegion = TextureRegion(tex, 32, 32)

        val tmpList = arrayListOf<LevelButton>()

        // FIXME: Make it not fixed, but responsive to current viewport width
        // design for 5x5 in total of 25 matches the value set in LEVEL_NUM
        for (i in 0..4) {
            for (j in 0..2) {
                val b = LevelButton(baseTexRegion, (i+1)+(j*5), false, 64f/2f + (i * 64f), hudCam.viewportHeight - 64f - (j * 64f))
                b.setOnClickListener { level -> this.onLevelButtonClick(level) }
                tmpList.add(b)
            }
        }

        levelButtons = tmpList.toTypedArray()
    }

    private fun onLevelButtonClick(level: Int) {
        println("clicked on ${level}")

        // TODO: Relax this for general to be able to play for all levels, not fix to 1 for safety
        when (level) {
            in 1..2 -> {
                Play.sToPlayLevel = level
                // clear state of score screen
                gsm.resetPreviousActiveLevelState()
                gsm.setState(GameStateManager.PLAY)
            }
        }
    }

    override fun handleInput() {
    }

    override fun update(dt: Float) {
        handleInput()
        bg.update(dt)

        for (b in levelButtons) {
            b.update(hudCam, gsm.game.hudViewport, dt)
        }
    }

    override fun render() {
        // clear screen
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT)

        sb.begin()

        sb.projectionMatrix = hudCam.combined
        bg.render(sb)

        for (b in levelButtons) {
            b.render(textRenderer, sb)
        }

        sb.end()
    }

    override fun dispose() {

    }
}