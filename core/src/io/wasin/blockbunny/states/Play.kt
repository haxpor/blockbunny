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
import io.wasin.blockbunny.handlers.MyInput

/**
 * Created by haxpor on 5/16/17.
 */
class Play(gsm: GameStateManager) : GameState(gsm) {

    private var world: World
    private var b2dr: Box2DDebugRenderer
    private var playerBody: Body
    private var b2dCam: OrthographicCamera
    private var cl: MyContactListener

    init {
        world = World(Vector2(0f, -9.81f), true)

        cl = MyContactListener()
        world.setContactListener(cl)
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
        fdef.filter.maskBits = B2DVars.BIT_PLAYER
        body.createFixture(fdef).userData = "ground"

        // create player
        bdef.position.set(160f / B2DVars.PPM, 200f / B2DVars.PPM)
        bdef.type = BodyDef.BodyType.DynamicBody
        playerBody = world.createBody(bdef)
        shape.setAsBox(5f / B2DVars.PPM,5f / B2DVars.PPM)
        fdef.shape = shape
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER
        fdef.filter.maskBits = B2DVars.BIT_GROUND
        playerBody.createFixture(fdef).userData = "player"

        // create foot sensor
        shape.setAsBox(2 / B2DVars.PPM, 2 / B2DVars.PPM, Vector2(0f, -5 / B2DVars.PPM), 0f)
        fdef.shape = shape
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER
        fdef.filter.maskBits = B2DVars.BIT_GROUND
        fdef.isSensor = true
        playerBody.createFixture(fdef).userData = "foot"

        // set box2d cam
        b2dCam = OrthographicCamera()
        b2dCam.setToOrtho(false, Game.V_WIDTH / B2DVars.PPM, Game.V_HEIGHT / B2DVars.PPM)
    }

    override fun handleInput() {
        // player jump
        if (MyInput.isPressed(MyInput.BUTTON1)) {
            if (cl.playerOnGround) {
                playerBody.applyForceToCenter(0f, 200f, true)
            }
        }
    }

    override fun update(dt: Float) {
        handleInput()
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