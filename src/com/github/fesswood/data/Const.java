package com.github.fesswood.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fesswood on 23.09.16.
 */
public class Const {

    public static class moduleNames {
        public static final String INJECTION_ROOT_PACKAGE = "inject";
        public static final String DATA_MODULE_NAME = "data";
        public static final String DATA_ROOT_PACKAGE = "db";
        public static final String DOMAIN_ROOT_PACKAGE = "domain";
        public static final String PRESENTATION_ROOT_PACKAGE = "presentation";
        public static final String DOMAIN_MODULE_NAME = "domain";
        public static final String PRESENTATION_MODULE_NAME = "presentation";

        public static List<String> getStandardNames() {
            return Arrays.asList(DATA_MODULE_NAME, DOMAIN_MODULE_NAME, PRESENTATION_MODULE_NAME);
        }
    }

    public static class string {

        public static class validation {

            public static final String emptyModuleName = "Имя модуля не может быть пустым!";
            public static final String incorrectModuleName = "Имя модуля не дебильным!";
            public static final String emptyModelName = "Имя модели не может быть пустым!";
        }
    }

    public static class templateFileNames {
        public static final String FT_ACTIVITY_NAME = "Viper Activity";
        public static final String FT_FRAGMENT_NAME = "Viper Fragment";
        public static final String FT_ROUTER_NAME = "Viper Router";
        public static final String FT_PRESENTER_NAME = "Viper Presenter";
        public static final String FT_INTERACTOR_NAME = "Viper Interactor";
        public static final String FT_REPOSITORY_NAME = "Viper Repository";
        public static final String FT_VIEW_NAME = "Viper View";
        public static final String FT_DATA_MODULE_NAME = "Viper DataModule";
        public static final String FT_DOMAIN_MODULE_NAME = "Viper DomainModule";
        public static final String FT_COMPONENT_NAME = "Viper ActivityComponent";
        public static final String FT_MODEL_NAME = "Viper Model";

        public static List<String> getTemplateFileNames() {
            ArrayList<String> names = new ArrayList<>();
            names.add(FT_ACTIVITY_NAME);
            names.add(FT_FRAGMENT_NAME);
            names.add(FT_ROUTER_NAME);
            names.add(FT_PRESENTER_NAME);
            names.add(FT_INTERACTOR_NAME);
            names.add(FT_REPOSITORY_NAME);
            names.add(FT_VIEW_NAME);
            names.add(FT_DATA_MODULE_NAME);
            names.add(FT_DOMAIN_MODULE_NAME);
            names.add(FT_COMPONENT_NAME);
            names.add(FT_MODEL_NAME);
            return names;
        }
    }

}
