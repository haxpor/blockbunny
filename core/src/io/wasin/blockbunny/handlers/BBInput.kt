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

        const val NUM_KEYS: Int = 2
        const val BUTTON1: Int = 0
        const val BUTTON2: Int = 1

        var keys: Array<Boolean> = Array<Boolean>(NUM_KEYS, { i -> false})
        var pkeys: Array<Boolean> = Array<Boolean>(NUM_KEYS, { i -> false})

        fun update() {
            // update previous down
            pdown = down

            for (i in 0..NUM_KEYS-1) {
                pkeys[i] = keys[i]
            }
        }

        fun isDown(i: Int): Boolean {
            return keys[i]
        }

        fun isPressed(i: Int): Boolean {
            return keys[i] && !pkeys[i]
        }

        fun setKey(i: Int, b: Boolean) {
            keys[i] = b
        }

        fun isDown(): Boolean {
            return down
        }

        fun isPressed(): Boolean {
            return down && !pdown
        }

        fun isReleased(): Boolean {
            return pdown && !down
        }
    }
}