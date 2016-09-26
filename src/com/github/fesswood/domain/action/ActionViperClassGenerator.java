package com.github.fesswood.domain.action;

import com.github.fesswood.data.ModuleMetaData;
import com.github.fesswood.domain.common.BaseModuleGenerator;
import com.github.fesswood.domain.generator.ModuleGenerator;
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
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.psi.impl.file.PsiDirectoryFactoryImpl;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by fesswood on 21.09.16.
 */
public class ActionViperClassGenerator extends AnAction {

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
            BaseModuleGenerator generator = new ModuleGenerator(project);
            PsiDirectoryFactory instance = PsiDirectoryFactoryImpl.getInstance(project);
            generator.init(moduleMetaData, PsiFileFactory.getInstance(project), instance.createDirectory(selectedDirectory));
            generator.generate();
            super.doOKAction();
        }
    }

}
