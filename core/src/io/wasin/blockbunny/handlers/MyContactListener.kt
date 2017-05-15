package io.wasin.blockbunny.handlers

import com.badlogic.gdx.physics.box2d.*

/**
 * Created by haxpor on 5/16/17.
 */
class MyContactListener : ContactListener {

    // get() and set() here is not necessary, but used here for demonstration
    // for kotlin syntax
    // use 'field' to access to its backing field
    var playerOnGround: Boolean = false
        get() = field
        private set(value) {
            field = value
        }

    override fun beginContact(contact: Contact?) {
        val fa: Fixture? = contact?.fixtureA
        val fb: Fixture? = contact?.fixtureB

        if (fa?.userData != null && fa?.userData.equals("foot")) {
            playerOnGround = true
        }
        if (fb?.userData != null && fb?.userData.equals("foot")) {
            playerOnGround = true
        }
    }

    override fun endContact(contact: Contact?) {
        val fa: Fixture? = contact?.fixtureA
        val fb: Fixture? = contact?.fixtureB

        if (fa?.userData != null && fa?.userData.equals("foot")) {
            playerOnGround = false
        }
        if (fb?.userData != null && fb?.userData.equals("foot")) {
            playerOnGround = false
        }
    }

    override fun preSolve(contact: Contact?, oldManifold: Manifold?) {

    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {

    }

}