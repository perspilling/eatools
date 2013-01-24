package no.eatools.diagramgen;
/**
 *
 * @author AB22273
 * @since 23.okt.2008 15:17:04
 * @date 23.okt.2008
 */

import junit.framework.TestCase;

public class EaMetaTypeTest extends TestCase {
    EaMetaType eaMetaType;

    public void testFromString() throws Exception {
        eaMetaType = EaMetaType.fromString("Component");
        assertTrue(eaMetaType == EaMetaType.COMPONENT);

        eaMetaType = EaMetaType.fromString("Rubbish");
        assertTrue(eaMetaType == EaMetaType.NULL);

        eaMetaType = EaMetaType.fromString("TaggedValue");
        assertTrue(eaMetaType == EaMetaType.TAGGED_VALUE);
    }


    public void testToString() throws Exception {
        assertEquals("Component", EaMetaType.COMPONENT.toString());
        assertEquals("TaggedValue", EaMetaType.TAGGED_VALUE.toString());
    }
}