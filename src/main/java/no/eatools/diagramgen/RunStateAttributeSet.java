package no.eatools.diagramgen;

import no.eatools.util.SystemProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author AB22273
 * @date 05.nov.2008
 * @since 05.nov.2008 15:08:02
 */
public class RunStateAttributeSet {
    private static final Log log = LogFactory.getLog(RunStateAttributeSet.class);

    private Map<String, RunStateAttribute> theAttributes = new HashMap<String, RunStateAttribute>();

    /**
     * @param xmlAttributes xml-fragment as produced from xmlBean-instances.
     */
    public RunStateAttributeSet(String xmlAttributes) {
        String allAttribs = xmlAttributes.replaceAll("\\<[^ ]* ", "");
        allAttribs = allAttribs.replaceAll("xmlns:xsi.*", "");

        String singleAttributes[] = allAttribs.split("\" ");
        for (String single : singleAttributes) {
            RunStateAttribute runStateAttribute = new RunStateAttribute(single);
            theAttributes.put(runStateAttribute.getName(), runStateAttribute);
        }
    }

    /**
     * Remove attributes with names on the filteredAttributes list.
     *
     * @param filteredAttributes the list of attributes to remove from the RunStateAttributeSet.
     */
    @SuppressWarnings({"VariableArgumentMethod"})
    public void filter(String... filteredAttributes) {
        for (String filteredAtt : filteredAttributes) {
            RunStateAttribute runStateAttribute = theAttributes.get(filteredAtt);
            if (runStateAttribute != null) {
                theAttributes.remove(runStateAttribute.getName());
            }
        }
    }

    /**
     * Create a String that is suitable for the RunState setting in EA.
     *
     * @return EA-compliant RunState string.
     */
    public String toRunState() {
        StringBuilder sb = new StringBuilder();
        for (RunStateAttribute runStateAttribute : theAttributes.values()) {
            sb.append(runStateAttribute.toRunState());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (RunStateAttribute runStateAttribute : theAttributes.values()) {
            sb.append(runStateAttribute.toString()).append(SystemProperties.LINE_SEPARATOR.value());
        }
        return sb.toString();
    }

    public RunStateAttribute get(final String key) {
        return theAttributes.get(key);
    }
}
