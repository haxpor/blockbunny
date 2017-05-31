package io.wasin.blockbunny.handlers

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.Viewport

/**
 * Created by haxpor on 5/22/17.
 */
class LevelButton(textureRegion: TextureRegion, levelNumber: Int, isClear: Boolean, x: Float, y: Float) {

    private var region: TextureRegion = textureRegion
    var levelNumber: Int = levelNumber
        private set
    var isClear: Boolean = isClear
        private set
    private var listener: ((Int) -> Unit)? = null
    private var bounds: Rectangle
    private var position: Vector2 = Vector2(x, y)

    init {
        bounds = Rectangle(x - region.regionWidth/2f, y - region.regionHeight/2f, region.regionWidth.toFloat(), region.regionHeight.toFloat())
    }

    fun setOnClickListener(listener: (Int) -> Unit) {
        this.listener = listener
    }

    fun update(cam: OrthographicCamera, viewport: Viewport, dt: Float) {

        if (BBInput.isPressed()) {
            // convert from screen position into world position to check collision (clicking)
            val screenCoor = Vector3(BBInput.screenX.toFloat(), BBInput.screenY.toFloat(), 0f)
            val worldCoor = cam.unproject(screenCoor, viewport.screenX.toFloat(), viewport.screenY.toFloat(), viewport.screenWidth.toFloat(), viewport.screenHeight.toFloat())

            if (bounds.contains(worldCoor.x, worldCoor.y)) {
                listener?.invoke(levelNumber)
            }
        }
    }

    fun render(textRenderer: TextRenderer, sb: SpriteBatch) {
        // draw base
        sb.draw(region, position.x - region.regionWidth/2f, position.y - region.regionHeight/2f)
        // draw text on top based on whether the level is clear or not
        if (isClear) {
            // draw at the half-center of the base
            // spare another part for indication of level cleared by slash
            textRenderer.renderNumber(levelNumber, position.x, position.y + textRenderer.fontHalfHeight, sb)
            textRenderer.renderSlash(position.x, position.y - textRenderer.fontHeight + 2f, sb)
        }
        else {
            // draw at the center of the base
            textRenderer.renderNumber(levelNumber, position.x, position.y, sb)
        }
    }
}