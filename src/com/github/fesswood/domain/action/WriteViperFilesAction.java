package com.github.fesswood.domain.action;

import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by fesswood on 22.09.16.
 */
public class WriteViperFilesAction extends WriteCommandAction {

    public WriteViperFilesAction(@Nullable Project project, PsiFile... files) {
        super(project, files);
    }

    @Override
    protected void run(@NotNull Result result) throws Throwable {
        System.out.println("WriteViperFilesAction.run" + result);
    }
}
