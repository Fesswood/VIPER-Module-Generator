package com.github.fesswood.domain.generator.common;

import com.github.fesswood.data.Const;
import com.github.fesswood.data.ModuleMetaData;
import com.github.fesswood.domain.Utils.PackageUtils;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.fileTypes.impl.FileTypeManagerImpl;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fesswood on 27.09.16.
 */
public abstract class BaseModuleGeneratorImpl implements IModuleGenerator {

    private Project project;
    private ModuleMetaData moduleMetaData;
    private PsiFileFactory psiFileFactory;
    private PsiDirectory selectedDirectory;
    private List<String> templateFileNames;

    public BaseModuleGeneratorImpl(Project project) {
        this.setProject(project);
    }


    protected void removeUnnecessaryModules(ModuleMetaData moduleMetaData) {
        List<String> templateFileNames = getTemplateFileNames();
        if (!moduleMetaData.isNeedInteractor()) {
            templateFileNames.remove(Const.templateFileNames.FT_INTERACTOR_NAME);
        }
        if (!moduleMetaData.isNeedRepository()) {
            templateFileNames.remove(Const.templateFileNames.FT_REPOSITORY_NAME);
            templateFileNames.remove(Const.templateFileNames.FT_MODEL_NAME);
        }
        if (!moduleMetaData.isNeedActivity()) {
            templateFileNames.remove(Const.templateFileNames.FT_ACTIVITY_NAME);
        }
        if (!moduleMetaData.isNeedDataModule()) {
            templateFileNames.remove(Const.templateFileNames.FT_DATA_MODULE_NAME);
            templateFileNames.remove(Const.templateFileNames.FT_DOMAIN_MODULE_NAME);
            templateFileNames.remove(Const.templateFileNames.FT_COMPONENT_NAME);
        }
        if (!moduleMetaData.isNeedFragmentPresenter()) {
            templateFileNames.remove(Const.templateFileNames.FT_FRAGMENT_NAME);
            templateFileNames.remove(Const.templateFileNames.FT_PRESENTER_NAME);
            templateFileNames.remove(Const.templateFileNames.FT_VIEW_NAME);
        }
    }


    @NotNull
    protected Properties getProperties() throws IOException {
        Properties properties = new Properties();
        Module curModule = ModuleUtil.findModuleForFile(getSelectedDirectory().getVirtualFile(), project);
        properties.put("NAME", getModuleMetaData().getModuleName());
        properties.put("UPPER_CASE_NAME", getModuleMetaData().getModuleName().toUpperCase());
        properties.put("LOWER_CASE_NAME", getModuleMetaData().getModuleName().toLowerCase());
        properties.put("MODEL_NAME", getModuleMetaData().getRepositoryModelName());
        properties.put("LOWER_NAME", getModuleLowerName());
        properties.put("PACKAGE_NAME", getPackage());
        properties.put("BASE_PACKAGE_NAME", PackageUtils.getRootPackage(curModule));
        return properties;
    }

    @NotNull
    protected String getModuleLowerName() {
        String moduleName = getModuleMetaData().getModuleName();
        String firstLetter = moduleName.substring(0, 1);
        return moduleName.replace(firstLetter, firstLetter.toLowerCase());
    }

    protected String getPackage() {
        String directoryPath = getSelectedDirectory().getVirtualFile().getPath();
        String regexp = "([\\/\\\\])";
        String[] packages = directoryPath.replaceAll(regexp, ".").split("src\\.main\\.java\\.");
        return packages.length > 1 ? packages[1] : "";
    }

    protected TemplateImpl applyFileTemplate(Project project,
                                             String templateName,
                                             Properties properties) throws IOException {
        String templateCode = getTemplateContent(templateName, properties);
        templateCode = replaceTemplateParams(templateCode);

        TemplateImpl template = (TemplateImpl) TemplateManager.getInstance(project).createTemplate("", "", templateCode);
        for (int i = 0; i < template.getSegmentsCount(); i++) {
            if (i == template.getEndSegmentNumber()) continue;
            String name = template.getSegmentName(i);
            String value = "\"" + properties.getProperty(name, "") + "\"";
            template.addVariable(name, value, value, true);
        }
        return template;
    }

    @NotNull
    protected String getTemplateContent(String templateName, Properties properties) throws IOException {
        FileTemplateManager manager = FileTemplateManager.getDefaultInstance();
        FileTemplate fileTemplate = manager.getTemplate(templateName);
        if (fileTemplate == null) {
            throw new FileNotFoundException("Template with name " + templateName + " not found!");
        }
        Properties allProperties = manager.getDefaultProperties();
        allProperties.putAll(properties);
        return fileTemplate.getText(allProperties);
    }

    @NotNull
    protected String replaceTemplateParams(String templateCode) {
        Pattern pattern = Pattern.compile("\\$\\{(.*)\\}");
        Matcher matcher = pattern.matcher(templateCode);
        StringBuffer builder = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(builder, "\\$" + matcher.group(1).toUpperCase() + "\\$");
        }
        matcher.appendTail(builder);
        templateCode = builder.toString();
        return templateCode;
    }

    protected void createTemplateFile(TemplateImpl template, final String fileSuffix) {
        String preparedFileSuffix = prepareFileSuffix(fileSuffix);
        String prefix = prepareFilePrefix(fileSuffix);
        PsiFile fileFromText = getPsiFileFactory().createFileFromText(
                prefix + preparedFileSuffix + ".java",
                FileTypeManagerImpl.getInstance().getFileTypeByExtension("java"), template.getTemplateText());
        //  new ReformatCodeProcessor(getProject(), fileFromText, null, false).run();
        getSelectedDirectory().add(fileFromText);
    }

    @Contract(pure = true)
    protected String prepareFileSuffix(String fileSuffix) {
        switch (fileSuffix) {
            case "Repository":
                return "RepositoryImpl";
            case "Model":
                return "";
            default:
                return fileSuffix;
        }
    }

    /**
     * Check file prefix for avoiding double prefix in model file like TaskTask.java
     *
     * @param filePrefix
     * @return
     */
    @NotNull
    protected String prepareFilePrefix(String filePrefix) {
        String prefix = getModuleMetaData().getModuleName();
        if (prefix.toLowerCase().equals(prepareFileSuffix(filePrefix).toLowerCase())) {
            prefix = "";
        }
        return prefix;
    }


    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public ModuleMetaData getModuleMetaData() {
        return moduleMetaData;
    }

    public void setModuleMetaData(ModuleMetaData moduleMetaData) {
        this.moduleMetaData = moduleMetaData;
    }

    public PsiFileFactory getPsiFileFactory() {
        return psiFileFactory;
    }

    public void setPsiFileFactory(PsiFileFactory psiFileFactory) {
        this.psiFileFactory = psiFileFactory;
    }

    public PsiDirectory getSelectedDirectory() {
        return selectedDirectory;
    }

    public void setSelectedDirectory(PsiDirectory selectedDirectory) {
        this.selectedDirectory = selectedDirectory;
    }

    public List<String> getTemplateFileNames() {
        return templateFileNames;
    }

    public void setTemplateFileNames(List<String> templateFileNames) {
        this.templateFileNames = templateFileNames;
    }
}
