package io.wasin.blockbunny.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import io.wasin.blockbunny.Game
import io.wasin.blockbunny.data.LevelResult
import io.wasin.blockbunny.entities.Bomb
import io.wasin.blockbunny.entities.Crystal
import io.wasin.blockbunny.entities.HUD
import io.wasin.blockbunny.entities.Player
import io.wasin.blockbunny.handlers.*
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

/**
 * Created by haxpor on 5/16/17.
 */
class Play(gsm: GameStateManager) : GameState(gsm) {

    companion object {
        // level number to be set before entering Play game state by LevelSelection
        // default to 1st level
        var sToPlayLevel: Int = 1
    }

    private var b2dDebug: Boolean = false

    var b2dViewport: Viewport
    var b2dCam: OrthographicCamera
        private set

    private var world: World
    private var b2dr: Box2DDebugRenderer
    private var cl: MyContactListener

    lateinit private var tileMap: TiledMap
    private var tileSize: Float = 0f
    lateinit private var tmr: OrthogonalTiledMapRenderer

    lateinit private var player: Player
    lateinit private var dummyPlayer: Player
    lateinit private var crystals: MutableList<Crystal>
    lateinit private var bombs: MutableList<Bomb>
    private var hud: HUD
    lateinit private var bgs: Array<Background>

    private var screenStopper: ScreenStopper
    private var isWentToScoreScreen: Boolean = false

    init {
        world = World(Vector2(0f, -9.81f), true)

        // set up box2d stuff
        cl = MyContactListener()
        world.setContactListener(cl)
        b2dr = Box2DDebugRenderer()

        // create player
        createPlayer()
        createDummyPlayer()

        // create tiles
        createTiles()

        // create objects in game
        createObjects()

        // set up box2d camera
        b2dCam = OrthographicCamera()
        b2dCam.setToOrtho(false, cam.viewportWidth / B2DVars.PPM, cam.viewportHeight / B2DVars.PPM)
        b2dViewport = ExtendViewport(cam.viewportWidth / B2DVars.PPM, cam.viewportHeight / B2DVars.PPM, b2dCam)

        // set up HUD
        hud = HUD(player)

        // backgrounds
        createBackgrounds()

        screenStopper = ScreenStopper(tileMap, cam)
        screenStopper.setOnRearchEndOfLevel { -> this.onReachEndOfLevel() }

        // set total of crystals in the map to player
        player.setTotalCrystals(getTotalObjetCountOfCrystals())
    }

    override fun setupViewport(cam: OrthographicCamera, hudCam: OrthographicCamera, viewportWidth: Float, viewportHeight: Float) {
        camViewport = ExtendViewport(viewportWidth, viewportHeight, cam)
        hudViewport = ExtendViewport(viewportWidth, viewportHeight, hudCam)
    }

    override fun resize_user(width: Int, height: Int) {
        b2dViewport.update((width / B2DVars.PPM).toInt(), (height / B2DVars.PPM).toInt())
    }

    fun onReachEndOfLevel() {
    }

    /**
     * Be careful, it's expensive call.
     * Cache the result for better performance.
     */
    fun getTotalObjetCountOfCrystals(): Int {
        val layer = tileMap.layers.get("objects")
        var count = 0

        for (obj in layer.objects) {
            if (obj.properties.get("type") == "crystal") {
                count++
            }
        }

        return count
    }

    /**
     * Be careful, it's expensive call.
     * Cache the result for better performance.
     */
    fun getTotalObjectCountOfBomb(): Int {
        val layer = tileMap.layers.get("objects")
        var count = 0

        for (obj in layer.objects) {
            if (obj.properties.get("type") == "bomb") {
                count++
            }
        }

        return count
    }

    override fun handleInput() {
        // ** Keyboard & Mouse **
        // convert screen coordinate to world coordinate in context of hud-camera
        val screenCoor = Vector3(BBInput.screenX.toFloat(), BBInput.screenY.toFloat(), 0f)
        val worldCoor = hudCam.unproject(screenCoor, hudViewport.screenX.toFloat(), hudViewport.screenY.toFloat(),
                hudViewport.screenWidth.toFloat(), hudViewport.screenHeight.toFloat())

        // player jump if BBInput.BUTTON1 is pressed, or click on left half of the screen
        if (BBInput.isPressed(BBInput.BUTTON1) ||
                ((worldCoor.x <= hudCam.viewportWidth/2f) && BBInput.isMousePressed(BBInput.MOUSE_BUTTON_LEFT)) ||
                BBInput.isControllerPressed(BBInput.CONTROLLER_BUTTON_2)) {
            if (cl.playerOnGround) {
                Game.res.getSound("jump")!!.play()
                player.body.applyForceToCenter(0f, 250f, true)
            }
        }

        // switch block color if BBInput.BUTTON2 is pressed, or click on right half of the screen
        if (BBInput.isPressed(BBInput.BUTTON2) ||
                (worldCoor.x > hudCam.viewportWidth/2f && BBInput.isMousePressed(BBInput.MOUSE_BUTTON_LEFT)) ||
                BBInput.isControllerPressed(BBInput.CONTROLLER_BUTTON_1)) {
            switchBlocks()
        }
    }

