package io.wasin.blockbunny.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.SerializationException
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import io.wasin.blockbunny.Game
import io.wasin.blockbunny.data.LevelResult
import io.wasin.blockbunny.data.PlayerSave
import io.wasin.blockbunny.entities.B2DSprite
import io.wasin.blockbunny.handlers.*
import kotlin.experimental.or

/**
 * Created by haxpor on 5/30/17.
 */
class Mainmenu(gsm: GameStateManager): GameState(gsm) {

    private var bg: TextureRegion

    private var b2dViewport: Viewport
    private var b2dCam: OrthographicCamera
    private var b2dDebug: Boolean = false

    private var world: World
    private var b2dr: Box2DDebugRenderer

    lateinit private var topChildBlocks: Array<B2DSprite>
    lateinit private var bottomChildBlocks: Array<B2DSprite>
    private var blockTextureRegions: Array<TextureRegion>

    private val playTextureRegion: TextureRegion

    init {
        val texture = Game.res.getTexture("menu")!!
        bg = TextureRegion(texture, texture.width, texture.height)

        val hudTexture = Game.res.getTexture("hud")!!
        // set all texture regions used to draw individual blocks
        blockTextureRegions = arrayOf(
                TextureRegion(hudTexture, 58, 34, 5, 5),
                TextureRegion(hudTexture, 58 + 5, 34, 5, 5),
                TextureRegion(hudTexture, 58 + 5*2, 34, 5, 5)
        )

        playTextureRegion = TextureRegion(hudTexture, 0, 34, 58, 28)

        // set up box2d related stuff
        world = World(Vector2(0f, -1.0f), true)
        b2dr = Box2DDebugRenderer()
        b2dCam = OrthographicCamera()
        b2dCam.setToOrtho(false, Game.V_WIDTH / B2DVars.PPM, Game.V_HEIGHT / B2DVars.PPM)
        b2dViewport = FitViewport(Game.V_WIDTH / B2DVars.PPM, Game.V_HEIGHT / B2DVars.PPM, b2dCam)

        createPhysicsTextBlocks()

        // read player's savefile
        // this will read it into cache, thus it will be maintained and used throughout the life
        // cycle of the game
        try {
            Gdx.app.log("Mainmenu", "read save file")
            game.playerSaveFileManager.readSaveFile()
        }
        catch(e: GameRuntimeException) {
            if (e.code == GameRuntimeException.SAVE_FILE_NOT_FOUND ||
                    e.code == GameRuntimeException.SAVE_FILE_EMPTY_CONTENT) {
                // write a new fresh save file to resolve the issue
                Gdx.app.log("Mainmenu", "write a fresh save file")
                game.playerSaveFileManager.writeFreshSaveFile(Settings.TOTAL_LEVELS)
            }
        }
        catch(e: SerializationException) {
            Gdx.app.log("Mainmenu", "save file is corrupted, rewrite a fresh one : ${e.message}")

            game.playerSaveFileManager.writeFreshSaveFile(Settings.TOTAL_LEVELS)
        }
    }

    override fun handleInput() {
        if (BBInput.isPressed(BBInput.BUTTON1) || BBInput.isPressed(BBInput.BUTTON2) ||
                BBInput.isMouseDown(BBInput.MOUSE_BUTTON_LEFT) || BBInput.isDown() ||
                BBInput.isControllerPressed(BBInput.CONTROLLER_BUTTON_2)) {
            // go back to level selection
            gsm.setState(GameStateManager.LEVEL_SELECTION)
        }
    }

    override fun update(dt: Float) {
        handleInput()
        world.step(dt, 6, 2)
    }

    override fun render() {
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT)

        sb.begin()
        sb.projectionMatrix = hudCam.combined
        sb.draw(bg, 0f, 0f)

        // draw top child blocks
        for (b in topChildBlocks) {
            b.render(sb)
        }
        for (b in bottomChildBlocks) {
            b.render(sb)
        }

        sb.draw(playTextureRegion, Game.V_WIDTH / 2f - playTextureRegion.regionWidth/2f, playTextureRegion.regionHeight/2f + 50f)

        sb.end()

