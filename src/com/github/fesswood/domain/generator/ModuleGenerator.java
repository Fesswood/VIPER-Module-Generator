package com.github.fesswood.domain.generator;

import com.github.fesswood.data.Const;
import com.github.fesswood.data.ModuleMetaData;
import com.github.fesswood.domain.common.BaseModuleGenerator;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileTypes.impl.FileTypeManagerImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fesswood on 22.09.16.
 */
public class ModuleGenerator implements BaseModuleGenerator {


    private Project project;
    private ModuleMetaData moduleMetaData;
    private PsiFileFactory psiFileFactory;
    private PsiDirectory selectedDirectory;
    private List<String> templateFileNames;

    public ModuleGenerator(Project project) {
        this.project = project;
    }

    @Override
    public void init(ModuleMetaData moduleMetaData, PsiFileFactory instance, PsiDirectory selectedDirectory) {
        this.moduleMetaData = moduleMetaData;
        this.psiFileFactory = instance;
        this.selectedDirectory = selectedDirectory;
        this.templateFileNames = Const.TemplateFileNames.getTemplateFileNames();
        if (!moduleMetaData.isNeedInteractor()) {
            templateFileNames.remove(Const.TemplateFileNames.FT_INTERACTOR_NAME);
        }
        if (!moduleMetaData.isNeedRepository()) {
            templateFileNames.remove(Const.TemplateFileNames.FT_REPOSITORY_NAME);
        }
        if (!moduleMetaData.isNeedActivity()) {
            templateFileNames.remove(Const.TemplateFileNames.FT_ACTIVITY_NAME);
        }
        if (!moduleMetaData.isNeedDataModule()) {
            templateFileNames.remove(Const.TemplateFileNames.FT_DATA_MODULE_NAME);
            templateFileNames.remove(Const.TemplateFileNames.FT_DOMAIN_MODULE_NAME);
        }
        if (!moduleMetaData.isNeedFragmentPresenter()) {
            templateFileNames.remove(Const.TemplateFileNames.FT_FRAGMENT_NAME);
            templateFileNames.remove(Const.TemplateFileNames.FT_PRESENTER_NAME);
            templateFileNames.remove(Const.TemplateFileNames.FT_VIEW_NAME);
        }
    }

    @Override
    public void generate() {
        Properties properties = new Properties();
        properties.put("NAME", moduleMetaData.getModuleName());
        properties.put("UPPER_CASE_NAME", moduleMetaData.getModuleName().toUpperCase());
        properties.put("LOWER_CASE_NAME", moduleMetaData.getModuleName().toLowerCase());
        properties.put("MODEL_NAME", moduleMetaData.getRepositoryModelName());
        properties.put("LOWER_NAME", getModuleLowerName());
        properties.put("PACKAGE_NAME", getPackage());
        try {
            properties.put("BASE_PACKAGE_NAME", getRootPackage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        WriteCommandAction.runWriteCommandAction(project, () -> {
            try {
                for (String templateName : templateFileNames) {
                    TemplateImpl template = applyFileTemplate(project, templateName, properties);
                    createTemplateFile(template, templateName.replace("Viper ", ""));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private String getRootPackage() throws IOException {
        VirtualFile projectManifest = getProjectManifest(selectedDirectory.getVirtualFile());
        String content = new String(projectManifest.contentsToByteArray());
        Pattern pattern = Pattern.compile("package=\"(.*)\"");
        Matcher matcher = pattern.matcher(content);
        if(matcher.find()){
            return matcher.group(1);
        }
        return "";
    }

    @NotNull
    private String getModuleLowerName() {
        String moduleName = moduleMetaData.getModuleName();
        String firstLetter = moduleName.substring(0, 1);
        return moduleName.replace(firstLetter, firstLetter.toLowerCase());
    }

    public String getPackage() {
        String directoryPath = selectedDirectory.getVirtualFile().getPath();
        String regexp = "([\\/\\\\])";
        String[] packages = directoryPath.replaceAll(regexp, ".").split("src\\.main\\.java\\.");
        return packages.length > 1 ? packages[1] : "";
    }

    public VirtualFile getProjectManifest(VirtualFile selectedDirectory) {
        if (!selectedDirectory.getName().toLowerCase().equals("main")) {
            return getProjectManifest(selectedDirectory.getParent());
        }
        return selectedDirectory.findChild("AndroidManifest.xml");
    }

    private TemplateImpl applyFileTemplate(Project project,
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
    private String getTemplateContent(String templateName, Properties properties) throws IOException {
        FileTemplateManager manager = FileTemplateManager.getDefaultInstance();
        FileTemplate fileTemplate = manager.getTemplate(templateName);
        Properties allProperties = manager.getDefaultProperties();
        allProperties.putAll(properties);
        return fileTemplate.getText(allProperties);
    }

    @NotNull
    private String replaceTemplateParams(String templateCode) {
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

    public void createTemplateFile(TemplateImpl template, final String fileSuffix) {
        String preparedFileSuffix = prepareFileSuffix(fileSuffix);
        PsiFile fileFromText = psiFileFactory.createFileFromText(moduleMetaData.getModuleName() + preparedFileSuffix + ".java",
                FileTypeManagerImpl.getInstance().getFileTypeByExtension("java"), template.getTemplateText());
        new ReformatCodeProcessor(project, fileFromText, null, false).run();
        selectedDirectory.add(fileFromText);
    }

    @Contract(pure = true)
    private String prepareFileSuffix(String fileSuffix) {
        switch (fileSuffix) {
            case "Repository":
                return "RepositoryImpl";
            default:
                return fileSuffix;
        }
    }

}