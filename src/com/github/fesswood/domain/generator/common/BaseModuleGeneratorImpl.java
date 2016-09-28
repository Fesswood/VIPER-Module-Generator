package com.github.fesswood.domain.generator.common;

import com.github.fesswood.data.Const;
import com.github.fesswood.data.ModuleMetaData;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.fileTypes.impl.FileTypeManagerImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
        }
        if (!moduleMetaData.isNeedActivity()) {
            templateFileNames.remove(Const.templateFileNames.FT_ACTIVITY_NAME);
        }
        if (!moduleMetaData.isNeedDataModule()) {
            templateFileNames.remove(Const.templateFileNames.FT_DATA_MODULE_NAME);
            templateFileNames.remove(Const.templateFileNames.FT_DOMAIN_MODULE_NAME);
        }
        if (!moduleMetaData.isNeedFragmentPresenter()) {
            templateFileNames.remove(Const.templateFileNames.FT_FRAGMENT_NAME);
            templateFileNames.remove(Const.templateFileNames.FT_PRESENTER_NAME);
            templateFileNames.remove(Const.templateFileNames.FT_VIEW_NAME);
        }
    }


    @NotNull
    protected Properties getProperties() {
        Properties properties = new Properties();
        properties.put("NAME", getModuleMetaData().getModuleName());
        properties.put("UPPER_CASE_NAME", getModuleMetaData().getModuleName().toUpperCase());
        properties.put("LOWER_CASE_NAME", getModuleMetaData().getModuleName().toLowerCase());
        properties.put("MODEL_NAME", getModuleMetaData().getRepositoryModelName());
        properties.put("LOWER_NAME", getModuleLowerName());
        properties.put("PACKAGE_NAME", getPackage());
        return properties;
    }

    protected String getRootPackage() throws IOException {
        VirtualFile projectManifest = getProjectManifest(getSelectedDirectory().getVirtualFile());
        String content = new String(projectManifest.contentsToByteArray());
        Pattern pattern = Pattern.compile("package=\"(.*)\"");
        Matcher matcher = pattern.matcher(content);
        if(matcher.find()){
            return matcher.group(1);
        }
        return "";
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

    protected VirtualFile getProjectManifest(VirtualFile selectedDirectory) {
        if (!selectedDirectory.getName().toLowerCase().equals("main")) {
            return getProjectManifest(selectedDirectory.getParent());
        }
        return selectedDirectory.findChild("AndroidManifest.xml");
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
        PsiFile fileFromText = getPsiFileFactory().createFileFromText(
                getModuleMetaData().getModuleName() + preparedFileSuffix + ".java",
                FileTypeManagerImpl.getInstance().getFileTypeByExtension("java"), template.getTemplateText());
        new ReformatCodeProcessor(getProject(), fileFromText, null, false).run();
        getSelectedDirectory().add(fileFromText);
    }

    @Contract(pure = true)
    protected String prepareFileSuffix(String fileSuffix) {
        switch (fileSuffix) {
            case "Repository":
                return "RepositoryImpl";
            default:
                return fileSuffix;
        }
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
