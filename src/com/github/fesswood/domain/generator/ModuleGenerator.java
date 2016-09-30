package com.github.fesswood.domain.generator;

import com.github.fesswood.data.Const;
import com.github.fesswood.data.ModuleMetaData;
import com.github.fesswood.domain.Utils.PackageUtils;
import com.github.fesswood.domain.generator.common.BaseModuleGeneratorImpl;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by fesswood on 22.09.16.
 */
public class ModuleGenerator extends BaseModuleGeneratorImpl {


    public ModuleGenerator(Project project) {
        super(project);
    }

    @Override
    public void init(ModuleMetaData moduleMetaData, PsiFileFactory instance, PsiDirectory selectedDirectory) {
        this.setModuleMetaData(moduleMetaData);
        this.setPsiFileFactory(instance);
        this.setSelectedDirectory(selectedDirectory);
        this.setTemplateFileNames(Const.templateFileNames.getTemplateFileNames());
        removeUnnecessaryModules(moduleMetaData);
    }

    @Override
    public void generate() throws IOException {
        Properties properties = getProperties();
        Project project = getProject();
        WriteCommandAction.runWriteCommandAction(project, () -> {
            try {
                for (String templateName : getTemplateFileNames()) {
                    TemplateImpl template = applyFileTemplate(project, templateName, properties);
                    createTemplateFile(template, templateName.replace("Viper ", ""));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


}