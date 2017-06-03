package io.wasin.blockbunny

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import io.wasin.blockbunny.handlers.*

class Game : ApplicationAdapter() {

    lateinit var sb: SpriteBatch
        private set
    lateinit var camViewport: Viewport
        private set
    lateinit var hudViewport: Viewport
        private set
    lateinit var cam: OrthographicCamera
        private set
    lateinit var hudCam: OrthographicCamera
        private set
    lateinit var gsm: GameStateManager
        private set
    lateinit var playerSaveFileManager: PlayerSaveFileManager
        private set

    companion object {
        const val TITLE = "Block Bunny"
        const val V_WIDTH = 320f
        const val V_HEIGHT = 240f
        const val SCALE = 2

        var res: Content = Content()
            private set
    }

    override fun create() {

        Gdx.input.inputProcessor = BBInputProcessor()

        sb = SpriteBatch()

        // set up cam
        cam = OrthographicCamera()
        cam.setToOrtho(false, V_WIDTH, V_HEIGHT)

        // set up hud-cam
        hudCam = OrthographicCamera()
        hudCam.setToOrtho(false, V_WIDTH, V_HEIGHT)

        gsm = GameStateManager(this)

        camViewport = FitViewport(Game.V_WIDTH, Game.V_HEIGHT, cam)
        hudViewport = FitViewport(Game.V_WIDTH, Game.V_HEIGHT, hudCam)

        // create player's savefile manager with pre-set of savefile's path
        playerSaveFileManager = PlayerSaveFileManager(Settings.PLAYER_SAVEFILE_RELATIVE_PATH)

        res.loadTexture("images/bunny.png", "bunny")
        res.loadTexture("images/crystal.png", "crystal")
        res.loadTexture("images/hud.png", "hud")
        res.loadTexture("images/bgs.png", "bgs")
        res.loadTexture("images/menu.png", "menu")
        res.loadTexture("images/spikes.png", "spikes")

        res.loadMusic("music/bbsong.mp3", "bbsong")
        res.loadSound("sfx/changeblock.wav", "changeblock")
        res.loadSound("sfx/crystal.wav", "crystal")
        res.loadSound("sfx/hit.wav", "hit")
        res.loadSound("sfx/jump.wav", "jump")
        res.loadSound("sfx/levelselect.wav", "levelselect")

        // set to play background music endlessly now
        val bgMusic = res.getMusic("bbsong")!!
        bgMusic.play()
        bgMusic.isLooping = true

        // only this time to check for controller
        // if user plug in controller after this then they have to restart the game
        setupFirstActiveController()

        // set to begin with Play state
        gsm.pushState(GameStateManager.MAINMENU)
    }

    private fun setupFirstActiveController() {
        if (Controllers.getControllers().count() > 0) {
            val bbInputProcessor = Gdx.input.inputProcessor as BBInputProcessor
            val controller = Controllers.getControllers().first()
            controller.addListener(bbInputProcessor)
            bbInputProcessor.setActiveController(controller)
        }
    }

    override fun render() {
        Gdx.graphics.setTitle(TITLE + " -- FPS: " + Gdx.graphics.framesPerSecond)
        gsm.update(Gdx.graphics.deltaTime)
        gsm.render()
        BBInput.update()
    }

    override fun dispose() {
    }

    override fun resize(width: Int, height: Int) {
        camViewport.update(width, height)
        hudViewport.update(width, height, true)
    }
}
