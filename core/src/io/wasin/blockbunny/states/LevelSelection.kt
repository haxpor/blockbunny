package io.wasin.blockbunny.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import io.wasin.blockbunny.Game
import io.wasin.blockbunny.handlers.*

/**
 * Created by haxpor on 5/22/17.
 */
class LevelSelection(gsm: GameStateManager): GameState(gsm) {

    companion object {
        const private val LEVEL_PER_PAGE: Int = 15
        const private val ROW_PER_PAGE: Int = 3
        const private val COLUMN_PER_PAGE: Int = 5
        private var sPreviousActiveSelectionIndex: Int = 0
    }

    private var bg: Background
    lateinit private var textRenderer: TextRenderer
    lateinit private var levelButtons: Array<LevelButton>
    private var activeSelectionTextureRegion: TextureRegion
    private var activeSelectionIndex: Int = sPreviousActiveSelectionIndex
    private var activePage: Int = 1

    init {
        var bgTexture = Game.res.getTexture("bgs")!!
        var bgTextureRegion = TextureRegion(bgTexture, 0, 0, bgTexture.width, bgTexture.height / 3)
        bg = Background(bgTextureRegion, hudCam, 0f)

        // this applies for keyboard, and controller
        var tex = Game.res.getTexture("misc")!!
        activeSelectionTextureRegion = TextureRegion(tex, 0, 0, tex.width, tex.height)

        createTextRenderer()
        createLevelButtons(Settings.TOTAL_LEVELS)
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
        // at this point, our cached data should be present and there should be no problem
        val syncedPlayerSave = game.playerSaveFileManager.cache.data!!

        // design for 5x3 in total of 15 matches the value set in Settings.TOTAL_LEVELS
        for (j in 0..ROW_PER_PAGE-1) {
            for (i in (activePage-1)* LEVEL_PER_PAGE..(activePage-1) * LEVEL_PER_PAGE + COLUMN_PER_PAGE-1) {
                val levelResult = syncedPlayerSave.levelResults[j* COLUMN_PER_PAGE + i]
                val b = LevelButton(baseTexRegion, (i + 1) + (j * COLUMN_PER_PAGE), levelResult.clear, 64f/2f + (i * 64f), hudCam.viewportHeight - 64f - (j * 64f))
                b.setOnClickListener { level -> run {
                        // as touch position is sudden so we manually set it and save for later
                        activeSelectionIndex = b.levelNumber - 1
                        sPreviousActiveSelectionIndex = activeSelectionIndex

                        Game.res.getSound("levelselect")!!.play()
                        this.onLevelButtonClick(level)
                    }
                }
                tmpList.add(b)
            }
        }

        levelButtons = tmpList.toTypedArray()
    }

    private fun onLevelButtonClick(level: Int) {
        println("clicked on ${level}")

        // TODO: Relax this for general to be able to play for all levels, not fix to 1 for safety
        when (level) {
            in 1..3 -> {
                Play.sToPlayLevel = level
                // clear state of score screen
                gsm.resetPreviousActiveLevelState()
                gsm.setState(GameStateManager.PLAY)
            }
        }
    }

    override fun handleInput() {
        if (BBInput.isPressed(BBInput.BUTTON_LEFT) ||
                BBInput.isControllerPressed(BBInput.CONTROLLER_BUTTON_LEFT)) {
            activeSelectionIndex = moveActiveSelectionLeft(activeSelectionIndex)
        }
        if (BBInput.isPressed(BBInput.BUTTON_RIGHT) ||
                BBInput.isControllerPressed(BBInput.CONTROLLER_BUTTON_RIGHT)) {
            activeSelectionIndex = moveActiveSelectionRight(activeSelectionIndex)
        }
        if (BBInput.isPressed(BBInput.BUTTON_UP) ||
                BBInput.isControllerPressed(BBInput.CONTROLLER_BUTTON_UP)) {
            activeSelectionIndex = moveActiveSelectionUp(activeSelectionIndex)
        }
        if (BBInput.isPressed(BBInput.BUTTON_DOWN) ||
                BBInput.isControllerPressed(BBInput.CONTROLLER_BUTTON_DOWN)) {
            activeSelectionIndex = moveActiveSelectionDown(activeSelectionIndex)
        }

        if (BBInput.isPressed(BBInput.BUTTON1) ||
                BBInput.isControllerPressed(BBInput.CONTROLLER_BUTTON_2)) {
            // save active selection index
            sPreviousActiveSelectionIndex = activeSelectionIndex

            // programmatically select a level button
            Game.res.getSound("levelselect")!!.play()
            this.onLevelButtonClick(activeSelectionIndex+1)
        }
    }

