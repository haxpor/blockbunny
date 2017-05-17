package io.wasin.blockbunny.entities

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.Body
import io.wasin.blockbunny.Game

/**
 * Created by haxpor on 5/17/17.
 */
class Player(body: Body) : B2DSprite(body) {

    private var numCrystals: Int = 0
    private var totalCrystals: Int = 0

    init {
        val tex = Game.res.getTexture("bunny")
        val sprites: Array<TextureRegion> = TextureRegion.split(tex, 32, 32)[0]
        setAnimation(sprites, 1 / 12f)
    }

    fun collectCrystal() {
        numCrystals++
    }

    fun getNumCrystals() : Int {
        return numCrystals
    }

    fun setTotalCrystals(value: Int) {
        totalCrystals = value
    }

    fun getTotalCrystals() : Int {
        return totalCrystals
    }
}