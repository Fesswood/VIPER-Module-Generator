package com.github.fesswood.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fesswood on 23.09.16.
 */
public class Const {

    public static class moduleNames {
        public static String DATA_MODULE_NAME = "data";
        public static String DATA_ROOT_PACKAGE = "db";
        public static String DOMAIN_ROOT_PACKAGE = "domain";
        public static String PRESENTATION_ROOT_PACKAGE = "presentation";
        public static String DOMAIN_MODULE_NAME = "domain";
        public static String PRESENTATION_MODULE_NAME = "presentation";

        public static List<String> getStandardNames() {
            return Arrays.asList(DATA_MODULE_NAME, DOMAIN_MODULE_NAME, PRESENTATION_MODULE_NAME);
        }
    }

    public static class string {

        public static class validation {

            public static String emptyModuleName = "Имя модуля не может быть пустым!";
            public static String incorrectModuleName = "Имя модуля не дебильным!";
            public static String emptyModelName = "Имя модели не может быть пустым!";
        }
    }

    public static class templateFileNames {
        public static String FT_ACTIVITY_NAME = "Viper Activity";
        public static String FT_FRAGMENT_NAME = "Viper Fragment";
        public static String FT_ROUTER_NAME = "Viper Router";
        public static String FT_PRESENTER_NAME = "Viper Presenter";
        public static String FT_INTERACTOR_NAME = "Viper Interactor";
        public static String FT_REPOSITORY_NAME = "Viper Repository";
        public static String FT_VIEW_NAME = "Viper View";
        public static String FT_DATA_MODULE_NAME = "Viper DataModule";
        public static String FT_DOMAIN_MODULE_NAME = "Viper DomainModule";

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
            return names;
        }
    }

}
