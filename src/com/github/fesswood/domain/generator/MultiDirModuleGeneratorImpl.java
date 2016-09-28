package com.github.fesswood.domain.generator;

import com.github.fesswood.data.Const;
import com.github.fesswood.data.ModuleMetaData;
import com.github.fesswood.domain.generator.common.BaseMultiDirModuleGeneratorImpl;
import com.intellij.ide.util.PackageUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFileFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by fesswood on 27.09.16.
 */
public class MultiDirModuleGeneratorImpl extends BaseMultiDirModuleGeneratorImpl {

    private List<PsiDirectory> selectedDirectory;

    public MultiDirModuleGeneratorImpl(Project project) {
        super(project);
    }

    @Override
    public void init(ModuleMetaData moduleMetaData, PsiFileFactory instance, PsiDirectory selectedDirectory) {

    }


    @Override
    public void generate() throws IOException {
        Properties properties = getProperties();
        String rootPackage = getRootPackage();
        ProjectRootManager rootManager = ProjectRootManager.getInstance(getProject());
        ModuleManager moduleManager = ModuleManager.getInstance(getProject());
        System.out.println(Arrays.toString(rootManager.getContentRoots()));

    }

    @Override
    public void init(ModuleMetaData moduleMetaData, PsiFileFactory instance, List<PsiDirectory> selectedDirectory) {
        this.setModuleMetaData(moduleMetaData);
        this.setPsiFileFactory(instance);
        this.setSelectedDirectory(selectedDirectory.get(0));
        this.selectedDirectory = selectedDirectory;
        this.setTemplateFileNames(Const.templateFileNames.getTemplateFileNames());
        removeUnnecessaryModules(moduleMetaData);
    }
}
