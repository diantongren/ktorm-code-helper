package com.github.diantongren.ktormcodehelper.state

import com.github.diantongren.ktormcodehelper.ORMConfig
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "DataSetting", storages = [Storage("plugin.xml")])
class DataSetting : PersistentStateComponent<ORMConfig> {
    private var state = ORMConfig(null, null, null)

    companion object {
        fun getInstance(project: Project): DataSetting {
            return project.getService(DataSetting::class.java)
        }
    }

    override fun getState(): ORMConfig {
        return state
    }

    override fun loadState(state: ORMConfig) {
        this.state = state
    }
}