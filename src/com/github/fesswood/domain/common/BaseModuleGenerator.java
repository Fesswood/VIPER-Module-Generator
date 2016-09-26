package com.github.fesswood.domain.common;

import com.github.fesswood.data.ModuleMetaData;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFileFactory;

/**
 * Created by fesswood on 22.09.16.
 */
public interface BaseModuleGenerator {
   public void init(ModuleMetaData moduleMetaData, PsiFileFactory instance, PsiDirectory selectedDirectory);
   public void generate();
}
