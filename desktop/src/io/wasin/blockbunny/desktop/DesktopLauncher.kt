package io.wasin.blockbunny.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import io.wasin.blockbunny.Game

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.title = Game.TITLE
        // always start at design resolution first then scale it to full screen by user later (if need)
        config.width = Game.V_WIDTH.toInt() * Game.SCALE
        config.height = Game.V_HEIGHT.toInt() * Game.SCALE
        LwjglApplication(Game(), config)
    }
}
