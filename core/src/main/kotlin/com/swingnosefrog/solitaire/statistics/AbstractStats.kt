package com.swingnosefrog.solitaire.statistics

import com.badlogic.gdx.files.FileHandle
import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonObject


abstract class AbstractStats {

    companion object {

        const val STATS_FORMAT_VERSION: Int = 1
    }

    data class UnknownStat(val id: String, val value: Int)

    private val _statMap: MutableMap<String, Stat> = linkedMapOf()
    val statMap: Map<String, Stat> = _statMap

    private val _unkStatMap: MutableMap<String, UnknownStat> = linkedMapOf()
    protected val unknownStatMap: Map<String, UnknownStat> = _unkStatMap
    

    protected fun register(stat: Stat): Stat {
        _statMap[stat.id] = stat
        return stat
    }

    open fun resetToInitialValues() {
        statMap.values.forEach { stat ->
            stat.setValue(stat.initialValue)
        }
    }

    open fun resetToResetValues() {
        statMap.values.forEach { stat ->
            stat.setValue(stat.resetValue)
        }
    }

    open fun fromJson(rootObj: JsonObject) {
        resetToInitialValues()

        val statsObj = rootObj["stats"].asObject()
        for (member in statsObj) {
            try {
                val statName = member.name
                val stat = statMap[statName]
                if (stat != null) {
                    stat.setValue(statsObj.getInt(stat.id, stat.initialValue))
                } else {
                    val value = member.value
                    if (value.isNumber) {
                        _unkStatMap[statName] = UnknownStat(statName, value.asInt())
                    }
                }
            } catch (ignored: Exception) {
            }
        }
    }

    open fun toJson(rootObj: JsonObject) {
        rootObj.add("stats_format_version", STATS_FORMAT_VERSION)
        rootObj.add("stats", Json.`object`().also { obj ->
            statMap.values.forEach { stat ->
                val value = stat.value.get()
                if (value != stat.initialValue) {
                    obj.add(stat.id, value)
                }
            }
            unknownStatMap.values.forEach { unk ->
                if (unk.id !in statMap.keys && obj.get(unk.id) == null) {
                    obj.add(unk.id, unk.value)
                }
            }
        })
    }

    /**
     * Returns true if the stats were loaded successfully or if there was no file.
     * Returns false if an exception occurred.
     */
    fun fromJsonFile(file: FileHandle): Boolean {
        resetToInitialValues()
        if (!file.exists() || file.isDirectory) return true

        return try {
            val str = file.readString("UTF-8")
            fromJson(Json.parse(str).asObject())
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun toJsonFile(file: FileHandle) {
        try {
            file.writeString(Json.`object`().also { obj ->
                toJson(obj)
            }.toString(), false, "UTF-8")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}