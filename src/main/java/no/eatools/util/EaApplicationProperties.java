package no.eatools.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * This class handles the properties that can be used to configure the EA utilities. The property file can:
 * <ul>
 *     <li>
 *     Be given as a parameter, or
 *     </li>
 *     <li>
 *     Be placed in a file called 'ea.application.properties' (i.e. corresponding to the classname) which
 *     must be located in {user.home} or in classpath.
 *     </li>
 * </ul>
 * <p>
 * The order of precedence is: parameter, file in {user.home}, file in classpath.
 * </p>
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
    private static String propsFilename = null;

    public static void init() {
        _init();
    }

    public static void init(String propertyFilename) {
        propsFilename = propertyFilename;
        _init();
    }

    /**
     * Loads development tool properties from the property file propsFilename which can be given as a
     * parameter, be located in {user.home}, or in the classpath. A parameter value will have precedence
     * over a file in the home directory, which will have precedence over a property file in the classpath.
     *
     * @return loaded properties
     */
    static void _init() {
        final String fileSeparator = SystemProperties.FILE_SEPARATOR.value();
        if (propsFilename == null) {
            propsFilename = getPropertiesFilename();
        }
        File localPropFile = new File(propsFilename);
        File homePropFile = new File(SystemProperties.USER_HOME.value() + fileSeparator + propsFilename);

        if (localPropFile.canRead()) {
            loadPropertiesFromFile(localPropFile);
        } else if (homePropFile.canRead()) {
            loadPropertiesFromFile(homePropFile);
        } else {
            loadPropertiesFromClassPath(propsFilename);
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
                //log.warn("Missing property enum [" + Camel.propertyNameAsConstant((String) key) + "] in " + EaApplicationProperties.class.getName());
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

    private static void loadPropertiesFromFile(File file) {
        try {
            applicationProperties.load(new FileInputStream(file));
            log.info("Using properties from " + file.getName());
        } catch (Exception e) {
            log.info("Unable to load properties from: " + file.getName());
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