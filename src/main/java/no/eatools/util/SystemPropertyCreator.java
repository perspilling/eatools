package no.eatools.util;

import java.util.Properties;

/**
 * @author AB22273
 * @date 14.nov.2008
 * @since 14.nov.2008 10:15:01
 */
public class SystemPropertyCreator {

    public static void main(String[] args) {
        if ((args.length < 1) || ("".equals(args[0])) || (args[0] == null)) {
            System.out.println("You must supply a target package for the resulting property class");
        }
        new SystemPropertyCreator().create(args[0]);
    }

    private void create(String targetPackage) {
        Properties systemProperties = System.getProperties();


        for (Object name : systemProperties.keySet()) {
            String key = (String) name;
            System.out.println(Camel.propertyNameAsConstant(key) + ",");
        }
    }
}
