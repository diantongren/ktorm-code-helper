package com.github.diantongren.ktormcodehelper.ui;

import com.github.diantongren.ktormcodehelper.component.FileChooserComponent;
import com.github.diantongren.ktormcodehelper.model.CodeGenContext;
import com.github.diantongren.ktormcodehelper.service.IGenerator;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class ORMSettingsUI implements Configurable {

    private JPanel main;
    private JTextField classpath;
    private JTextField projectName;
    private JTextField modelPath;
    private JTextField daoPath;
    private JButton modelButton;
    private JButton daoButton;
    private JTextField prefix;

    //    private final ORMConfig config;
    private final Project project;
    private final IGenerator generator;

    public ORMSettingsUI(Project project, IGenerator generator) {
        this.project = project;
        this.generator = generator;
//        config = DataSetting.Companion.getInstance(project).getState();

        this.projectName.setText(project.getName());
        this.classpath.setText(project.getBasePath());

        // 选择PO生成目录
        this.modelButton.addActionListener(e -> {
            FileChooserComponent component = FileChooserComponent.Companion.getInstance(project);
            VirtualFile baseDir = LocalFileSystem.getInstance().findFileByPath(Objects.requireNonNull(project.getBasePath()));
            VirtualFile virtualFile = component.showFolderSelectionDialog("选择PO生成目录", baseDir, baseDir);
            if (null != virtualFile) {
                this.modelPath.setText(virtualFile.getPath());
            }
        });

        this.daoButton.addActionListener(e -> {
            FileChooserComponent component = FileChooserComponent.Companion.getInstance(project);
            VirtualFile baseDir = LocalFileSystem.getInstance().findFileByPath(Objects.requireNonNull(project.getBasePath()));
            VirtualFile virtualFile = component.showFolderSelectionDialog("选择DAO生成目录", baseDir, baseDir);
            if (null != virtualFile) {
                this.daoPath.setText(virtualFile.getPath());
            }
        });

    }

    public @Nullable JComponent createComponent() {
        return main;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() {
//        this.config.setEntityPath(this.modelPath.getText());
        CodeGenContext codeGenContext = new CodeGenContext(this.daoPath.getText(), this.modelPath.getText(), this.prefix.getText());
        generator.generate(project, codeGenContext);
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return "Config";
    }

}
