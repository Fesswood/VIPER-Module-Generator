package com.github.fesswood.domain.Utils;

import com.github.fesswood.data.Const;
import com.github.fesswood.data.Const.moduleNames;
import com.intellij.ide.util.PackageUtil;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 * Created by fesswood on 28.09.16.
 */
public class PackageService {

    private Project project;
    private ModuleManager moduleManager;
    private String moduleName;
    private boolean isDifferentModules = false;

    public PackageService(Project project, String moduleName) {
        this.project = project;
        this.moduleName = moduleName;
        moduleManager = ModuleManager.getInstance(project);
    }

    public boolean isCleanArchitecture() {
        Module[] modules = moduleManager.getModules();
        List<String> standardModuleNames = moduleNames.getStandardNames();
        Map<String, Module> collect = getModules(modules, standardModuleNames);
        // check that we have 3 module data domain and presentation
        return collect.size() == 3 && collect.get(moduleNames.DATA_MODULE_NAME) != null
                && collect.get(moduleNames.DOMAIN_MODULE_NAME) != null
                && collect.get(moduleNames.PRESENTATION_MODULE_NAME) != null;
    }

    public HashMap<String, PsiDirectory> getPackages() throws IOException {
        Map<String, Module> modules = getModules(moduleManager.getModules(), moduleNames.getStandardNames());
        if (isCleanArchitecture()) {
            return getDirectoriesForModules(modules);
        }
        return getDirectoriesForOneModule();
    }

    @NotNull
    private HashMap<String, PsiDirectory> getDirectoriesForOneModule() throws IOException {
        Module module = moduleManager.findModuleByName("app");
        ModuleHolder moduleHolder = new ModuleHolder(module).invoke();
        PsiDirectory data = moduleHolder.getData();
        PsiDirectory domain = moduleHolder.getDomain();
        PsiDirectory presentation = moduleHolder.getPresentation();

        return new HashMap<String, PsiDirectory>() {
            {
                put(moduleNames.DATA_ROOT_PACKAGE, data);
                put(moduleNames.DOMAIN_ROOT_PACKAGE, domain);
                put(moduleNames.PRESENTATION_ROOT_PACKAGE, presentation);
            }
        };
    }

    @NotNull
    private HashMap<String, PsiDirectory> getDirectoriesForModules(Map<String, Module> modules) throws IOException {
        PsiDirectory rootDataPackage = getModuleDirectory(modules.get(moduleNames.DATA_MODULE_NAME),
                PackageUtils.getRootPackage(modules.get(moduleNames.DATA_MODULE_NAME)));
        rootDataPackage = PackageUtil.findOrCreateSubdirectory(rootDataPackage, moduleNames.DATA_ROOT_PACKAGE);

        PsiDirectory rootDomainPackage = getModuleDirectory(modules.get(moduleNames.DOMAIN_MODULE_NAME),
                PackageUtils.getRootPackage(modules.get(moduleNames.DOMAIN_MODULE_NAME)));

        PsiDirectory rootPresentPackage = getModuleDirectory(modules.get(moduleNames.PRESENTATION_MODULE_NAME),
                PackageUtils.getRootPackage(modules.get(moduleNames.PRESENTATION_MODULE_NAME)));

        final PsiDirectory finalRootDataPackage = rootDataPackage;
        return new HashMap<String, PsiDirectory>() {
            {
                put(moduleNames.DATA_ROOT_PACKAGE, finalRootDataPackage);
                put(moduleNames.DOMAIN_ROOT_PACKAGE, rootDomainPackage);
                put(moduleNames.PRESENTATION_ROOT_PACKAGE, rootPresentPackage);
            }
        };
    }

    private PsiDirectory getModuleDirectory(Module module, String rootPackage) throws IOException {
        return PackageUtil.findPossiblePackageDirectoryInModule(module,
                rootPackage);
    }

    private Map<String, Module> getModules(Module[] modules, List<String> standardModuleNames) {
        return Arrays.asList(modules).stream()
                .filter(standardModuleNames::contains)
                .collect(toMap(Module::getName, module -> module));
    }

    private class ModuleHolder {
        private Module module;
        private PsiDirectory data;
        private PsiDirectory domain;
        private PsiDirectory presentation;

        public ModuleHolder(Module module) {
            this.module = module;
        }

        public PsiDirectory getData() {
            return data;
        }

        public PsiDirectory getDomain() {
            return domain;
        }

        public PsiDirectory getPresentation() {
            return presentation;
        }

        public ModuleHolder invoke() throws IOException {
            PsiDirectory rootViperPackage = getModuleDirectory(module, PackageUtils.getRootPackage(module));
            // get root of data dir
            data = WriteCommandAction.runWriteCommandAction(project, (Computable<PsiDirectory>) () ->
                    PackageUtil.findOrCreateSubdirectory(rootViperPackage, Const.moduleNames.DATA_MODULE_NAME));
            // get db dir in data dir
            data = WriteCommandAction.runWriteCommandAction(project, (Computable<PsiDirectory>) () ->
                    PackageUtil.findOrCreateSubdirectory(data, Const.moduleNames.DATA_ROOT_PACKAGE));

            domain = WriteCommandAction.runWriteCommandAction(project, (Computable<PsiDirectory>) () ->
                    PackageUtil.findOrCreateSubdirectory(rootViperPackage, Const.moduleNames.DOMAIN_MODULE_NAME));

            presentation = WriteCommandAction.runWriteCommandAction(project, (Computable<PsiDirectory>) () ->
                    PackageUtil.findOrCreateSubdirectory(rootViperPackage, Const.moduleNames.PRESENTATION_MODULE_NAME));

            return this;
        }
    }
}
