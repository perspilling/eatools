package no.eatools.diagramgen;
/**
 *
 * @author AB22273
 * @since 28.nov.2008 12:43:36
 * @date 28.nov.2008
 */

import junit.framework.TestCase;

public class EaLinkDirectionTest extends TestCase {
    EaLinkDirection eaLinkDirection;

    public void testFromString() throws Exception {
        eaLinkDirection = EaLinkDirection.fromString("Source -> Destination");
        assertSame(EaLinkDirection.SOURCE_DESTINATION, eaLinkDirection);

        eaLinkDirection = EaLinkDirection.fromString("Source->Destination");
        assertSame(EaLinkDirection.SOURCE_DESTINATION, eaLinkDirection);

        eaLinkDirection = EaLinkDirection.fromString("SourceDestination");
        assertSame(EaLinkDirection.SOURCE_DESTINATION, eaLinkDirection);

        eaLinkDirection = EaLinkDirection.fromString("Source<-Destination");
        assertSame(EaLinkDirection.SOURCE_DESTINATION, eaLinkDirection);

        eaLinkDirection = EaLinkDirection.fromString("Source<-Destinatio");
        assertSame(EaLinkDirection.UNSPECIFIED, eaLinkDirection);

        eaLinkDirection = EaLinkDirection.fromString("Destination -> Source");
        assertSame(EaLinkDirection.DESTINATION_SOURCE, eaLinkDirection);

        eaLinkDirection = EaLinkDirection.fromString("Destination->Source");
        assertSame(EaLinkDirection.DESTINATION_SOURCE, eaLinkDirection);

        eaLinkDirection = EaLinkDirection.fromString("DestinationSource");
        assertSame(EaLinkDirection.DESTINATION_SOURCE, eaLinkDirection);

        eaLinkDirection = EaLinkDirection.fromString("Destination<-Source");
        assertSame(EaLinkDirection.DESTINATION_SOURCE, eaLinkDirection);

        eaLinkDirection = EaLinkDirection.fromString("Destinatn<-Source");
        assertSame(EaLinkDirection.UNSPECIFIED, eaLinkDirection);

        eaLinkDirection = EaLinkDirection.fromString("Bi-Directional");
        assertSame(EaLinkDirection.BI_DIRECTIONAL, eaLinkDirection);

        eaLinkDirection = EaLinkDirection.fromString("Bi->Directional");
        assertSame(EaLinkDirection.BI_DIRECTIONAL, eaLinkDirection);

        eaLinkDirection = EaLinkDirection.fromString("BiDirectional");
        assertSame(EaLinkDirection.BI_DIRECTIONAL, eaLinkDirection);

        eaLinkDirection = EaLinkDirection.fromString("Bi -Directional");
        assertSame(EaLinkDirection.BI_DIRECTIONAL, eaLinkDirection);

        eaLinkDirection = EaLinkDirection.fromString("Bi<- Directional");
        assertSame(EaLinkDirection.BI_DIRECTIONAL, eaLinkDirection);
    }

    public void testToString() throws Exception {
        String dirString = EaLinkDirection.UNSPECIFIED.toString();
        assertEquals("Unspecified", dirString);

        dirString = EaLinkDirection.BI_DIRECTIONAL.toString();
        assertEquals("Bi-Directional", dirString);

        dirString = EaLinkDirection.SOURCE_DESTINATION.toString();
        assertEquals("Source -> Destination", dirString);

        dirString = EaLinkDirection.DESTINATION_SOURCE.toString();
        assertEquals("Destination -> Source", dirString);
    }
}