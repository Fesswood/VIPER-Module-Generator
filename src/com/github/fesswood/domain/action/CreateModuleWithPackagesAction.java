package com.github.fesswood.domain.action;

import com.github.fesswood.data.ModuleMetaData;
import com.github.fesswood.domain.Utils.PackageService;
import com.github.fesswood.domain.generator.common.IModuleGenerator;
import com.github.fesswood.domain.generator.MultiDirModuleGeneratorImpl;
import com.github.fesswood.domain.generator.common.IMultiDirModuleGenerator;
import com.github.fesswood.ui.ViperOption;
import com.github.fesswood.ui.common.CreateModuleView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageDialogBuilder;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFileFactory;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;

/**
 * Created by fesswood on 27.09.16.
 */
public class CreateModuleWithPackagesAction extends AnAction {


    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        VirtualFile selectedDirectory = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (selectedDirectory != null && selectedDirectory.isDirectory()) {

            System.out.println("data vf" + selectedDirectory);
            new TestDialog(e.getProject(), selectedDirectory).show();
        } else {
            MessageDialogBuilder.yesNo("Ошибка", "Вы должны выбрать директорию!");
        }
    }

    private class TestDialog extends DialogWrapper {

        private final Project project;
        private VirtualFile selectedDirectory;
        CreateModuleView createModuleView;

        protected TestDialog(@Nullable Project project, VirtualFile selectedDirectory) {
            super(project);
            this.project = project;
            this.selectedDirectory = selectedDirectory;
            createModuleView = new ViperOption();
            init();
            setTitle("Форма создания классов для VIPER модуля");
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {

            return createModuleView.getRootView();
        }

        @Nullable
        @Override
        protected ValidationInfo doValidate() {
            return createModuleView.validate();
        }

        @Override
        protected void doOKAction() {
            System.out.println("TestDialog.processDoNotAskOnOk");
            ModuleMetaData moduleMetaData = createModuleView.getModuleMetaData();
            IMultiDirModuleGenerator generator = new MultiDirModuleGeneratorImpl(project);
            try {
                PackageService packageService = new PackageService(project,  moduleMetaData.getModuleName());
                generator.init(moduleMetaData, PsiFileFactory.getInstance(project), packageService.getPackages());
                generator.generate();
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.doOKAction();
        }

    }
}
