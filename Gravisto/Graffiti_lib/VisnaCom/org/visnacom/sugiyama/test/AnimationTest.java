/*==============================================================================
*
*   AnimationTest.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: AnimationTest.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.Action;

import org.visnacom.controller.ViewPanel;
import org.visnacom.model.*;
import org.visnacom.sugiyama.AffinLinearAnimation;
import org.visnacom.sugiyama.SugiyamaDrawingStyle;
import org.visnacom.sugiyama.eval.DummyPicture.ImagePanel;
import org.visnacom.view.*;

import junit.framework.TestCase;

/**
 * shows a frame with a button. The animation is proceeded step by step. each
 * test method need only to contain a graph.
 */
public class AnimationTest extends TestCase {
    //~ Methods ================================================================

    /**
     *
     */
    public final void testSmallGraph() {
        MyAction a = initialize(getName());
        a.v = a.view.newLeaf(a.view.getRoot());

        Node n2 = a.view.newLeaf(a.view.getRoot());
        Node n3 = a.view.newLeaf(a.v);
        a.view.newEdge(n3, n2);
        //        a.view.newEdge(a.v, n2);
    }

    /**
     *
     */
    public final void testoldEdgeRemains() {
        MyAction a = initialize(getName());
        a.v = a.view.newLeaf(a.view.getRoot());

        Node n2 = a.view.newLeaf(a.view.getRoot());
        Node n3 = a.view.newLeaf(a.v);
        Edge e1 = a.view.newEdge(n3, n2);
        Edge e2 = a.view.newEdge(a.v, n2);
    }

    /**
     *
     */
    public void testall() {
        MyAction a = initialize(getName());
        CompoundGraph view = a.view;
        Node n1 = view.newLeaf(view.getRoot());
        Node n2 = view.newLeaf(view.getRoot());
        a.v = view.newLeaf(n1);

        Node v = a.v;
        Node n4 = view.newLeaf(n2);
        Node n5 = view.newLeaf(n1);
        Node n6 = view.newLeaf(n1);

        Node n7 = view.newLeaf(n2);
        Node n8 = view.newLeaf(n2);

        Node n9 = view.newLeaf(v);
        Node n10 = view.newLeaf(v);
        Node n11 = view.newLeaf(v);
        Node n12 = view.newLeaf(v);

        view.newEdge(n7, n8);
        view.newEdge(n8, n4);
        view.newEdge(n5, v);
        view.newEdge(n6, n5);

        Edge e1 = view.newEdge(n4, n10);
        Edge e2 = view.newEdge(n6, n10);
        Edge e7 = view.newEdge(n4, n9);
        Edge e8 = view.newEdge(n4, n11);
        Edge e9 = view.newEdge(n6, n9);
        Edge e10 = view.newEdge(n6, n12);

        Edge e3 = view.newEdge(n9, n10);
        Edge e4 = view.newEdge(n9, n11);
        Edge e5 = view.newEdge(n10, n12);
        Edge e6 = view.newEdge(n12, n9);

        //        SugiyamaDrawingStyle sds = new SugiyamaDrawingStyle(view, SugiyamaDrawingStyle.DEBUG_STYLE);
        //        sds.drawImpl();
        //        sds.show();
    }

    /**
     *
     *
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static MyAction initialize(String name) {
        MyAction action = new MyAction(name);

        ViewPanel panel = new ViewPanel();
        action.geo = panel.getGeometry();
        action.view = action.geo.getView();
        action.panel = panel;
        
        action.geo.setDrawingStyle(new DefaultDrawingStyle(action.geo));
        action.panel.setAnimationStyle(new NoAnimation(action.geo, panel));

        action.animation = new AffinLinearAnimation(action.geo, action.ip);
        action.animation.stepMode = true; //stepMode?

        action.frame = new JFrame();
        action.ip = new ImagePanel(action.frame, action.geo);

        action.frame.getContentPane().setLayout(new BorderLayout());
        action.frame.getContentPane().add(new JButton(action),
            BorderLayout.NORTH);
        action.frame.getContentPane().add(new JScrollPane(action.ip),
            BorderLayout.CENTER);

        action.frame.pack();

        action.frame.show();

        return action;
    }

    //~ Inner Classes ==========================================================

    /**
     *
     */
    private static class MyAction extends AbstractAction {
        protected ViewPanel panel;
        protected Geometry geo;
        protected ImagePanel ip;
        protected JFrame frame;
        protected AffinLinearAnimation animation;
        protected Node v; // The node to be expanded and collapsed
        protected View view;
        private String nextString;
        private int mode = 0;
        private int nextStep;

        /**
         * Creates a new MyAction object.
         *
         * @param string DOCUMENT ME!
         */
        public MyAction(String string) {
            super(string);
        }

        /**
         * DOCUMENT ME!
         *
         * @param arg0 DOCUMENT ME!
         */
        public void actionPerformed(ActionEvent arg0) {
            Thread thread =
                new Thread(new Runnable() {
                        public void run() {
                            runImpl();
                        }
                    });
            thread.start();
        }

        /**
         *
         */
        public void runImpl() {
            switch(mode) {
                case -1:
                    if(animation.stepMode) {
                        animation.nextFrame();
                        ip.repaint();
                    }

                    if(!animation.isRunning()) {
                        mode = nextStep;
                        putValue(Action.NAME, nextString);
                    }

                    break;

                case 0: //start
                    view.contract(v);
                    geo.setDrawingStyle(new SugiyamaDrawingStyle(geo));
                    panel.setAnimationStyle(animation);
                    geo.redraw();
                    ip.repaint();
                    mode = 1;
                    putValue(Action.NAME, "expand");
                    break;

                case 1:
                    view.expand(v);
                    nextStep = mode + 1;
                    nextString = "collapse";
                    mode = -1;
                    putValue(Action.NAME, "nextFrame");
                    break;

                case 2:
                    view.contract(v);
                    putValue(Action.NAME, "nextFrame");
                    nextStep = mode + 1;
                    mode = -1;
                    nextString = "exit";
                    break;

                case 3:
                    frame.dispose();
            }
        }
    }
}
