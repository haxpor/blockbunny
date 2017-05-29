package io.wasin.blockbunny.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import io.wasin.blockbunny.Game
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

    private var b2dDebug: Boolean = true

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
    private var hud: HUD
    lateinit private var bgs: Array<Background>

    private var screenStopper: ScreenStopper

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

        // create crystals
        createCrystals()

        // set up box2d camera
        b2dCam = OrthographicCamera()
        b2dCam.setToOrtho(false, Game.V_WIDTH / B2DVars.PPM, Game.V_HEIGHT / B2DVars.PPM)
        b2dViewport = ExtendViewport(Game.V_WIDTH / B2DVars.PPM, Game.V_HEIGHT / B2DVars.PPM, b2dCam)

        // set up HUD
        hud = HUD(player)

        // backgrouds
        createBackgrounds()

        screenStopper = ScreenStopper(tileMap, cam)
        screenStopper.setOnRearchEndOfLevel {  -> this.onReachEndOfLevel() }
    }

    fun onReachEndOfLevel() {
    }

    override fun handleInput() {
        // player jump
        if (BBInput.isPressed(BBInput.BUTTON1)) {
            if (cl.playerOnGround) {
                player.body.applyForceToCenter(0f, 250f, true)
            }
        }

        // switch block color
        if (BBInput.isPressed(BBInput.BUTTON2)) {
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
            crystals.removeIf { c -> c == b.userData as Crystal }
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

        screenStopper.update(dt)

        if (cl.playerFrontCollided && !player.died) {
            Gdx.app.log("Play", "Player collided with tile at front")
            screenStopper.stop()
            player.actDie()
        }
    }

    override fun render() {
        // clear screen
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT)

        sb.begin()

        // set camera to follow player
        if (!screenStopper.isStopped) {
            cam.position.set(dummyPlayer.position.x * B2DVars.PPM + Game.V_WIDTH / 4f, Game.V_HEIGHT / 2f, 0f)
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
        fdef.filter.maskBits = B2DVars.BIT_RED or B2DVars.BIT_CRYSTAL
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
        createLayer(tileMap.layers.get("red") as TiledMapTileLayer, B2DVars.BIT_RED)
        createLayer(tileMap.layers.get("green") as TiledMapTileLayer, B2DVars.BIT_GREEN)
        createLayer(tileMap.layers.get("blue") as TiledMapTileLayer, B2DVars.BIT_BLUE)
    }

    private fun createLayer(layer: TiledMapTileLayer, bits: Short) {
        // go through all the cells in the layer
        for (row in 0..layer.height-1) {
            for (col in 0..layer.width-1) {
                // get cell
                val cell = layer.getCell(col, row)

                // check if cell exist
                if (cell == null) continue
                if (cell.tile == null) continue

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
                fdef.filter.categoryBits = bits
                fdef.filter.maskBits = B2DVars.BIT_PLAYER

                world.createBody(bdef).createFixture(fdef)
            }
        }
    }

    private fun createCrystals() {
        crystals = mutableListOf()
        val layer = tileMap.layers.get("crystals")

        val bdef = BodyDef()
        val fdef = FixtureDef()

        for (mo in layer.objects) {
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

            val c = Crystal(body)
            body.userData = c
            crystals.add(c)
        }
    }

    private fun switchBlocks() {
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
        tmpFilterData.maskBits = bits and B2DVars.BIT_CRYSTAL.inv()
        foot.filterData = tmpFilterData

        // set new mask bits to front
        tmpFilterData = front.filterData
        tmpFilterData.maskBits = bits and B2DVars.BIT_CRYSTAL.inv()
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