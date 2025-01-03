// =============================================================================
//
//   TestNodeListener.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TestNodeListener.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.event;

import java.util.LinkedList;
import java.util.logging.Logger;

import org.graffiti.event.NodeEvent;
import org.graffiti.event.NodeListener;
import org.graffiti.event.TransactionEvent;

/**
 * Auxiliary test class to examine the functionality of ListenerManager.
 * 
 * @version $Revision: 5771 $
 */
public class TestNodeListener implements NodeListener {
    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(TestNodeListener.class.getName());

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
    public void postInEdgeAdded(NodeEvent e) {
        methodCalled("postInEdgeAdded");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void postInEdgeRemoved(NodeEvent e) {
        methodCalled("postInEdgeRemoved");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void postOutEdgeAdded(NodeEvent e) {
        methodCalled("postOutEdgeAdded");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void postOutEdgeRemoved(NodeEvent e) {
        methodCalled("postOutEdgeRemoved");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void postUndirectedEdgeAdded(NodeEvent e) {
        methodCalled("postUndirectedEdgeAdded");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void postUndirectedEdgeRemoved(NodeEvent e) {
        methodCalled("postUndirectedEdgeRemoved");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void preInEdgeAdded(NodeEvent e) {
        methodCalled("preInEdgeAdded");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void preInEdgeRemoved(NodeEvent e) {
        methodCalled("preInEdgeRemoved");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void preOutEdgeAdded(NodeEvent e) {
        methodCalled("preOutEdgeAdded");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void preOutEdgeRemoved(NodeEvent e) {
        methodCalled("preOutEdgeRemoved");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void preUndirectedEdgeAdded(NodeEvent e) {
        methodCalled("preUndirectedEdgeAdded");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void preUndirectedEdgeRemoved(NodeEvent e) {
        methodCalled("preUndirectedEdgeRemoved");
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
