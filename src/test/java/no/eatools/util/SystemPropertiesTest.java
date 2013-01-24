package no.eatools.util;
/**
 *
 * @author AB22273
 * @since 14.nov.2008 10:41:31
 * @date 14.nov.2008
 */

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Properties;

public class SystemPropertiesTest extends TestCase {
    private static final Log log = LogFactory.getLog(SystemPropertiesTest.class);

    SystemProperties systemProperties;

    public void testValue() throws Exception {
        Properties systemProperties = System.getProperties();

        for (Object name : systemProperties.keySet()) {
            String key = (String) name;
            log.info("Trying system property: " + key);
            // Drop environment properties set in cygwin-run
            if (!key.startsWith("env")) {
                try {
                    SystemProperties sysProp = SystemProperties.valueOf(Camel.propertyNameAsConstant(key));
                    assertEquals(systemProperties.getProperty(key), sysProp.value());
                } catch (IllegalArgumentException iae) {
                    log.warn("No Enum property for: " + key);
                }
            } else {
                log.info("Skipping:" + key);
            }
        }
    }

    public void testKey() throws Exception {
        Properties systemProperties = System.getProperties();

        for (Object name : systemProperties.keySet()) {
            String key = (String) name;
            log.info("Trying system property: " + key);
            // Drop environment properties set in cygwin-run
            if (!key.startsWith("env")) {
                try {
                    SystemProperties sysProp = SystemProperties.valueOf(Camel.propertyNameAsConstant(key));
                    assertEquals(key, sysProp.key());
                } catch (IllegalArgumentException iae) {
                    log.warn("No Enum property for: " + key);
                }
            } else {
                log.info("Skipping:" + key);
            }
        }
    }
}