package io.wasin.blockbunny.handlers

import com.badlogic.gdx.physics.box2d.*

/**
 * Created by haxpor on 5/16/17.
 */
class MyContactListener : ContactListener {
    override fun beginContact(contact: Contact?) {
        val fa: Fixture? = contact?.fixtureA
        val fb: Fixture? = contact?.fixtureB

        println("${fa?.userData}, ${fb?.userData}")
    }

    override fun endContact(contact: Contact?) {
        println("End contact")
    }

    override fun preSolve(contact: Contact?, oldManifold: Manifold?) {

    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {

    }

}