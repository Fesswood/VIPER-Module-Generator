package com.github.fesswood.domain.Utils;

import com.github.fesswood.data.Const.moduleNames;
import com.intellij.ide.util.PackageUtil;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.psi.impl.file.PsiDirectoryFactoryImpl;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 * Created by fesswood on 28.09.16.
 */
public class PackageService {

    private Project project;
    private final ModuleManager moduleManager;
    private String moduleName;
    private boolean isDifferntModules = false;

    public PackageService(Project project, String moduleName) {
        this.project = project;
        this.moduleName = moduleName;
        moduleManager = ModuleManager.getInstance(project);
    }

    public boolean isViperArchitecture() {
        Module[] modules = moduleManager.getModules();
        List<String> standardModuleNames = moduleNames.getStandardNames();
        if (modules.length > 1) {
            Map<String, Module> collect = getModules(modules, standardModuleNames);
            // check that we have 3 module data domain and presentation
            isDifferntModules = true;
            return collect.size() == 3;
        }
        Module module = modules[0];
        ModuleHolder moduleHolder = new ModuleHolder(module).invoke();
        PsiDirectory data = moduleHolder.getData();
        PsiDirectory domain = moduleHolder.getDomain();
        PsiDirectory presentation = moduleHolder.getPresentation();
        return data != null && domain != null && presentation != null;
    }

    private Map<String, Module> getModules(Module[] modules, List<String> standardModuleNames) {
        return Arrays.asList(modules).stream()
                .filter(standardModuleNames::contains)
                .collect(toMap(Module::getName, module -> module));
    }

    public List<PsiDirectory> getPackages() {
        Map<String, Module> modules = getModules(moduleManager.getModules(), moduleNames.getStandardNames());
        if (isViperArchitecture() && isDifferntModules) {
            Module dataProjectModule = modules.get("data");
            Module domainProjectModule = modules.get("domain");
            Module presProjectModule = modules.get("presentation");
            PsiDirectory viperDataRootDir = PackageUtil
                    .findPossiblePackageDirectoryInModule(dataProjectModule, moduleNames.DATA_ROOT_PACKAGE);
            PsiDirectory viperDomainRootDir = PackageUtil
                    .findPossiblePackageDirectoryInModule(domainProjectModule, moduleNames.DATA_ROOT_PACKAGE);
            PsiDirectory viperPresentationRootDir = PackageUtil
                    .findPossiblePackageDirectoryInModule(presProjectModule, moduleNames.DATA_ROOT_PACKAGE);
            PsiDirectory data = createViperModuleDir(viperDataRootDir, dataProjectModule.getModuleFile());
            PsiDirectory domain = createViperModuleDir(viperDomainRootDir, domainProjectModule.getModuleFile());
            PsiDirectory presentation = createViperModuleDir(viperPresentationRootDir, presProjectModule.getModuleFile());
            return Arrays.asList(data, domain, presentation);
        }
        Module module = moduleManager.getModules()[0];
        ModuleHolder moduleHolder = new ModuleHolder(module).invoke();
        PsiDirectory data = moduleHolder.getData();
        PsiDirectory domain = moduleHolder.getDomain();
        PsiDirectory presentation = moduleHolder.getPresentation();
        return Arrays.asList(createViperModuleDir(data, module.getModuleFile()),
                createViperModuleDir(domain, module.getModuleFile()),
                createViperModuleDir(presentation, module.getModuleFile()));

    }

    private PsiDirectory createViperModuleDir(@Nullable PsiDirectory viperRootDir, VirtualFile moduleDir) {
        if (moduleDir == null) {
            throw new IllegalArgumentException("Directory of project module is null!");
        }

        PsiDirectoryFactory psiDirectoryFactory = PsiDirectoryFactoryImpl.getInstance(project);
        PsiDirectory psiDirectory = WriteCommandAction.runWriteCommandAction(project, (Computable<PsiDirectory>) () -> {
            try {
                PsiDirectory db = viperRootDir;
                if (db == null) {
                    db = psiDirectoryFactory
                            .createDirectory(moduleDir.createChildData(this, moduleNames.DATA_ROOT_PACKAGE));
                }
                return psiDirectoryFactory.createDirectory(db.getVirtualFile().createChildData(this, moduleName));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });

        return psiDirectory;
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

        public ModuleHolder invoke() {
            PsiDirectoryFactory psiDirectoryFactory = PsiDirectoryFactoryImpl.getInstance(project);
            PsiDirectory file = PsiManager.getInstance(project).findDirectory(module.getModuleFile());
            data = PackageUtil.findOrCreateDirectoryForPackage(module, "data", file, false);
            domain = PackageUtil.findOrCreateDirectoryForPackage(module, "domain", file, false);
            presentation = PackageUtil.findOrCreateDirectoryForPackage(module, "presentation", file, false);
            return this;
        }
    }
}
