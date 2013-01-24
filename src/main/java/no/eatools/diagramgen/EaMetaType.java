package no.eatools.diagramgen;

import no.eatools.util.Camel;

/**
 * The set of EA Meta types (should correspond to UML meta types).
 *
 * @author AB22273
 * @date 23.okt.2008
 * @since 23.okt.2008 14:12:40
 */
public enum EaMetaType {
    NULL,
    CLASS,
    COMPONENT,
    NODE,
    OBJECT,
    TAGGED_VALUE,
    PACKAGE,
    RELATIONSHIP,
    ASSOCIATION,
    LINK,
    DIAGRAM;
    // etc.
    // todo complete the set


    /**
     * Simple factory for getting safe EAmetType.
     *
     * @param metaType
     * @return never null
     */
    public static EaMetaType fromString(String metaType) {
        EaMetaType theType = EaMetaType.NULL;
        try {
            theType = EaMetaType.valueOf(Camel.toConstantString(metaType));
        } catch (IllegalArgumentException iae) {
            // Default to the safe instance
        }
        return theType;
    }

    public String toString() {
        return Camel.toCamelCaseString(super.toString());
    }
}
