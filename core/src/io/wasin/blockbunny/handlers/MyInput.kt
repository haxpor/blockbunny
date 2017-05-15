package io.wasin.blockbunny.handlers

/**
 * Created by haxpor on 5/16/17.
 */
class MyInput {
    companion object {
        const val NUM_KEYS: Int = 2
        const val BUTTON1: Int = 0
        const val BUTTON2: Int = 1

        var keys: Array<Boolean> = Array<Boolean>(NUM_KEYS, { i -> false})
        var pkeys: Array<Boolean> = Array<Boolean>(NUM_KEYS, { i -> false})

        fun update() {
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
    }
}