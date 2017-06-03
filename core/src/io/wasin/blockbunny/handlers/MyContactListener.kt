package io.wasin.blockbunny.handlers

import com.badlogic.gdx.physics.box2d.*
import io.wasin.blockbunny.Game

/**
 * Created by haxpor on 5/16/17.
 */
class MyContactListener : ContactListener {

    private var numFootContacts: Int = 0
    var bodiesToRemove: MutableList<Body> = mutableListOf()
        private set

    var playerOnGround: Boolean = false
        get() = numFootContacts > 0

    var playerFrontCollided: Boolean = false
    var playerCollidedWithBomb: Boolean = false

    override fun beginContact(contact: Contact?) {
        val fa: Fixture? = contact?.fixtureA
        val fb: Fixture? = contact?.fixtureB

        if (fa == null || fb == null) return

        if (fa!!.userData != null && fa!!.userData.equals("foot")) {
            numFootContacts++
        }
        if (fb!!.userData != null && fb!!.userData.equals("foot")) {
            numFootContacts++
        }

        if (fa!!.userData != null && fa!!.userData.equals("crystal")) {
            Game.res.getSound("crystal")!!.play()
            bodiesToRemove.add(fa.body)
        }
        if (fb!!.userData != null && fb!!.userData.equals("crystal")) {
            Game.res.getSound("crystal")!!.play()
            bodiesToRemove.add(fb.body)
        }

        if (fa!!.userData != null && fa!!.userData.equals("bomb")) {
            playerCollidedWithBomb = true
        }
        if (fb!!.userData != null && fb!!.userData.equals("bomb")) {
            playerCollidedWithBomb = true
        }

        if (fa!!.userData != null && fa!!.userData.equals("front")) {
            playerFrontCollided = true
        }
        if (fb!!.userData != null && fb!!.userData.equals("front")) {
            playerFrontCollided = true
        }
    }

    override fun endContact(contact: Contact?) {
        val fa: Fixture? = contact?.fixtureA
        val fb: Fixture? = contact?.fixtureB

        if (fa == null || fb == null) return

        if (fa!!.userData != null && fa!!.userData.equals("foot")) {
            numFootContacts--
        }
        if (fb!!.userData != null && fb!!.userData.equals("foot")) {
            numFootContacts--
        }
    }

    override fun preSolve(contact: Contact?, oldManifold: Manifold?) {

    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {

    }

}