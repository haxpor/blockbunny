package io.wasin.blockbunny.entities

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import io.wasin.blockbunny.handlers.Animation
import io.wasin.blockbunny.handlers.B2DVars

/**
 * Created by haxpor on 5/17/17.
 */
open class B2DSprite(body: Body) {
    var body: Body = body
        private set
    protected var animation: Animation = Animation()

    var width: Float = 0f
        private set
    var height: Float = 0f
        private set

    var position: Vector2 = body.position
        get() = body.position
        private set

    fun setAnimation(regs: Array<TextureRegion>, delay: Float) {
        animation.setFrames(regs, delay)

        if (regs.size > 0) {
            // same size sprite
            width = regs[0].regionWidth.toFloat()
            height = regs[0].regionHeight.toFloat()
        }
    }

    fun update(dt: Float) {
        animation.update(dt)
    }

    fun render(sb: SpriteBatch) {
        sb.begin()
        sb.draw(
                animation.getCurrentFrame(),
                body.position.x * B2DVars.PPM - width / 2,
                body.position.y * B2DVars.PPM - height / 2)
        sb.end()
    }
}