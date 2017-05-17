package io.wasin.blockbunny.handlers

import io.wasin.blockbunny.Game
import io.wasin.blockbunny.states.GameState
import io.wasin.blockbunny.states.Play

import java.util.Stack

/**
 * Created by haxpor on 5/14/17.
 */

class GameStateManager(game: Game) {
    var game: Game
        private set
    private var gameStates: Stack<GameState>

    init {
        this.game = game
        this.gameStates = Stack<GameState>()
    }

    companion object {
        const val PLAY = 912837
    }

    fun update(dt: Float) {
        this.gameStates.peek().update(dt)
    }

    fun render() {
        this.gameStates.peek().render()
    }

    private fun getState(state: Int): GameState? {
        if (state == PLAY)  return Play(this)
        return null
    }

    fun setState(state: Int) {
        this.popState()
        this.pushState(state)
    }

    fun pushState(state: Int) {
        this.gameStates.push(this.getState(state))
    }

    fun popState() {
        val g = this.gameStates.pop()
        g.dispose()
    }
}