package io.wasin.blockbunny.entities

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Filter
import io.wasin.blockbunny.Game

/**
 * Created by haxpor on 5/17/17.
 */
class Player(body: Body) : B2DSprite(body) {

    private var numCrystals: Int = 0
    private var totalCrystals: Int = 0
    var died: Boolean = false
        private set

    init {
        val tex = Game.res.getTexture("bunny")
        val sprites: Array<TextureRegion> = TextureRegion.split(tex, 32, 32)[0]
        setAnimation(sprites, 1 / 12f)
    }

    fun collectCrystal() {
        numCrystals++
    }

    fun getNumCrystals(): Int {
        return numCrystals
    }

    fun setTotalCrystals(value: Int) {
        totalCrystals = value
    }

    fun getTotalCrystals(): Int {
        return totalCrystals
    }

    fun makeDirectDie() {
        died = true
    }

    fun actDie() {
        // set mask bit to none
        // player will not anymore collide with anything
        val center = body.fixtureList.first()
        val foot = body.fixtureList[1]
        val front = body.fixtureList[2]

        var tmpFilterData: Filter

        tmpFilterData = center.filterData
        tmpFilterData.maskBits = 0
        center.filterData = tmpFilterData

        tmpFilterData = foot.filterData
        tmpFilterData.maskBits = 0
        foot.filterData = tmpFilterData

        tmpFilterData = front.filterData
        tmpFilterData.maskBits = 0
        front.filterData = tmpFilterData

        // apply force upward and slightly back
        body.applyForce(Vector2(-70f, 250f), body.getWorldPoint(Vector2(1f, 1f)), true)
        died = true
    }
}