// =============================================================================
//
//   AllEventTests.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AllEventTests.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.event;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A test suite for all graffiti.attributes test cases.
 * 
 * @version $Revision: 5771 $
 */
public class AllEventTests extends TestCase {

    /**
     * Constructs a new test case.
     * 
     * @param name
     *            the name for the test case.
     */
    public AllEventTests(String name) {
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
        TestSuite suite = new TestSuite("Event");
        suite.addTestSuite(tests.graffiti.event.ListenerManagerTest.class);
        suite.addTestSuite(tests.graffiti.event.GraphEventTest.class);
        suite.addTestSuite(tests.graffiti.event.NodeEventTest.class);
        suite.addTestSuite(tests.graffiti.event.EdgeEventTest.class);
        suite.addTestSuite(tests.graffiti.event.AttributeEventTest.class);

        // Add other test suites here
        return suite;
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
