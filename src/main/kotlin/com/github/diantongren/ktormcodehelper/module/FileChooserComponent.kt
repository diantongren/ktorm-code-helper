package com.github.diantongren.ktormcodehelper.module

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class FileChooserComponent(private val project: Project) {
    private val fileEditorManager: FileEditorManager = FileEditorManager.getInstance(project)

    fun showFolderSelectionDialog(title: String, toSelect: VirtualFile?, vararg roots: VirtualFile?): VirtualFile? {
        val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
        descriptor.title = title
        descriptor.setRoots(*roots)
        return FileChooser.chooseFile(descriptor, project, toSelect)
    }

    companion object {
        fun getInstance(project: Project): FileChooserComponent {
            return FileChooserComponent(project)
        }
    }
}