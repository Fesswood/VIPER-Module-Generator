package com.github.fesswood.domain.generator.common;

import com.github.fesswood.data.ModuleMetaData;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFileFactory;

import java.io.IOException;

/**
 * Created by fesswood on 22.09.16.
 */
public interface IModuleGenerator {
   public void init(ModuleMetaData moduleMetaData, PsiFileFactory instance, PsiDirectory selectedDirectory);

   public void generate() throws IOException;
}