        // draw box2d world
        if (b2dDebug) {
            b2dr.render(world, b2dCam.combined)
        }
    }

    override fun dispose() {

    }

    override fun resize_user(width: Int, height: Int) {

    }

    private fun createPhysicsTextBlocks() {
        val tileSize = 5f
        val offsetY = 20f    // offset in y-position to initially place each block before physics kicks in

        // create top platform block
        _createB2DStaticBlock(B2DVars.BIT_TOP_PLATFORM, B2DVars.BIT_LINE1_BLOCK, Vector2(Game.V_WIDTH/2f, 175f), 250f, 1f)
        // create bottom platform block
        _createB2DStaticBlock(B2DVars.BIT_BOTTOM_PLATFORM, B2DVars.BIT_LINE2_BLOCK, Vector2(Game.V_WIDTH/2f, 110f), 250f, 1f)

        // definition of text as arrays 1,0
        // size is 40x8
        // line1 represents "B L O C K"
        val line1Blocks = arrayOf(
                arrayOf(0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0),
                arrayOf(0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0),
                arrayOf(0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0),
                arrayOf(0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0),
                arrayOf(0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0),
                arrayOf(0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0),
                arrayOf(0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0),
                arrayOf(0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0)
        )
        topChildBlocks = _createChildBlocks(line1Blocks, 1, BodyDef.BodyType.DynamicBody, B2DVars.BIT_LINE1_BLOCK, B2DVars.BIT_TOP_PLATFORM or B2DVars.BIT_LINE1_BLOCK,
                { i -> (Game.V_WIDTH/2f - line1Blocks[0].count()*tileSize/2f + i*tileSize) / B2DVars.PPM },
                { rowIndex -> (175f+offsetY + (line1Blocks.count()-1 - rowIndex)*tileSize + tileSize*2) / B2DVars.PPM })

        val line2Blocks = arrayOf(
                arrayOf(0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0),
                arrayOf(0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0),
                arrayOf(0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0),
                arrayOf(0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0),
                arrayOf(0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0),
                arrayOf(0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0),
                arrayOf(0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0),
                arrayOf(0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0)
        )
        bottomChildBlocks = _createChildBlocks(line2Blocks, 1, BodyDef.BodyType.DynamicBody, B2DVars.BIT_LINE2_BLOCK, B2DVars.BIT_BOTTOM_PLATFORM or B2DVars.BIT_LINE2_BLOCK,
                { i -> (Game.V_WIDTH/2f - line2Blocks[0].count()*tileSize/2f + i*tileSize) / B2DVars.PPM },
                { rowIndex -> (110f+offsetY + (line2Blocks.count()-1 - rowIndex)*tileSize + tileSize*2) / B2DVars.PPM })
    }

    private fun _createB2DStaticBlock(categoryBits: Short, maskBits: Short, position: Vector2, width: Float, height: Float) {
        // used for each physics element to fall down on it
        val bdef = BodyDef()
        bdef.position.set(position.x/B2DVars.PPM, position.y/B2DVars.PPM)
        bdef.type = BodyDef.BodyType.StaticBody

        val shape = PolygonShape()
        val fdef = FixtureDef()

        val body = world.createBody(bdef)
        shape.setAsBox(width/2f/B2DVars.PPM, height/2f/B2DVars.PPM)
        fdef.shape = shape
        fdef.friction = 0.0f
        fdef.filter.categoryBits = categoryBits
        fdef.filter.maskBits = maskBits
        body.createFixture(fdef)
    }

    private fun _createBlocksAgainstTiles(rowTiles: Array<Int>, targetValue: Int, bodyType: BodyDef.BodyType, categoryBits: Short, maskBits: Short, textureRegion: TextureRegion,
                                             position: ((Int) -> Vector2)): Array<B2DSprite> {
        val tileSize = 5f

        val tmpList = ArrayList<B2DSprite>()

        // top platform
        for (i in 0..rowTiles.count()-1) {

            // create a top platform
            // used for each physics element to fall down on it
            val bdef = BodyDef()
            bdef.position.set(position.invoke(i))
            bdef.type = bodyType
            bdef.fixedRotation = true

            val shape = PolygonShape()
            val fdef = FixtureDef()

            val body = world.createBody(bdef)
            shape.setAsBox(tileSize/2f/B2DVars.PPM, tileSize/2f/B2DVars.PPM)
            fdef.shape = shape
            fdef.filter.categoryBits = categoryBits
            fdef.filter.maskBits = maskBits
            body.createFixture(fdef)

            // create b2dsprite
            if (rowTiles[i] == targetValue) {
                val b2dsprite = B2DSprite(body)
                b2dsprite.setAnimation(arrayOf(textureRegion), 1 / 12f)
                tmpList.add(b2dsprite)
            }
        }
        return tmpList.toTypedArray()
    }

    private fun _createChildBlocks(blockTiles: Array<Array<Int>>, targetValue: Int, bodyType: BodyDef.BodyType, categoryBits: Short, maskBits: Short,
                                  positionX: ((i:Int) -> Float), positionY: ((rowIndex:Int) -> Float)): Array<B2DSprite> {

        var tmpRowList: Array<B2DSprite>
        val mutableList = ArrayList<B2DSprite>()

        for (row in 0..blockTiles.count()-1) {
            tmpRowList = _createBlocksAgainstTiles(blockTiles[row], targetValue, bodyType, categoryBits, maskBits,
                    blockTextureRegions[MathUtils.random(blockTextureRegions.count()-1)],
                    { i -> Vector2(positionX.invoke(i), positionY.invoke(row)) })
            mutableList.addAll(tmpRowList)
        }
        // set result to top child blocks
        return mutableList.toTypedArray()
    }
}