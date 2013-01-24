package no.eatools.diagramgen;

import no.eatools.util.Camel;

/**
 * @author AB22273
 * @date 02.des.2008
 * @since 02.des.2008 11:10:16
 */
public enum EaDiagramType {
    NULL,
    OBJECT,
    CLASS,
    COMPONENT,
    DEPLOYMENT,
    PACKAGE,
    COMPOSITE_STRUCTURE,
    USE_CASE,
    ACTIVITY,
    STATE_MACHINE,
    COMMUNICATION,
    SEQUENCE,
    TIMING,
    INTERACTION_OVERVIEW
    ;
    // todo complete as needs arise
    
    /**
     * Simple factory for getting safe EAmetType.
     *
     * @param diagramType
     * @return never null
     */
    public static EaDiagramType fromString(String diagramType) {
        EaDiagramType theType = EaDiagramType.NULL;
        try {
            theType = EaDiagramType.valueOf(Camel.toConstantString(diagramType));
        } catch (IllegalArgumentException iae) {
            // Default to the safe instance
        }
        return theType;
    }

    public String toString() {
        return Camel.toCamelCaseString(super.toString());
    }
}
