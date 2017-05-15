package io.wasin.blockbunny.states

import io.wasin.blockbunny.handlers.B2DVars
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import io.wasin.blockbunny.Game
import io.wasin.blockbunny.handlers.GameStateManager
import io.wasin.blockbunny.handlers.MyContactListener
import kotlin.experimental.or

/**
 * Created by haxpor on 5/16/17.
 */
class Play(gsm: GameStateManager) : GameState(gsm) {

    private var world: World
    private var b2dr: Box2DDebugRenderer

    private var b2dCam: OrthographicCamera

    init {
        world = World(Vector2(0f, -1.81f), true)
        world.setContactListener(MyContactListener())
        b2dr = Box2DDebugRenderer()

        // create platform
        val bdef = BodyDef()
        bdef.position.set(160.0f / B2DVars.PPM, 120.0f / B2DVars.PPM)
        bdef.type = BodyDef.BodyType.StaticBody

        var body = world.createBody(bdef)

        val shape = PolygonShape()
        shape.setAsBox(50f / B2DVars.PPM, 5f / B2DVars.PPM)

        val fdef = FixtureDef()
        fdef.shape = shape
        fdef.filter.categoryBits = B2DVars.BIT_GROUND
        fdef.filter.maskBits = B2DVars.BIT_BOX or B2DVars.BIT_BALL
        body.createFixture(fdef).userData = "ground"

        // create falling box
        bdef.position.set(160f / B2DVars.PPM, 200f / B2DVars.PPM)
        bdef.type = BodyDef.BodyType.DynamicBody
        body = world.createBody(bdef)
        shape.setAsBox(5f / B2DVars.PPM,5f / B2DVars.PPM)
        fdef.shape = shape
        fdef.filter.categoryBits = B2DVars.BIT_BOX
        fdef.filter.maskBits = B2DVars.BIT_GROUND
        body.createFixture(fdef).userData = "box"

        // create ball
        bdef.position.set(153 / B2DVars.PPM, 220 / B2DVars.PPM)
        body = world.createBody(bdef)
        val cshape = CircleShape()
        cshape.radius = 5 / B2DVars.PPM
        fdef.shape = cshape
        fdef.filter.categoryBits = B2DVars.BIT_BALL
        fdef.filter.maskBits = B2DVars.BIT_GROUND
        body.createFixture(fdef).userData = "ball"

        // set box2d cam
        b2dCam = OrthographicCamera()
        b2dCam.setToOrtho(false, Game.V_WIDTH / B2DVars.PPM, Game.V_HEIGHT / B2DVars.PPM)
    }

    override fun handleInput() {
    }

    override fun update(dt: Float) {
        world.step(dt, 6, 2)
    }

    override fun render() {
        // clear screen
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // draw box2d world
        b2dr.render(world, b2dCam.combined)
    }

    override fun dispose() {
    }
}