package com.github.diantongren.ktormcodehelper

import com.github.diantongren.ktormcodehelper.service.IGenerator
import com.github.diantongren.ktormcodehelper.service.impl.GeneratorImpl
import com.github.diantongren.ktormcodehelper.ui.ORMSettingsUI
import com.intellij.database.psi.DbTable
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.options.ShowSettingsUtil

class KtormGenerateAction : AnAction() {
    private val generator: IGenerator = GeneratorImpl()
    override fun update(e: AnActionEvent) {
        val element = e.getData(LangDataKeys.PSI_ELEMENT)
        e.presentation.isEnabledAndVisible = element is DbTable
        super.update(e)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        generator.event = e
        ShowSettingsUtil.getInstance().editConfigurable(project, ORMSettingsUI(project, generator))
    }
}