    override fun update(dt: Float) {
        handleInput()
        world.step(dt, 6, 2)

        // update backgrounds
        for (b in bgs) {
            b.update(dt)
        }

        // remove crystals
        var bodies = cl.bodiesToRemove
        for (b in bodies) {
            // this manual loop section fixed this issue https://github.com/multi-os-engine/multi-os-engine/issues/114
            for (c in crystals) {
                if (c == b.userData as Crystal) {
                    crystals.remove(c)
                    break
                }
            }
            world.destroyBody(b)
            player.collectCrystal()
        }
        bodies.clear()

        player.update(dt)

        if (!screenStopper.isStopped) {
            dummyPlayer.update(dt)
        }

        for (c in crystals) {
            c.update(dt)
        }
        for (b in bombs) {
            b.update(dt)
        }

        screenStopper.update(dt)

        // check to act die for player (front collided with tile)
        if (cl.playerFrontCollided && !player.died) {
            Gdx.app.log("Play", "Player collided with tile at front")

            val hit = Game.res.getSound("hit")!!
            val hitId = hit.play()
            hit.setVolume(hitId, 2.0f)

            screenStopper.stop()
            player.actDie()
        }
        // check to act die for player (bomb)
        if (cl.playerCollidedWithBomb && !player.died) {
            Gdx.app.log("Play", "Player collided with bomb")

            val hit = Game.res.getSound("hit")!!
            val hitId = hit.play()
            hit.setVolume(hitId, 2.0f)

            screenStopper.stop()
            player.actDie()
        }

        // if player is outside of the screen then go to SCORE screen
        // GAME OVER state
        if (!isWentToScoreScreen && player.position.y * B2DVars.PPM + player.height/2 < 0f) {
            Gdx.app.log("Play", "Player died and is outside of the screen")

            // no need to update save file

            // go to Score screen
            gsm.pushState(GameStateManager.SCORE)
            isWentToScoreScreen = true
        }
        // if player is outside of the total width of tilemap, then player clears the level
        // WIN state
        else if (!isWentToScoreScreen && player.position.x * B2DVars.PPM - player.width/2 > screenStopper.width) {
            Gdx.app.log("Play", "Player clears the level")

            // update to player save file if level isn't cleared yet OR
            // update to player save file if level is cleared and its high score is beated
            val levelResult: LevelResult? = game.playerSaveFileManager.getLevelResult(sToPlayLevel)
            if ((levelResult != null && !levelResult.clear) ||
                (levelResult != null && levelResult.clear && levelResult.collectedCrystal < player.getNumCrystals())) {
                game.playerSaveFileManager.updateLevelResult(sToPlayLevel, LevelResult(true, player.getNumCrystals()), true)
            }

            // go to Score screen
            gsm.setCurrentActiveLevelAsClear(player.getNumCrystals(), player.getTotalCrystals())
            gsm.pushState(GameStateManager.SCORE)
            isWentToScoreScreen = true
        }
    }

    override fun render() {
        // clear screen
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT)

        sb.begin()

        // set camera to follow player
        if (!screenStopper.isStopped) {
            cam.position.set(dummyPlayer.position.x * B2DVars.PPM + cam.viewportWidth / 4f, cam.viewportHeight / 2f, 0f)
            cam.update()
        }

        // draw bgs
        sb.projectionMatrix = hudCam.combined
        for (b in bgs) {
            b.render(sb)
        }
        sb.end()

        // draw tile map
        tmr.setView(cam)
        tmr.render()

        
        sb.begin()
        // draw player
        sb.projectionMatrix = cam.combined
        player.render(sb)

        // draw crystals
        for (c in crystals) {
            c.render(sb)
        }
        for (b in bombs) {
            b.render(sb)
        }

        // draw hud
        sb.projectionMatrix = hudCam.combined
        hud.render(sb)
        sb.end()

