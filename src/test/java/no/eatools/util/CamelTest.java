package no.eatools.util;
/**
 *
 * @author AB22273
 * @since 12.nov.2008 11:46:41
 * @date 12.nov.2008
 */

import junit.framework.TestCase;

public class CamelTest extends TestCase {
    Camel camel;

    public void testToConstantString() throws Exception {
        String testString = "ThisIsATest";

        String constantString = Camel.toConstantString(testString);
        assertEquals("THIS_IS_A_TEST", constantString);

        testString = " This Is A \tTest\n";
        constantString = Camel.toConstantString(testString);
        assertEquals("THIS_IS_A_TEST", constantString);

        testString = " This Is a \ttest\n";
        constantString = Camel.toConstantString(testString);
        assertEquals("THIS_ISATEST", constantString);
    }

    public void testToCamelCaseString() throws Exception {
        String constantString = "THIS_IS_A_TEST_";

        String camelCaseString = Camel.toCamelCaseString(constantString);
        assertEquals("ThisIsATest", camelCaseString);

        constantString = "_THIS__IS_A_ TEST_";
        camelCaseString = Camel.toCamelCaseString(constantString);
        assertEquals("ThisIsATest", camelCaseString);

        constantString = "_THIS__IS_\nA_ TEST_";
        camelCaseString = Camel.toCamelCaseString(constantString);
        assertEquals("ThisIsATest", camelCaseString);

        constantString = "__THIS__IS_A TEST_";
        camelCaseString = Camel.toCamelCaseString(constantString);
        assertEquals("ThisIsATest", camelCaseString);

        constantString = "_ _THIS_\t\n_IS_A TEST_";
        camelCaseString = Camel.toCamelCaseString(constantString);
        assertEquals("ThisIsATest", camelCaseString);
    }

    public void testToPropertyName() throws Exception {
        String constantString = "THIS_IS_A_TEST_";

        String camelCaseString = Camel.toPropertyName(constantString);
        assertEquals("this.is.a.test", camelCaseString);

        constantString = "_THIS__IS_A_ TEST_";
        camelCaseString = Camel.toPropertyName(constantString);
        assertEquals("this.is.a.test", camelCaseString);

        constantString = "_THIS__IS_\nA_ TEST_";
        camelCaseString = Camel.toPropertyName(constantString);
        assertEquals("this.is.a.test", camelCaseString);

        constantString = "__THIS__IS_A TEST_";
        camelCaseString = Camel.toPropertyName(constantString);
        assertEquals("this.is.a.test", camelCaseString);

        constantString = "_ _THIS_\t\n_IS_A TEST_";
        camelCaseString = Camel.toPropertyName(constantString);
        assertEquals("this.is.a.test", camelCaseString);
    }

    public void testPropertyNameAsConstant() throws Exception {
        String testString = "This.Is.A.Test";

        String constantString = Camel.propertyNameAsConstant(testString);
        assertEquals("THIS_IS_A_TEST", constantString);

        testString = " This.is.a.Test\n";
        constantString = Camel.propertyNameAsConstant(testString);
        assertEquals("THIS_IS_A_TEST", constantString);

        testString = " This Is a \ttest\n";
        constantString = Camel.propertyNameAsConstant(testString);
        assertEquals("THIS_IS_A_TEST", constantString);

        testString = "this.is.a.test";
        constantString = Camel.propertyNameAsConstant(testString);
        assertEquals("THIS_IS_A_TEST", constantString);

    }

    public void testCamelCaseAsPropertyName() throws Exception {
        String testString = "ThisIsATest";

        String constantString = Camel.camelCaseAsPropertyName(testString);
        assertEquals("this.is.a.test", constantString);

        testString = " This Is A \tTest\n";
        constantString = Camel.camelCaseAsPropertyName(testString);
        assertEquals("this.is.a.test", constantString);

        testString = " This Is a \ttest\n";
        constantString = Camel.camelCaseAsPropertyName(testString);
        assertEquals("this.isatest", constantString);
    }
}