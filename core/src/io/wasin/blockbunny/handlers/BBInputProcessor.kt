package io.wasin.blockbunny.handlers

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter

/**
 * Created by haxpor on 5/16/17.
 */
class BBInputProcessor : InputAdapter() {

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        BBInput.screenX = screenX
        BBInput.screenY = screenY
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        // keyboard
        BBInput.screenX = screenX
        BBInput.screenY = screenY
        BBInput.down = true

        // mouse
        BBInput.mouseDown = true
        if (button == Input.Buttons.LEFT) {
            BBInput.setMouseKey(BBInput.MOUSE_BUTTON_LEFT, true)
        }
        if (button == Input.Buttons.RIGHT) {
            BBInput.setMouseKey(BBInput.MOUSE_BUTTON_RIGHT, true)
        }

        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        // keyboard
        BBInput.screenX = screenX
        BBInput.screenY = screenY
        BBInput.down = false

        // mouse
        BBInput.mouseDown = false
        if (button == Input.Buttons.LEFT) {
            BBInput.setMouseKey(BBInput.MOUSE_BUTTON_LEFT, false)
        }
        if (button == Input.Buttons.RIGHT) {
            BBInput.setMouseKey(BBInput.MOUSE_BUTTON_RIGHT, false)
        }

        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        BBInput.screenX = screenX
        BBInput.screenY = screenY
        BBInput.down = true
        BBInput.mouseDown = true
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