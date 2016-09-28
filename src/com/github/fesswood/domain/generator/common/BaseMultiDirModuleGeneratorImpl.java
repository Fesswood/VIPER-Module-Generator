package com.github.fesswood.domain.generator.common;

import com.intellij.openapi.project.Project;

/**
 * Created by fesswood on 27.09.16.
 */
public abstract class BaseMultiDirModuleGeneratorImpl extends BaseModuleGeneratorImpl implements IMultiDirModuleGenerator {


    public BaseMultiDirModuleGeneratorImpl(Project project) {
        super(project);
    }
}
