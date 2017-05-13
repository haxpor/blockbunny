package io.wasin.blockbunny.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import io.wasin.blockbunny.Game

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.title = Game.TITLE
        config.width = Game.V_WIDTH * Game.SCALE
        config.height = Game.V_HEIGHT * Game.SCALE
        LwjglApplication(Game(), config)
    }
}
