package no.eatools.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * This class handles the properties that can be used to configure the EA utilities. The
 * properties should be placed in a file called 'ea.application.properties' (i.e. corresponding
 * to the classname) which must be located in {user.home} or in classpath. If there are two property
 * files then both files will be read, and the properties in {user.home} will override the properties
 * from the file on the classpath.
 *
 * The names of the properties in the property file must correspond to the constants defined in
 * this enum class, and all enum-constants must have a corresponding property in the file.
 *
 * @author Ove Scheel
 * @author Per Spilling
 * @since 05.nov.2008 09:25:13
 */
public enum EaApplicationProperties {
    EA_PROJECT,
    EA_ROOTPKG,
    EA_DOC_ROOT_DIR,
    EA_LOGLEVEL,
    EA_DIAGRAM_TO_GENERATE;

    private static final Log log = LogFactory.getLog(EaApplicationProperties.class);

    private final static Properties applicationProperties = new Properties();

    /**
     * Loads development tool properties from the property file propsFilename
     * which must be located in {user.home} or in classpath.
     *
     * @return loaded properties
     */
    static {
        final String fileSeparator = SystemProperties.FILE_SEPARATOR.value();
        final String propsFilename = getPropertiesFilename();
        File homePropFile = new File(SystemProperties.USER_HOME.value() + fileSeparator + propsFilename);

        loadPropertiesFromClassPath(propsFilename);
        if (homePropFile.canRead()) {
            loadPropertiesFromUserHome(homePropFile);
        }
        if (applicationProperties.isEmpty()) {
            String helpMessage = "Couldn't find the property file - The properties should be placed in a file called 'ea.application.properties' (i.e. corresponding\n" +
                    "to the classname) which must be located in {user.home} or in classpath. If there are two property\n" +
                    "files then both files will be read, and the properties in {user.home} will override the properties\n" +
                    "from the file on the classpath. {user.home} = " + SystemProperties.USER_HOME.value();
            System.out.println(helpMessage);
            System.exit(0);
        }

        // Check that properties in the Enum property set also exist in the property file
        for (EaApplicationProperties prop : EaApplicationProperties.values()) {
            if (applicationProperties.getProperty(prop.key()) == null) {
                log.warn("Missing property [" + prop.key() + "] in property file: " + propsFilename);
            }
        }
        // Check that properties in the property file also exists in the Enum Property set
        for (Object key : applicationProperties.keySet()) {
            try {
                String enumName = Camel.propertyNameAsConstant((String) key);
                EaApplicationProperties.valueOf(enumName);
            } catch (IllegalArgumentException iae) {
                log.warn("Missing property enum [" + Camel.propertyNameAsConstant((String) key) + "] in " + EaApplicationProperties.class.getName());
            }
        }
    }

    private static String getPropertiesFilename() {
        // Expects a property file with the name of this class:
        String name = EaApplicationProperties.class.getName();
        if (name.lastIndexOf('.') > 0) {
            name = name.substring(name.lastIndexOf('.') + 1);
        }
        final String propsFilename = Camel.camelCaseAsPropertyName(name);
        return propsFilename;
    }

    private static void loadPropertiesFromClassPath(String propsFilename) {
        try {
            applicationProperties.load(EaApplicationProperties.class.getClassLoader().getResourceAsStream(propsFilename));
            log.info("Using properties from classpath");
        } catch (Exception e1) {
            // no need to worry
        }
    }

    private static void loadPropertiesFromUserHome(File homePropFile) {
        try {
            applicationProperties.load(new FileInputStream(homePropFile));
            log.info("Using properties from user.home");
        } catch (Exception e) {
            log.info("Unable to load properties from: " + homePropFile.getAbsolutePath());
        }
    }

    public String value() {
        return applicationProperties.getProperty(key(), "");
    }

    public String value(String defaultValue) {
        return applicationProperties.getProperty(key(), defaultValue);
    }

    public String key() {
        return Camel.toPropertyName(super.toString());
    }
}