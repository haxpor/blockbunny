package io.wasin.blockbunny.handlers

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter

/**
 * Created by haxpor on 5/16/17.
 */
class BBInputProcessor : InputAdapter() {

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        BBInput.x = screenX
        BBInput.y = screenY
        BBInput.down = true
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        BBInput.x = screenX
        BBInput.y = screenY
        BBInput.down = false
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        BBInput.x = screenX
        BBInput.y = screenY
        BBInput.down = true
        return true
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.Z) {
            BBInput.setKey(BBInput.BUTTON1, true)
        }
        if (keycode == Input.Keys.X) {
            BBInput.setKey(BBInput.BUTTON2, true)
        }
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        if (keycode == Input.Keys.Z) {
            BBInput.setKey(BBInput.BUTTON1, false)
        }
        if (keycode == Input.Keys.X) {
            BBInput.setKey(BBInput.BUTTON2, false)
        }
        return true
    }
}