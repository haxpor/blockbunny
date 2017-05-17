package io.wasin.blockbunny.handlers

import com.badlogic.gdx.physics.box2d.*

/**
 * Created by haxpor on 5/16/17.
 */
class MyContactListener : ContactListener {

    private var numFootContacts: Int = 0

    var playerOnGround: Boolean = false
        get() = numFootContacts > 0

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