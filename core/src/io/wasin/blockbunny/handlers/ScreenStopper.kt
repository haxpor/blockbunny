package io.wasin.blockbunny.handlers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import io.wasin.blockbunny.Game

/**
 * Created by haxpor on 5/29/17.
 */
class ScreenStopper(tileMap: TiledMap, cam: OrthographicCamera) {
    // get total width for the input tilemap
    private val width: Float = tileMap.properties.get("tilewidth", Float::class.java) * tileMap.properties.get("width", Float::class.java)

    private val cam: OrthographicCamera = cam

    private var onReachEndOfLevel: (() -> Unit)? = null
    var isReachedEndOfLevel: Boolean = false
        private set

    var isStopped: Boolean = false
        get() {
            return isReachedEndOfLevel || field
        }
        private set

    fun setOnRearchEndOfLevel(listener: () -> Unit) {
        this.onReachEndOfLevel = listener
    }

    fun stop() {
        isStopped = true
    }

    fun update(dt: Float) {
        if (!isStopped && cam.position.x + Game.V_WIDTH/2f >= width) {
            Gdx.app.log("ScreenStopper", "reached end of level")

            // stop camera from progression
            cam.position.x = width - Game.V_WIDTH/2f
            cam.update()

            isReachedEndOfLevel = true
            isStopped = true

            onReachEndOfLevel?.invoke()
        }
    }
}