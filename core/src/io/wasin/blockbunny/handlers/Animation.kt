package io.wasin.blockbunny.handlers

import com.badlogic.gdx.graphics.g2d.TextureRegion

/**
 * Created by haxpor on 5/17/17.
 */
class Animation(frames: Array<TextureRegion>, delay: Float) {

    private var frames: Array<TextureRegion> = frames
    private var time: Float = 1.0f
    private var delay: Float = delay
    private var currentFrame: Int = 1
    private var timesPlayed: Int = 0

    init {
        setFrames(frames, delay)
    }

    constructor(frames: Array<TextureRegion>) : this(frames, 1/12f) { }

    fun setFrames(frames: Array<TextureRegion>, delay: Float) {
        this.frames = frames
        this.delay = delay
        time = 0f
        currentFrame = 0
        timesPlayed = 0
    }

    fun update(dt: Float) {
        if (delay <= 0f) return
        time += dt
        while (time >= delay) {
            step()
        }
    }

    private fun step() {
        time -= delay
        currentFrame++
        if (currentFrame == frames.size) {
            currentFrame = 0
            timesPlayed++
        }
    }

    fun getFrame() : TextureRegion {
        return frames[currentFrame]
    }

    fun getTimesPlayed() : Int {
        return timesPlayed
    }
}