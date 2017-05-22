package io.wasin.blockbunny.handlers

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import io.wasin.blockbunny.Game
import io.wasin.blockbunny.interfaces.ScreenSizeChangedUpdatable

/**
 * Created by haxpor on 5/21/17.
 */
class Background(image: TextureRegion, gameCam: OrthographicCamera, scale: Float): ScreenSizeChangedUpdatable {

    private var image: TextureRegion = image
    private var cam: OrthographicCamera = gameCam

    var scale: Float = scale
        private set
    var velocity: Vector2 = Vector2(-1f, 0f)    // default is to move along screenX-direction only

    private var numXDraw: Int
    private var position: Vector2 = Vector2.Zero

    init {
        // calculate how many time we need to draw across screenX-direction
        // num draw will be at least 2
        numXDraw = Math.ceil((cam.viewportWidth / this.image.regionWidth).toDouble()).toInt() + 1
        position.x = (cam.viewportWidth/2 - cam.position.x) * scale
        position.y = (cam.viewportHeight/2 - cam.position.y) * scale
    }

    fun update(dt: Float) {
        // calculate diff of position to be updated to absolute position later
        position.x += velocity.x * scale * dt
        position.y += velocity.y * scale * dt
    }

    fun render(sb: SpriteBatch) {

        sb.begin()
        for (i in 0..numXDraw-1) {

            val x = (((position.x + cam.viewportWidth/2 - cam.position.x)*scale)% image.regionWidth) + (i * image.regionWidth)
            val y = (position.y + cam.viewportHeight/2 - cam.position.y)*scale

            sb.draw(image, x, y)
        }
        sb.end()
    }

    override fun updateScreenSize(width: Int, height: Int) {
        numXDraw = Math.ceil((cam.viewportWidth / this.image.regionWidth).toDouble()).toInt() + 1
        position.x = (cam.viewportWidth/2 - cam.position.x) * scale
        position.y = (cam.viewportHeight/2 - cam.position.y) * scale
    }
}