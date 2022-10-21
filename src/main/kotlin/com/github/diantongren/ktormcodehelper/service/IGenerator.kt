package com.github.diantongren.ktormcodehelper.service

import com.github.diantongren.ktormcodehelper.MyModel
import com.github.diantongren.ktormcodehelper.model.CodeGenContext
import com.github.diantongren.ktormcodehelper.model.Table
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project

interface IGenerator {
    abstract var event: AnActionEvent
    fun generate(project: Project, context: CodeGenContext)
}