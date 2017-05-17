package io.wasin.blockbunny.handlers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture

/**
 * Created by haxpor on 5/17/17.
 */
class Content {

    private var textures: HashMap<String, Texture>

    init {
        textures = HashMap<String, Texture>()
    }

    fun loadTexture(path: String, key: String) {
        val tex = Texture(Gdx.files.internal(path))
        textures.put(key, tex)
    }

    fun getTexture(key: String) : Texture? {
        return textures.get(key)
    }

    fun disposeTexture(key: String) {
        textures.remove(key)
    }
}