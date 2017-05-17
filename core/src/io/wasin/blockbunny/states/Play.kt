package io.wasin.blockbunny.states

import io.wasin.blockbunny.handlers.B2DVars
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import io.wasin.blockbunny.Game
import io.wasin.blockbunny.entities.Crystal
import io.wasin.blockbunny.entities.Player
import io.wasin.blockbunny.handlers.GameStateManager
import io.wasin.blockbunny.handlers.MyContactListener
import io.wasin.blockbunny.handlers.MyInput

/**
 * Created by haxpor on 5/16/17.
 */
class Play(gsm: GameStateManager) : GameState(gsm) {

    private var debug: Boolean = false

    private var world: World
    private var b2dr: Box2DDebugRenderer
    private var b2dCam: OrthographicCamera
    private var cl: MyContactListener

    lateinit private var tileMap: TiledMap
    private var tileSize: Float = 0f
    lateinit private var tmr: OrthogonalTiledMapRenderer

    lateinit private var player: Player
    lateinit private var crystals: MutableList<Crystal>

    init {
        world = World(Vector2(0f, -9.81f), true)

        // set up box2d stuff
        cl = MyContactListener()
        world.setContactListener(cl)
        b2dr = Box2DDebugRenderer()

        // create player
        createPlayer()

        // create tiles
        createTiles()

        // create crystals
        createCrystals()

        // set box2d cam
        b2dCam = OrthographicCamera()
        b2dCam.setToOrtho(false, Game.V_WIDTH / B2DVars.PPM, Game.V_HEIGHT / B2DVars.PPM)
    }

    override fun handleInput() {
        // player jump
        if (MyInput.isPressed(MyInput.BUTTON1)) {
            if (cl.playerOnGround) {
                player.body.applyForceToCenter(0f, 200f, true)
            }
        }
    }

    override fun update(dt: Float) {
        handleInput()
        world.step(dt, 6, 2)
        player.update(dt)

        for (c in crystals) {
            c.update(dt)
        }
    }

    override fun render() {
        // clear screen
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // draw tile map
        tmr.setView(cam)
        tmr.render()

        // draw player
        sb.projectionMatrix = cam.combined
        player.render(sb)

        // draw crystals
        for (c in crystals) {
            c.render(sb)
        }

        // draw box2d world
        if (debug) {
            b2dr.render(world, b2dCam.combined)
        }
    }

    override fun dispose() {
    }

    private fun createPlayer() {
        val bdef = BodyDef()
        val shape = PolygonShape()
        val fdef = FixtureDef()

        bdef.position.set(160f / B2DVars.PPM, 200f / B2DVars.PPM)
        bdef.type = BodyDef.BodyType.DynamicBody
        val body: Body = world.createBody(bdef)
        shape.setAsBox(13f / B2DVars.PPM, 13f / B2DVars.PPM)
        fdef.shape = shape
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER
        fdef.filter.maskBits = B2DVars.BIT_RED
        body.createFixture(fdef).userData = "player"

        // create foot sensor
        shape.setAsBox(13 / B2DVars.PPM, 2 / B2DVars.PPM, Vector2(0f, -13 / B2DVars.PPM), 0f)
        fdef.shape = shape
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER
        fdef.filter.maskBits = B2DVars.BIT_RED
        fdef.isSensor = true
        body.createFixture(fdef).userData = "foot"

        // create player
        player = Player(body)
        // circular reference from body->player, and player->body
        body.userData = player
    }

    private fun createTiles() {
        // load tile map
        tileMap = TmxMapLoader().load("maps/test.tmx")
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
            body.createFixture(fdef)

            val c = Crystal(body)
            body.userData = c
            crystals.add(c)
        }
    }
}