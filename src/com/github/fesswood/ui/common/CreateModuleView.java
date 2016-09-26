package com.github.fesswood.ui.common;

import com.github.fesswood.data.ModuleMetaData;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by fesswood on 22.09.16.
 */
public interface CreateModuleView {

    @Nullable
    public ValidationInfo validate();
    public ModuleMetaData getModuleMetaData();
    public JComponent getRootView();
}