        // draw box2d world
        if (b2dDebug) {
            b2dCam.position.set(cam.position.x / B2DVars.PPM, cam.position.y / B2DVars.PPM, 0f)
            b2dCam.update()
            b2dr.render(world, b2dCam.combined)
        }
    }

    override fun dispose() {
    }

    private fun createPlayer() {
        val bdef = BodyDef()

        bdef.position.set(100f / B2DVars.PPM, 200f / B2DVars.PPM)
        bdef.type = BodyDef.BodyType.DynamicBody
        bdef.linearVelocity.set(1f, 0f)

        // shape, and fdef
        var shape = PolygonShape()
        var fdef = FixtureDef()

        val body: Body = world.createBody(bdef)
        shape.setAsBox(13f / B2DVars.PPM, 13f / B2DVars.PPM)
        fdef.shape = shape
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER
        fdef.filter.maskBits = B2DVars.BIT_RED or B2DVars.BIT_CRYSTAL or B2DVars.BIT_BOMB
        body.createFixture(fdef).userData = "player"

        // reuse
        shape = PolygonShape()
        fdef = FixtureDef()

        // create foot sensor
        shape.setAsBox(13 / B2DVars.PPM, 2 / B2DVars.PPM, Vector2(0f, -13 / B2DVars.PPM), 0f)
        fdef.shape = shape
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER
        fdef.filter.maskBits = B2DVars.BIT_RED
        fdef.isSensor = true
        body.createFixture(fdef).userData = "foot"

        // reuse
        shape = PolygonShape()
        fdef = FixtureDef()

        // create front sensor
        shape.setAsBox(2 / B2DVars.PPM, 6 / B2DVars.PPM, Vector2(13 / B2DVars.PPM, 0f), 0f)
        fdef.shape = shape
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER
        fdef.filter.maskBits = B2DVars.BIT_RED
        fdef.isSensor = true
        body.createFixture(fdef).userData = "front"

        // create player
        player = Player(body)
        // circular reference from body->player, and player->body
        body.userData = player
    }

    private fun createDummyPlayer() {
        // dummy player will be used to update camera's position
        val bdef = BodyDef()

        bdef.position.set(100f / B2DVars.PPM, 200f / B2DVars.PPM)
        bdef.type = BodyDef.BodyType.KinematicBody  // it's kinematic type as we don't want it to be affected by physics simulation
        bdef.linearVelocity.set(1f, 0f) // important, it needs to have the same linear velocity as of player

        // shape, and fdef
        var shape = PolygonShape()
        var fdef = FixtureDef()

        val body: Body = world.createBody(bdef)
        shape.setAsBox(13f / B2DVars.PPM, 13f / B2DVars.PPM)
        fdef.shape = shape
        fdef.isSensor = true
        body.createFixture(fdef)

        // create dummy player
        dummyPlayer = Player(body)
    }

    private fun createTiles() {
        // load tile map from selected level
        tileMap = TmxMapLoader().load("maps/level${sToPlayLevel}.tmx")
        tmr = OrthogonalTiledMapRenderer(tileMap)

        tileSize = tileMap.properties.get("tilewidth", Float::class.java)

        // note: this section is not optimized, the code loops 3 times for each layer unneccesary
        // we can do better by adding custom property to Tile via color name and check it in 1 single loop
        createB2DTileFromLayerWithMappings(tileMap.layers.get("tiles") as TiledMapTileLayer, hashMapOf(1 to B2DVars.BIT_RED, 2 to B2DVars.BIT_GREEN, 3 to B2DVars.BIT_BLUE))
    }

    private fun createB2DTileFromLayerWithMappings(layer: TiledMapTileLayer, idMaps: HashMap<Int, Short>) {
        // go through all the cells in the layer
        for (row in 0..layer.height-1) {
            for (col in 0..layer.width-1) {
                // get cell
                val cell = layer.getCell(col, row)

                // check if tile exists
                if (cell == null) continue
                if (cell.tile == null) continue
                if (!idMaps.containsKey(cell.tile.id)) continue

                // creae a body fixture from cell
                val bdef = BodyDef()
                bdef.type = BodyDef.BodyType.StaticBody
                bdef.position.set(
                        (col + 0.5f) * tileSize / B2DVars.PPM,
                        (row + 0.5f) * tileSize / B2DVars.PPM
                )

                val cs = ChainShape()
                val v = Array<Vector2>(3, { i -> Vector2.Zero })
                v[0] = Vector2(-tileSize / 2 / B2DVars.PPM, -tileSize / 2 / B2DVars.PPM)
                v[1] = Vector2(-tileSize / 2 / B2DVars.PPM, tileSize / 2 / B2DVars.PPM)
                v[2] = Vector2(tileSize / 2 / B2DVars.PPM, tileSize / 2 / B2DVars.PPM)
                cs.createChain(v)

                val fdef = FixtureDef()
                fdef.shape = cs
                fdef.friction = 0f
                fdef.filter.categoryBits = idMaps.getValue(cell.tile.id)
                fdef.filter.maskBits = B2DVars.BIT_PLAYER

                world.createBody(bdef).createFixture(fdef)
            }
        }
    }

    private fun createObjects() {
        // create mutable list for all type of objects
        crystals = mutableListOf()
        bombs = mutableListOf()

        // all types of object are inside "objects" layer
        val layer = tileMap.layers.get("objects")

        val bdef = BodyDef()
        val fdef = FixtureDef()

        for (mo in layer.objects) {

            val type = mo.properties.get("type")

            when(type) {
                "crystal" -> {
                    bdef.type = BodyDef.BodyType.StaticBody
                    val x = mo.properties.get("x", Float::class.java) / B2DVars.PPM
                    val y = mo.properties.get("y", Float::class.java) / B2DVars.PPM
                    bdef.position.set(x, y)

                    val cshape = CircleShape()
                    cshape.radius = 8f / B2DVars.PPM

                    fdef.shape = cshape
                    fdef.isSensor = true
                    fdef.filter.categoryBits = B2DVars.BIT_CRYSTAL
                    fdef.filter.maskBits = B2DVars.BIT_PLAYER

                    val body = world.createBody(bdef)
                    body.createFixture(fdef).userData = "crystal"

                    val type = mo.properties.get("type")

                    val c = Crystal(body)
                    body.userData = c
                    crystals.add(c)
                }

                "bomb" -> {
                    bdef.type = BodyDef.BodyType.StaticBody
                    val x = mo.properties.get("x", Float::class.java) / B2DVars.PPM
                    val y = mo.properties.get("y", Float::class.java) / B2DVars.PPM
                    bdef.position.set(x, y)

                    val cshape = CircleShape()
                    cshape.radius = 13f / B2DVars.PPM   // no the exact same size of texture region

                    fdef.shape = cshape
                    fdef.isSensor = true
                    fdef.filter.categoryBits = B2DVars.BIT_BOMB
                    fdef.filter.maskBits = B2DVars.BIT_PLAYER

                    val body = world.createBody(bdef)
                    body.createFixture(fdef).userData = "bomb"

                    val c = Bomb(body)
                    body.userData = c
                    bombs.add(c)
                }
            }
        }
    }

    private fun switchBlocks() {
        Game.res.getSound("changeblock")!!.play()

        val body = player.body.fixtureList.first()
        val foot = player.body.fixtureList[1]
        val front = player.body.fixtureList[2]

        // get current bits set on player's body
        var bits = body.filterData.maskBits
        // temp filter data to hold data and set back to each fixture
        var tmpFilterData: Filter

        // switch to next color
        // red -> green -> blue -> red
        if ((bits and B2DVars.BIT_RED) != 0.toShort()) {
            bits = bits and B2DVars.BIT_RED.inv()
            bits = bits or B2DVars.BIT_GREEN
        }
        else if ((bits and B2DVars.BIT_GREEN) != 0.toShort()) {
            bits = bits and B2DVars.BIT_GREEN.inv()
            bits = bits or B2DVars.BIT_BLUE
        }
        else if ((bits and B2DVars.BIT_BLUE) != 0.toShort()) {
            bits = bits and B2DVars.BIT_BLUE.inv()
            bits = bits or B2DVars.BIT_RED
        }

        // set new mask bits to body
        tmpFilterData = body.filterData
        tmpFilterData.maskBits = bits
        body.filterData = tmpFilterData

        // set new mask bits to foot
        tmpFilterData = foot.filterData
        tmpFilterData.maskBits = bits and B2DVars.BIT_CRYSTAL.inv() and B2DVars.BIT_BOMB.inv()
        foot.filterData = tmpFilterData

        // set new mask bits to front
        tmpFilterData = front.filterData
        tmpFilterData.maskBits = bits and B2DVars.BIT_CRYSTAL.inv() and B2DVars.BIT_BOMB.inv()
        front.filterData = tmpFilterData
    }

    private fun createBackgrounds() {
        val texture = Game.res.getTexture("bgs")!!
        val textureRegions = TextureRegion.split(texture, texture.width, texture.height / 3)

        val sky = Background(textureRegions[0][0], hudCam, 0.0f)
        val cloud = Background(textureRegions[1][0], hudCam, 1.0f)
        val rocks = Background(textureRegions[2][0], hudCam, 8.0f)

        bgs = arrayOf(sky, cloud, rocks)
    }
}