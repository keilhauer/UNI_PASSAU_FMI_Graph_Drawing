// =============================================================================
//
//   TestGraphListener.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TestGraphListener.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.event;

import java.util.LinkedList;
import java.util.logging.Logger;

import org.graffiti.event.GraphEvent;
import org.graffiti.event.GraphListener;
import org.graffiti.event.TransactionEvent;

/**
 * Auxiliary test class to examine the functionality of ListenerManager.
 * 
 * @version $Revision: 5771 $
 */
public class TestGraphListener implements GraphListener {
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
    public void postEdgeAdded(GraphEvent e) {
        methodCalled("postEdgeAdded");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void postEdgeRemoved(GraphEvent e) {
        methodCalled("postEdgeRemoved");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void postGraphCleared(GraphEvent e) {
        methodCalled("postGraphCleared");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void postNodeAdded(GraphEvent e) {
        methodCalled("postNodeAdded");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void postNodeRemoved(GraphEvent e) {
        methodCalled("postNodeRemoved");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void preEdgeAdded(GraphEvent e) {
        methodCalled("preEdgeAdded");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void preEdgeRemoved(GraphEvent e) {
        methodCalled("preEdgeRemoved");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void preGraphCleared(GraphEvent e) {
        methodCalled("preGraphCleared");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void preNodeAdded(GraphEvent e) {
        methodCalled("preNodeAdded");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void preNodeRemoved(GraphEvent e) {
        methodCalled("preNodeRemoved");
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
