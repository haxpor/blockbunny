package io.wasin.blockbunny.entities

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.Body
import io.wasin.blockbunny.Game

/**
 * Created by haxpor on 5/17/17.
 */
class Crystal(body: Body) : B2DSprite(body) {
    init {
        val tex = Game.res.getTexture("crystal")
        val sprites = TextureRegion.split(tex, 16,16)[0]
        setAnimation(sprites, 1/12f)
    }
}