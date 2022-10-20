package com.github.diantongren.ktormcodehelper

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

class Generator : GeneratorConfig() {

    fun writeFile(model: MyModel, project: Project) {
        try {
            val stringWriter = StringWriter()
            val template = super.getTemplate("Entity.ftl")
            template.process(model, stringWriter)
            ApplicationManager.getApplication()
                .runWriteAction {
                    createPackageDir(model.packageName)?.createChildData(project, "${model.table.className}.kt")
                        ?.setBinaryContent(stringWriter.toString().toByteArray(StandardCharsets.UTF_8))
                }

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TemplateException) {
            e.printStackTrace()
        }
    }

    companion object {
        fun createPackageDir(packageName: String): VirtualFile? {
            val path = FileUtil.toSystemIndependentName(StringUtil.replace(packageName, ".", "/"))
            print(File(path).mkdirs())
            return LocalFileSystem.getInstance().refreshAndFindFileByPath(path)
        }
    }
}