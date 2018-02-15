// =============================================================================
//
//   TestEdgeListener.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TestEdgeListener.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.event;

import java.util.LinkedList;
import java.util.logging.Logger;

import org.graffiti.event.EdgeEvent;
import org.graffiti.event.EdgeListener;
import org.graffiti.event.TransactionEvent;

/**
 * Auxiliary test class to examine the functionality of ListenerManager.
 * 
 * @version $Revision: 5771 $
 */
public class TestEdgeListener implements EdgeListener {

    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(TestGraphListener.class.getName());

    /** Contains a list of methods, which have been called. */
    public LinkedList<String> methodsCalled = new LinkedList<String>();

    /**
     * Contains the name of the method, that has been called by the
     * <code>ListenerManager</code>.
     */
    public String lastMethodCalled = "";

    /** Contains the number of times, a listener method has been called. */
    public int called = 0;

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void postDirectedChanged(EdgeEvent e) {
        methodCalled("postDirectedChanged");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void postEdgeReversed(EdgeEvent e) {
        methodCalled("postEdgeReversed");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void postSourceNodeChanged(EdgeEvent e) {
        methodCalled("postSourceNodeChanged");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void postTargetNodeChanged(EdgeEvent e) {
        methodCalled("postTargetNodeChanged");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void preDirectedChanged(EdgeEvent e) {
        methodCalled("preDirectedChanged");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void preEdgeReversed(EdgeEvent e) {
        methodCalled("preEdgeReversed");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void preSourceNodeChanged(EdgeEvent e) {
        methodCalled("preSourceNodeChanged");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void preTargetNodeChanged(EdgeEvent e) {
        methodCalled("preTargetNodeChanged");
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public String toString() {
        return methodsCalled.toString();
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void transactionFinished(TransactionEvent e) {
        methodCalled("transactionFinished");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void transactionStarted(TransactionEvent e) {
        methodCalled("transactionStarted");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param method
     *            DOCUMENT ME!
     */
    private void methodCalled(String method) {
        called++;
        lastMethodCalled = method;
        methodsCalled.add(method);
        logger.info(method + " called " + called + " times.");
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------