    /**
     * Move activeIndex to the left and wrap around to the right of its row if necessary.
     * @return New moved left index
     */
    private fun moveActiveSelectionLeft(activeIndex: Int): Int {
        val leftMostIndexes = ArrayList<Int>()

        val startingIndex: Int = (activePage-1)* LEVEL_PER_PAGE
        leftMostIndexes.add(startingIndex)

        for (i in 1..ROW_PER_PAGE-1) {
            leftMostIndexes.add(startingIndex + i * COLUMN_PER_PAGE)
        }

        val chkIndex = leftMostIndexes.indexOf(activeIndex)
        if (chkIndex != -1) {
            return leftMostIndexes[chkIndex] + COLUMN_PER_PAGE - 1
        }
        else {
            return activeIndex - 1
        }
    }

    private fun moveActiveSelectionRight(activeIndex: Int): Int {
        val rightMostIndexes = ArrayList<Int>()

        val startingIndex: Int = (activePage-1) * LEVEL_PER_PAGE + COLUMN_PER_PAGE - 1
        rightMostIndexes.add(startingIndex)

        for (i in 1..ROW_PER_PAGE-1) {
            rightMostIndexes.add(startingIndex + i * COLUMN_PER_PAGE)
        }

        val chkIndex = rightMostIndexes.indexOf(activeIndex)
        if (chkIndex != -1) {
            return rightMostIndexes[chkIndex] - COLUMN_PER_PAGE + 1
        }
        else {
            return activeIndex + 1
        }
    }

    private fun moveActiveSelectionUp(activeIndex: Int): Int {
        val topMostIndexes = ArrayList<Int>()

        val startingIndex: Int = (activePage-1) * LEVEL_PER_PAGE
        topMostIndexes.add(startingIndex)

        for (i in 1..COLUMN_PER_PAGE-1) {
            topMostIndexes.add(startingIndex + i)
        }

        val chkIndex = topMostIndexes.indexOf(activeIndex)
        if (chkIndex != -1) {
            return topMostIndexes[chkIndex] + (ROW_PER_PAGE-1)* COLUMN_PER_PAGE
        }
        else {
            return activeIndex - COLUMN_PER_PAGE
        }
    }

    private fun moveActiveSelectionDown(activeIndex: Int): Int {
        val bottomMostIndexes = ArrayList<Int>()

        val startingIndex: Int = (activePage-1) * LEVEL_PER_PAGE + (ROW_PER_PAGE-1)* COLUMN_PER_PAGE
        bottomMostIndexes.add(startingIndex)

        for (i in 1..COLUMN_PER_PAGE-1) {
            bottomMostIndexes.add(startingIndex + i)
        }

        val chkIndex = bottomMostIndexes.indexOf(activeIndex)
        if (chkIndex != -1) {
            return bottomMostIndexes[chkIndex] - (ROW_PER_PAGE-1)* COLUMN_PER_PAGE
        }
        else {
            return activeIndex + COLUMN_PER_PAGE
        }
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

        renderActiveSelection(sb)

        sb.end()
    }

    private fun renderActiveSelection(sb: SpriteBatch) {
        val activeLevelButton = levelButtons[activeSelectionIndex]
        sb.draw(activeSelectionTextureRegion, activeLevelButton.position.x - activeSelectionTextureRegion.regionWidth/2f, activeLevelButton.position.y - activeSelectionTextureRegion.regionHeight/2f)
    }

    override fun dispose() {

    }
}