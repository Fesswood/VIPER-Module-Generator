package com.github.fesswood.domain.generator;

import com.github.fesswood.data.Const;
import com.github.fesswood.data.ModuleMetaData;
import com.github.fesswood.domain.generator.common.BaseMultiDirModuleGeneratorImpl;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.ide.util.PackageUtil;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

import static com.github.fesswood.data.Const.*;

/**
 * Created by fesswood on 27.09.16.
 */
public class MultiDirModuleGeneratorImpl extends BaseMultiDirModuleGeneratorImpl {

    private HashMap<String, PsiDirectory> moduleDirectories;

    public MultiDirModuleGeneratorImpl(Project project) {
        super(project);
    }

    @Override
    public void init(ModuleMetaData moduleMetaData, PsiFileFactory instance, PsiDirectory selectedDirectory) {

    }


    @Override
    public void generate() throws IOException {
        if (getModuleMetaData().isNeedRepository()) {
            generateModuleInDataDir();
        }
        if (getModuleMetaData().isNeedInteractor()) {
            generateModuleInDomainDir();
        }
        if (getModuleMetaData().isNeedDataModule()) {
            generateDependencyResolver();
        }
        generateModuleInPresentationDir(getModuleMetaData().isNeedActivity(),
                getModuleMetaData().isNeedFragmentPresenter());

    }

    private void generateModuleInDataDir() {
        //Find or create viper module folder exist in data.db package and next setup it as selected dir
        WriteCommandAction.runWriteCommandAction(getProject(), () -> {
            try {
                PsiDirectory rootDir = moduleDirectories.get(moduleNames.DATA_ROOT_PACKAGE);
                PsiDirectory moduleDir = PackageUtil.findOrCreateSubdirectory(rootDir, getModuleMetaData().getModuleName());
                setSelectedDirectory(moduleDir);

                String templateName = Const.templateFileNames.FT_REPOSITORY_NAME;
                TemplateImpl template = applyFileTemplate(getProject(), templateName, getProperties());
                safeCreateTemplateFile(templateName, template);

                templateName = Const.templateFileNames.FT_MODEL_NAME;
                template = applyFileTemplate(getProject(), templateName, getProperties());
                safeCreateTemplateFile(getModuleMetaData().getRepositoryModelName(), template);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void generateDependencyResolver() {
        WriteCommandAction.runWriteCommandAction(getProject(), () -> {
            try {
                //Find or create viper module folder exist in data.db package and next setup it as selected dir
                PsiDirectory rootDir = moduleDirectories.get(moduleNames.PRESENTATION_ROOT_PACKAGE);
                PsiDirectory injectDir = PackageUtil.findOrCreateSubdirectory(rootDir, moduleNames.INJECTION_ROOT_PACKAGE);
                PsiDirectory moduleDir = PackageUtil.findOrCreateSubdirectory(injectDir, getModuleMetaData().getModuleName());
                setSelectedDirectory(moduleDir);
                String templateName = Const.templateFileNames.FT_COMPONENT_NAME;
                TemplateImpl template = applyFileTemplate(getProject(), templateName, getProperties());
                safeCreateTemplateFile(templateName, template);

                templateName = Const.templateFileNames.FT_DOMAIN_MODULE_NAME;
                template = applyFileTemplate(getProject(), templateName, getProperties());
                safeCreateTemplateFile(templateName, template);

                templateName = Const.templateFileNames.FT_DATA_MODULE_NAME;
                template = applyFileTemplate(getProject(), templateName, getProperties());
                safeCreateTemplateFile(templateName, template);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void generateModuleInPresentationDir(boolean needActivity, boolean needFragmentPresenter) {
        WriteCommandAction.runWriteCommandAction(getProject(), () -> {
            try {
                //Find or create viper module folder exist in data.db package and next setup it as selected dir
                PsiDirectory rootDir = moduleDirectories.get(moduleNames.PRESENTATION_ROOT_PACKAGE);
                PsiDirectory moduleDir = PackageUtil.findOrCreateSubdirectory(rootDir, getModuleMetaData().getModuleName());
                setSelectedDirectory(moduleDir);
                String templateName;
                TemplateImpl template;
                if (needActivity) {
                    templateName = Const.templateFileNames.FT_ACTIVITY_NAME;
                    template = applyFileTemplate(getProject(), templateName, getProperties());
                    safeCreateTemplateFile(templateName, template);

                    templateName = Const.templateFileNames.FT_ROUTER_NAME;
                    template = applyFileTemplate(getProject(), templateName, getProperties());
                    safeCreateTemplateFile(templateName, template);
                }

                if (needFragmentPresenter) {
                    templateName = Const.templateFileNames.FT_PRESENTER_NAME;
                    template = applyFileTemplate(getProject(), templateName, getProperties());
                    safeCreateTemplateFile(templateName, template);

                    templateName = Const.templateFileNames.FT_FRAGMENT_NAME;
                    template = applyFileTemplate(getProject(), templateName, getProperties());
                    safeCreateTemplateFile(templateName, template);

                    templateName = Const.templateFileNames.FT_VIEW_NAME;
                    template = applyFileTemplate(getProject(), templateName, getProperties());
                    safeCreateTemplateFile(templateName, template);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    protected void safeCreateTemplateFile(String templateName, TemplateImpl template) {
        if (!isClassExist(templateName.replace("Viper ", ""))) {
            super.createTemplateFile(template, templateName.replace("Viper ", ""));
        }
    }

    private void generateModuleInDomainDir() {
        WriteCommandAction.runWriteCommandAction(getProject(), () -> {
            try {
                //Find or create viper module folder exist in data.db package and next setup it as selected dir
                PsiDirectory rootDir = moduleDirectories.get(moduleNames.DOMAIN_ROOT_PACKAGE);
                PsiDirectory moduleDir = PackageUtil.findOrCreateSubdirectory(rootDir, getModuleMetaData().getModuleName());
                setSelectedDirectory(moduleDir);
                String templateName = Const.templateFileNames.FT_INTERACTOR_NAME;
                TemplateImpl template = applyFileTemplate(getProject(), templateName, getProperties());
                safeCreateTemplateFile(templateName, template);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private boolean isClassExist(String className) {
        PsiDirectory moduleDir = getSelectedDirectory();
        String prefix = prepareFilePrefix(className);
        PsiFile file = moduleDir.findFile(prefix + prepareFileSuffix(className) + ".java");
        return file != null;
    }


    @Override
    public void init(ModuleMetaData moduleMetaData, PsiFileFactory instance, HashMap<String, PsiDirectory> selectedDirectory) {
        this.setModuleMetaData(moduleMetaData);
        this.setPsiFileFactory(instance);
        moduleDirectories = selectedDirectory;
        this.setSelectedDirectory(selectedDirectory.entrySet().stream().map(Map.Entry::getValue).findFirst().get());
        this.setTemplateFileNames(templateFileNames.getTemplateFileNames());
        removeUnnecessaryModules(moduleMetaData);
    }
}
