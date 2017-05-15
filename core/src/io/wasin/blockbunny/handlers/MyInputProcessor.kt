package io.wasin.blockbunny.handlers

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter

/**
 * Created by haxpor on 5/16/17.
 */
class MyInputProcessor : InputAdapter() {
    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.Z) {
            MyInput.setKey(MyInput.BUTTON1, true)
        }
        if (keycode == Input.Keys.X) {
            MyInput.setKey(MyInput.BUTTON2, true)
        }
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        if (keycode == Input.Keys.Z) {
            MyInput.setKey(MyInput.BUTTON1, false)
        }
        if (keycode == Input.Keys.X) {
            MyInput.setKey(MyInput.BUTTON2, false)
        }
        return true
    }
}