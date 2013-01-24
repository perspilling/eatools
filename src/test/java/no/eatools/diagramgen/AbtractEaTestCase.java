package no.eatools.diagramgen;

import junit.framework.TestCase;
import no.eatools.util.EaApplicationProperties;

import java.io.File;

/**
 * Helper class that takes care of standard JUnit things
 */
public abstract class AbtractEaTestCase extends TestCase {
    protected EaRepo eaRepo;

    protected void setUp() throws Exception {
        super.setUp();
        File modelFile = new File(EaApplicationProperties.EA_PROJECT.value());
        eaRepo = new EaRepo(modelFile);
        eaRepo.open();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        eaRepo.close();
    }
}
