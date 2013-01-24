package no.eatools.diagramgen;

import no.eatools.util.Camel;

/**
 * @author AB22273
 * @date 28.nov.2008
 * @since 28.nov.2008 12:01:46
 */
public enum EaLinkDirection {
    UNSPECIFIED,

    /**
     * "Source -> Destination"
     */
    SOURCE_DESTINATION {
        public String toString() {
            return super.toString().replace(Camel.HYPHEN, SPACED_RIGHT_ARROW);
        }
    },

    /**
     * "Destination -> Source"
     */
    DESTINATION_SOURCE {
        public String toString() {
            return super.toString().replace(Camel.HYPHEN, SPACED_RIGHT_ARROW);
        }
    },

    /**
     * "Bi-Directional"
     */
    BI_DIRECTIONAL
    ;

    public static final String SPACED_RIGHT_ARROW = " -> ";
    /**
     * Simple factory for getting safe EAmetType.
     *
     * @param metaType
     * @return never null
     */
    public static EaLinkDirection fromString(String metaType) {
        EaLinkDirection theType = EaLinkDirection.UNSPECIFIED;
        try {
            theType = EaLinkDirection.valueOf(Camel.toConstantString(metaType));
        } catch (IllegalArgumentException iae) {
            // Default to the safe instance
        }
        return theType;
    }

    public String toString() {
        return Camel.toHyphenatedCamelCaseString(super.toString());
    }
}
