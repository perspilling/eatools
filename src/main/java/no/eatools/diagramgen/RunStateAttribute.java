package no.eatools.diagramgen;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class holds a single object attributes that are to be represented as runState in the EA Object Instance.
 * <p/>
 * RunState info is represented like this in EA:
 * <pre>
 * "@VAR;Variable=myVar;Value=myVarValue;Op==;@ENDVAR;@VAR;Variable=attribOne;Value=aValue;Op==;@ENDVAR;"
 *  </pre>
 * @author AB22273
 * @date 05.nov.2008
 * @since 05.nov.2008 14:47:55
 */
public class RunStateAttribute {
    private static final Log log = LogFactory.getLog(RunStateAttribute.class);

    public static final String VAR_PREFIX = "@VAR;Variable=";
    public static final String VAR_SUFFIX = ";Op==;@ENDVAR;";
    public static final String VALUE_PREFIX = ";Value=";

    private final String name;
    private final String value;

    public RunStateAttribute(String xmlAttribute) {
        String[] parts = xmlAttribute.split("=");
        name = parts[0];
        value = parts[1].replaceAll("\\\"", "");
    }

    public String toString() {
        return "name: " + name + " value: " + value;
    }

    public String toRunState() {
        return VAR_PREFIX + name + VALUE_PREFIX + value + VAR_SUFFIX;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
