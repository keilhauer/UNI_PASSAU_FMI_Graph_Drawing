// =============================================================================
//
//   AllIOTests.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AllIOTests.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.io;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A test suite for all graffiti.io test cases.
 * 
 * @version $Revision: 5771 $
 */
public class AllIOTests extends TestCase {

    /**
     * Constructs a new test case.
     * 
     * @param name
     *            the name for the test case.
     */
    public AllIOTests(String name) {
        super(name);
    }

    /**
     * Runs the test case.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Assembles and returns a test suite for all test cases within all test
     * classes.
     * 
     * @return A test suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("I/O");

        // suite.addTest(tests.graffiti.attributes.IntegerAttributeTest.suite());
        // Add other test suites here
        return suite;
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
