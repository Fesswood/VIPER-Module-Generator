package com.github.fesswood.domain.generator.common;

import com.github.fesswood.data.ModuleMetaData;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFileFactory;

import java.util.List;

/**
 * Created by fesswood on 28.09.16.
 */
public interface IMultiDirModuleGenerator extends IModuleGenerator {
   void init(ModuleMetaData moduleMetaData, PsiFileFactory instance, List<PsiDirectory> selectedDirectory);
}
