package com.github.diantongren.ktormcodehelper.service

import com.github.diantongren.ktormcodehelper.GeneratorConfig
import com.github.diantongren.ktormcodehelper.MyModel
import com.github.diantongren.ktormcodehelper.model.CodeGenContext
import com.github.diantongren.ktormcodehelper.model.Table
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import freemarker.template.TemplateException
import java.io.File
import java.io.IOException
import java.io.StringWriter
import java.nio.charset.StandardCharsets

abstract class AbstractGenerator : GeneratorConfig(), IGenerator {

    protected abstract fun generateORM(project: Project, context: CodeGenContext)

    override fun generate(project: Project, context: CodeGenContext) {
        generateORM(project, context)
    }

    fun writeFile(project: Project, path: String, name: String, flt: String, dataModel: Any) {
        try {
            val stringWriter = StringWriter()
            val template = super.getTemplate(flt)
            template.process(dataModel, stringWriter)
            ApplicationManager.getApplication()
                .runWriteAction {
                    createPackageDir(path)?.createChildData(project, name)
                        ?.setBinaryContent(stringWriter.toString().toByteArray(StandardCharsets.UTF_8))
                }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TemplateException) {
            e.printStackTrace()
        }
    }

    companion object {
        fun createPackageDir(codePath: String): VirtualFile? {
            val path = FileUtil.toSystemIndependentName(StringUtil.replace(codePath, ".", "/"))
            File(path).mkdirs()
            return LocalFileSystem.getInstance().refreshAndFindFileByPath(path)
        }
    }
}