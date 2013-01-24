package no.eatools.util;

/**
 * @author AB22273
 * @date 05.nov.2008
 */
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ApplicationPropertiesTest extends TestCase {
    private static final Log log = LogFactory.getLog(ApplicationPropertiesTest.class);

    public void testLoadProperties() throws Exception {
        String rootPackageName = EaApplicationProperties.EA_ROOTPKG.value();
        assertNotNull(rootPackageName);
        assertEquals("Model", rootPackageName);
    }
}