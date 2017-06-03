package io.wasin.blockbunny.entities

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.Body
import io.wasin.blockbunny.Game

/**
 * Created by haxpor on 6/3/17.
 */
class Bomb(body: Body): B2DSprite(body) {
    init {
        val tex = Game.res.getTexture("spikes")
        val sprites = TextureRegion.split(tex, 32,32)[0]
        setAnimation(sprites, 1/12f)
    }
}