package com.github.fesswood.data;

/**
 * Created by fesswood on 22.09.16.
 */
public class ModuleMetaData {
    boolean isNeedRepository;
    boolean isNeedInteractor;
    private boolean isNeedActivity;
    private boolean isNeedFragmentPresenter;
    private boolean isNeedDataModule;
    String moduleName;
    private String repositoryModelName;

    public ModuleMetaData(boolean isNeedRepository,
                          boolean isNeedInteractor,
                          boolean isNeedActivity,
                          boolean isNeedFragmentPresenter, boolean isNeedDataModule, String moduleName, String modelName) {
        this.isNeedRepository = isNeedRepository;
        this.isNeedInteractor = isNeedInteractor;
        this.isNeedActivity = isNeedActivity;
        this.isNeedFragmentPresenter = isNeedFragmentPresenter;
        this.isNeedDataModule = isNeedDataModule;
        this.moduleName = moduleName;
        this.repositoryModelName = modelName;
    }

    public boolean isNeedRepository() {
        return isNeedRepository;
    }

    public void setNeedRepository(boolean needRepository) {
        isNeedRepository = needRepository;
    }

    public boolean isNeedInteractor() {
        return isNeedInteractor;
    }

    public void setNeedInteractor(boolean needInteractor) {
        isNeedInteractor = needInteractor;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModuleMetaData that = (ModuleMetaData) o;

        if (isNeedRepository != that.isNeedRepository) return false;
        if (isNeedInteractor != that.isNeedInteractor) return false;
        return moduleName != null ? moduleName.equals(that.moduleName) : that.moduleName == null;

    }

    @Override
    public int hashCode() {
        int result = (isNeedRepository ? 1 : 0);
        result = 31 * result + (isNeedInteractor ? 1 : 0);
        result = 31 * result + (moduleName != null ? moduleName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ModuleMetaData{" +
                "isNeedRepository=" + isNeedRepository +
                ", isNeedInteractor=" + isNeedInteractor +
                ", moduleName='" + moduleName + '\'' +
                '}';
    }

    public String getRepositoryModelName() {
        return repositoryModelName;
    }

    public void setRepositoryModelName(String repositoryModelName) {
        this.repositoryModelName = repositoryModelName;
    }

    public boolean isNeedActivity() {
        return isNeedActivity;
    }

    public void setNeedActivity(boolean needActivity) {
        isNeedActivity = needActivity;
    }

    public boolean isNeedFragmentPresenter() {
        return isNeedFragmentPresenter;
    }

    public void setNeedFragmentPresenter(boolean needFragmentPresenter) {
        isNeedFragmentPresenter = needFragmentPresenter;
    }

    public boolean isNeedDataModule() {
        return isNeedDataModule;
    }

    public void setNeedDataModule(boolean needDataModule) {
        isNeedDataModule = needDataModule;
    }
}
