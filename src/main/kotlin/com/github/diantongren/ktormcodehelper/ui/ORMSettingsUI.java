package com.github.diantongren.ktormcodehelper.ui;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ORMSettingsUI implements Configurable {

    private JPanel main;
    private JTextField classpath;
    private JPasswordField password;
    private JTextField projectName;
    private JTextField user;
    private JTextField database;
    private JTextField host;
    private JTextField port;
    private JTextField poPath;
    private JTextField daoPath;
    private JButton selectButton;
    private JButton poButton;
    private JButton daoButton;
    private JButton testButton;
    private JTextField xmlPath;
    private JButton xmlButton;
    private JTable table1;

    public ORMSettingsUI() {

       this.poPath.setText("asdasdaadad");

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
        System.out.println(this.poPath.getText());
        System.out.println(this.daoPath.getText());
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return "Config";
    }

}
