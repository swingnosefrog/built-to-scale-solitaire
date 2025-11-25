package com.swingnosefrog.solitaire.persistence

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.GdxRuntimeException
import com.eclipsesource.json.Json
import java.util.Locale
import kotlin.collections.forEach
import kotlin.collections.set
import kotlin.collections.toMap


class JsonPreferences(private val fileHandle: FileHandle) : Preferences {

    private val map: MutableMap<String, String> = mutableMapOf()

    init {
        load()
    }

    private fun load() {
        if (!fileHandle.exists()) return

        try {
            val jsonStr = fileHandle.readString("UTF-8")
            val jsonObj = Json.parse(jsonStr).asObject()
            for (key in jsonObj.names()) {
                val value = jsonObj[key]
                if (!value.isString) continue
                map[key] = value.asString()
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    override fun putBoolean(key: String, `val`: Boolean): Preferences {
        map[key] = `val`.toString()
        return this
    }

    override fun putInteger(key: String, `val`: Int): Preferences {
        map[key] = `val`.toString()
        return this
    }

    override fun putLong(key: String, `val`: Long): Preferences {
        map[key] = `val`.toString()
        return this
    }

    override fun putFloat(key: String, `val`: Float): Preferences {
        map[key] = `val`.toString()
        return this
    }

    override fun putString(key: String, `val`: String): Preferences {
        map[key] = `val`
        return this
    }

    override fun put(vals: Map<String, *>): Preferences {
        vals.forEach { (key, value) ->
            when (value) {
                is Boolean -> putBoolean(key, value)
                is Int -> putInteger(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                is String -> putString(key, value)
            }
        }
        return this
    }

    override fun getBoolean(key: String): Boolean = this.getBoolean(key, false)

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return map[key]?.lowercase(Locale.ROOT)?.toBooleanStrictOrNull() ?: defValue
    }

    override fun getInteger(key: String): Int = this.getInteger(key, 0)

    override fun getInteger(key: String, defValue: Int): Int {
        return map[key]?.toIntOrNull() ?: defValue
    }

    override fun getLong(key: String): Long = this.getLong(key, 0L)

    override fun getLong(key: String, defValue: Long): Long {
        return map[key]?.toLongOrNull() ?: defValue
    }

    override fun getFloat(key: String): Float = this.getFloat(key, 0f)

    override fun getFloat(key: String, defValue: Float): Float {
        return map[key]?.toFloatOrNull() ?: defValue
    }

    override fun getString(key: String): String? = this.getString(key, "")

    override fun getString(key: String, defValue: String?): String? {
        return map[key] ?: defValue
    }

    override fun get(): Map<String, *> {
        return this.map.toMap()
    }

    override fun contains(key: String): Boolean {
        return map.containsKey(key)
    }

    override fun clear() {
        map.clear()
    }

    override fun remove(key: String) {
        map.remove(key)
    }

    override fun flush() {
        try {
            val obj = Json.`object`()
            map.forEach { (key, value) ->
                obj.set(key, value)
            }
            fileHandle.writeString(obj.toString(), false, "UTF-8")
        } catch (ex: Exception) {
            throw GdxRuntimeException("Error writing preferences: $fileHandle", ex)
        }
    }
}