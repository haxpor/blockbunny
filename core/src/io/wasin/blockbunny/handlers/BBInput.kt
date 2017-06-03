package io.wasin.blockbunny.handlers

/**
 * Created by haxpor on 5/16/17.
 */
class BBInput {
    companion object {

        var screenX: Int = 0
        var screenY: Int = 0

        var down: Boolean = false
        var pdown: Boolean = false

        var mouseDown: Boolean = false
        var pMouseDown: Boolean = false

        const val NUM_KEYS: Int = 2
        const val BUTTON1: Int = 0
        const val BUTTON2: Int = 1

        const val NUM_MOUSE_KEYS: Int = 2
        const val MOUSE_BUTTON_LEFT: Int = 0
        const val MOUSE_BUTTON_RIGHT: Int = 1

        var keys: Array<Boolean> = Array<Boolean>(NUM_KEYS, { i -> false})
        var pkeys: Array<Boolean> = Array<Boolean>(NUM_KEYS, { i -> false})

        var mouseKeys: Array<Boolean> = Array<Boolean>(NUM_MOUSE_KEYS, { i -> false})
        var pMouseKeys: Array<Boolean> = Array<Boolean>(NUM_MOUSE_KEYS, { i -> false})

        fun update() {
            // update previous down
            pdown = down
            pMouseDown = mouseDown

            for (i in 0..NUM_KEYS-1) {
                pkeys[i] = keys[i]
            }

            for (i in 0..NUM_MOUSE_KEYS-1) {
                pMouseKeys[i] = mouseKeys[i]
            }
        }

        fun isDown(i: Int): Boolean {
            return keys[i]
        }

        fun isMouseDown(i: Int): Boolean {
            return mouseKeys[i]
        }

        fun isPressed(i: Int): Boolean {
            return keys[i] && !pkeys[i]
        }

        fun isMousePressed(i: Int): Boolean {
            return mouseKeys[i] && !pMouseKeys[i]
        }

        fun setKey(i: Int, b: Boolean) {
            keys[i] = b
        }

        fun setMouseKey(i: Int, b: Boolean) {
            mouseKeys[i] = b
        }

        fun isDown(): Boolean {
            return down
        }

        fun isMouseDown(): Boolean {
            return mouseDown
        }

        fun isPressed(): Boolean {
            return down && !pdown
        }

        fun isMousePressed(): Boolean {
            return mouseDown && !pMouseDown
        }

        fun isReleased(): Boolean {
            return pdown && !down
        }

        fun isMouseReleased(): Boolean {
            return pMouseDown && !mouseDown
        }
    }
}