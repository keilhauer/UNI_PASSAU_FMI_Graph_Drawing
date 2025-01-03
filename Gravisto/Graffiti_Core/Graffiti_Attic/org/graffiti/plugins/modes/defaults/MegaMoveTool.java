// =============================================================================
//
//   MegaMoveTool.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: MegaMoveTool.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.modes.defaults;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.editor.GraffitiInternalFrame;
import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.view.EdgeShape;
import org.graffiti.plugin.view.GraphElementComponent;
import org.graffiti.plugin.view.View;
import org.graffiti.undo.ChangeAttributesEdit;
import org.graffiti.undo.GraphElementsDeletionEdit;

/**
 * A tool for creating and editing a graph.
 * 
 * @author Holleis
 * @version $Revision: 5772 $
 * @deprecated
 */
@Deprecated
public class MegaMoveTool extends MegaTools {
    /** The logger for the current class. */
    private static final Logger logger = Logger.getLogger(MegaMoveTool.class
            .getName());

    /**
     * Specifies if graphelements that would be selected are highlighted during
     * dragging the selection rectangle.
     */
    protected boolean dynamicSelectionRect = true;

    /**
     * Specifies if - when there is a selection - graphelements are highlighted
     * only if the CTRL key is pressed.
     */
    protected boolean onlyIfCtrl = false;

    /** Deletes all selected items (incl. undo support). */
    private Action deleteAction;

    /** Component that was last marked. */
    private Component lastPressed;

    /** Coordinates of the bend on which the user most recently pressed. */
    private CoordinateAttribute lastBendHit;

    /**
     * Component used to associate the key binding for deleting graph elements
     * with.
     */
    private JComponent keyComponent;

    /** Saves the selected components. */
    private List<GraphElementComponent> tempSelectedComps;

    /**
     * Saves original attributes with their values in this map before they are
     * changed; used for undo
     */
    private Map<Attribute, Object> originalCoordinates;

    /** First corner of selection rectangle. */
    private Point2D lastPressedPoint;

    /** Second corner of selection rectangle. */
    private Point2D lastPressedPoint2;

    /** Point used for moving. */
    private Point2D lastPressedPointRel;

    /** The selection rectangle. */
    private Rectangle selRect = new Rectangle();

    /** Used to distinguish between simple move and drag. */
    private boolean dragged = false;

    /**
     * Constructor for this tool. Registers a key used to delete graph elements.
     */
    public MegaMoveTool() {
        deleteAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                // useful to check if the selection isn't empty
                // before an edit is build.
                if (!selection.isEmpty()) {
                    GraphElementsDeletionEdit edit = new GraphElementsDeletionEdit(
                            selection.getElements(), graph, geMap);
                    unmarkAll();
                    fireSelectionChanged();
                    edit.execute();
                    undoSupport.postEdit(edit);
                }
            }
        };
    }

    /**
     * States whether this class wants to be registered as a
     * <code>SessionListener</code>. This tool returns true.
     * 
     * @return DOCUMENT ME!
     */
    public boolean isSessionListener() {
        return true;
    }

    /**
     * The method additionally registers a key used to delete graph elements.
     * 
     * @see org.graffiti.plugins.modes.deprecated.AbstractTool#activate()
     */
    public void activate() {
        super.activate();

        // System.out.println("Activating MegaMoveTool");

        try {
            JComponent view = this.session.getActiveView().getViewComponent();

            // I don't really understand why I had to do this, honestly:
            while (!(view instanceof GraffitiInternalFrame)) {
                if (view.getParent() == null) {
                    break;
                } else {
                    view = (JComponent) view.getParent();
                }
            }

            keyComponent = view;

            String deleteName = "delete";
            view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(
                            KeyStroke.getKeyStroke(
                                    java.awt.event.KeyEvent.VK_DELETE, 0),
                            deleteName);
            view.getActionMap().put(deleteName, deleteAction);
        } catch (ClassCastException cce) {
            System.err.println("Failed to register a key for some action in "
                    + getClass().getName() + ", activate()");
        }
    }

    /**
     * This method additionaly unregisters the key used for deleting graph
     * elements.
     * 
     * @see org.graffiti.plugins.modes.deprecated.AbstractTool#deactivate()
     */
    public void deactivate() {
        super.deactivate();

        // System.out.println("Deactivating MegaMoveTool");

        try {
            keyComponent
                    .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(
                            KeyStroke.getKeyStroke(
                                    java.awt.event.KeyEvent.VK_DELETE, 0), null);
        } catch (Exception e) {
            System.err.println("Error in deactivate tool: Delete-Key could "
                    + "not be unregistered. (CK)");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Empty method. Invoked at mouse clicks. Does not do anything. All is done
     * via mousePressed.
     * 
     * @param e
     *            the mouse event
     */
    public void mouseClicked(MouseEvent e) {
        logger.info("MOUSE CLICKED");
    }

    /**
     * Invoked when the mouse button has been pressed and dragged inside the
     * editor panel and handles what has to happen.
     * 
     * @param e
     *            the mouse event
     */
    public void mouseDragged(MouseEvent e) {
        logger.info("MOUSE DRAGGED");

        if (!SwingUtilities.isLeftMouseButton(e)) {
            dragged = true;

            return;
        }

        if (lastBendHit != null) {
            // calculated moved distance
            double newX = e.getPoint().getX() + lastPressedPointRel.getX();
            double newY = e.getPoint().getY() + lastPressedPointRel.getY();

            if (!dragged) {
                ChangeAttributesEdit edit = new ChangeAttributesEdit(
                        lastBendHit, geMap);
                undoSupport.postEdit(edit);
            }

            lastBendHit.setCoordinate(new Point2D.Double(newX, newY));
            e.getComponent().repaint();
            dragged = true;

            return;
        }

        if (lastPressedPoint != null) { // equiv. to "pressed" on view before
            lastPressedPoint2 = e.getPoint();

            paintSelectionRectangle((JComponent) e.getSource(),
                    lastPressedPoint, lastPressedPoint2);

            // update view scrolling
            View view = (View) e.getComponent();
            view.autoscroll(new Point((int) lastPressedPoint2.getX(),
                    (int) lastPressedPoint2.getY()));
        } else if (lastPressed instanceof NodeComponent) {
            if (isControlDown(e)) {
                dragged = true;

                return;
            }

            assert !selection.isEmpty() : "Strangely the selection list is "
                    + "empty although a component has been selected";

            if (!dragged) {
                // first call to mouseDragged
                // save changed (Coordinate/Bends Attributes) for undo edit
                List<GraphElement> selElements = selection.getElements();

                originalCoordinates = new HashMap<Attribute, Object>();

                ChangeAttributesEdit edit;

                for (GraphElement ge : selElements) {
                    if (ge instanceof Node) {
                        // for nodes, add their coordinates
                        Attribute coAttr = ge
                                .getAttribute(GraphicAttributeConstants.COORD_PATH);

                        // TODO:fix finally the access to the attribute values
                        // over the getValue().
                        // It is currently only a temporary solution for
                        // nonfixed
                        // access.
                        originalCoordinates.put(coAttr, ((Attribute) coAttr
                                .copy()).getValue());
                        logger.finer(coAttr.getValue().getClass().toString());
                    } else {
                        // for edges, add the coordinates of their bends
                        List<Node> selNodes = selection.getNodes();

                        for (Node node : selNodes) {
                            for (Iterator<Edge> eit = node.getEdgesIterator(); eit
                                    .hasNext();) {
                                Edge edge = eit.next();

                                if (selNodes.contains(edge.getSource())
                                        && selNodes.contains(edge.getTarget())) {
                                    SortedCollectionAttribute bends = (SortedCollectionAttribute) edge
                                            .getAttribute(GraphicAttributeConstants.GRAPHICS
                                                    + Attribute.SEPARATOR
                                                    + GraphicAttributeConstants.BENDS);

                                    // TODO:fix finally the access to the
                                    // attribute values
                                    // over the getValue().
                                    // It is currently only a temporary solution
                                    // for nonfixed
                                    // access.
                                    originalCoordinates.put(bends,
                                            ((Attribute) bends.copy())
                                                    .getValue());
                                }
                            }
                        }
                    }
                }

                edit = new ChangeAttributesEdit(originalCoordinates, geMap);
                undoSupport.postEdit(edit);
            }

            // move
            NodeComponent nodecomp = (NodeComponent) lastPressed;

            CoordinateAttribute coord = (CoordinateAttribute) nodecomp
                    .getGraphElement().getAttribute(
                            GraphicAttributeConstants.GRAPHICS
                                    + Attribute.SEPARATOR
                                    + GraphicAttributeConstants.COORDINATE);

            // calculated moved distance
            double deltaX = e.getPoint().getX() - coord.getCoordinate().getX()
                    + lastPressedPointRel.getX();
            double deltaY = e.getPoint().getY() - coord.getCoordinate().getY()
                    + lastPressedPointRel.getY();

            // save max values (for autoscroll)
            double maxX = 0;
            double maxY = 0;

            for (Node node : selection.getNodes()) {
                coord = (CoordinateAttribute) node
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.COORDINATE);

                Point2D coordPt = coord.getCoordinate();
                double newX = coordPt.getX() + deltaX;
                double newY = coordPt.getY() + deltaY;
                coord.setCoordinate(new Point2D.Double(newX, newY));

                if (maxX < newX) {
                    maxX = newX;
                }

                if (maxY < newY) {
                    maxY = newY;
                }
            }

            Set<CoordinateAttribute> bendsCoordsSet = new HashSet<CoordinateAttribute>();
            List<Node> selNodes = selection.getNodes();

            for (Node node : selection.getNodes()) {
                for (Iterator<Edge> eit = node.getEdgesIterator(); eit
                        .hasNext();) {
                    Edge edge = eit.next();

                    if (selNodes.contains(edge.getSource())
                            && selNodes.contains(edge.getTarget())) {
                        addBends(bendsCoordsSet, edge);
                    }
                }
            }

            for (Iterator<CoordinateAttribute> it = bendsCoordsSet.iterator(); it
                    .hasNext();) {
                coord = it.next();

                Point2D coordPt = coord.getCoordinate();
                double newX = coordPt.getX() + deltaX;
                double newY = coordPt.getY() + deltaY;
                coord.setCoordinate(new Point2D.Double(newX, newY));

                if (maxX < newX) {
                    maxX = newX;
                }

                if (maxY < newY) {
                    maxY = newY;
                }
            }

            // update view scrolling
            View view = (View) e.getComponent();
            view.autoscroll(new Point((int) maxX, (int) maxY));

            e.getComponent().repaint();
        }

        dragged = true;

        // else do nothing with labels and other attributecomponents
    }

    /**
     * Invoked when the mouse button has been pressed.
     * 
     * @param e
     *            the mouse event
     */
    public void mousePressed(MouseEvent e) {
        logger.info("MOUSE PRESSED");

        if (!SwingUtilities.isLeftMouseButton(e))
            return;

        dragged = false;
        tempSelectedComps = new LinkedList<GraphElementComponent>();

        Component src = this.findComponentAt(e);

        if (src != null) {
            if (src instanceof View) {
                lastBendHit = null;

                Point2D coord;

                for (Iterator<Edge> edgeIt = this.session.getGraph()
                        .getEdgesIterator(); edgeIt.hasNext();) {
                    Edge edge = edgeIt.next();
                    SortedCollectionAttribute bendsColl = (SortedCollectionAttribute) edge
                            .getAttribute(GraphicAttributeConstants.BENDS_PATH);

                    for (Attribute attribute : bendsColl.getCollection()
                            .values()) {
                        CoordinateAttribute coordAttr = (CoordinateAttribute) attribute;
                        coord = coordAttr.getCoordinate();

                        if (hit(e.getPoint(), coord)) {
                            lastBendHit = coordAttr;
                            lastPressedPointRel = new Point2D.Double(coord
                                    .getX()
                                    - e.getPoint().getX(), coord.getY()
                                    - e.getPoint().getY());

                            break;
                        }
                    }

                    if (lastBendHit != null) {
                        break;
                    }
                }

                if (lastBendHit == null) {
                    if (!isControlDown(e)) {
                        unmarkAll();
                        fireSelectionChanged();
                    }
                }

                lastPressedPoint = e.getPoint();
            } else if (src instanceof NodeComponent) {
                NodeComponent nodecomp = (NodeComponent) src;
                mark(nodecomp, isControlDown(e), this);
                fireSelectionChanged();

                Point2D coordPt = ((CoordinateAttribute) (nodecomp
                        .getGraphElement()
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.COORDINATE)))
                        .getCoordinate();
                lastPressedPointRel = new Point2D.Double(coordPt.getX()
                        - e.getPoint().getX(), coordPt.getY()
                        - e.getPoint().getY());
                lastPressedPoint = null;
                lastPressed = nodecomp;
                lastBendHit = null;
            } else if (src instanceof EdgeComponent) {
                mark((EdgeComponent) src, isControlDown(e), this);
                fireSelectionChanged();
                lastPressedPoint = null;
                lastPressed = src;

                EdgeComponent edgeComp = (EdgeComponent) src;
                Edge edge = (Edge) edgeComp.getGraphElement();
                SortedCollectionAttribute bendsColl = (SortedCollectionAttribute) edge
                        .getAttribute(GraphicAttributeConstants.BENDS_PATH);
                Point2D coord;
                lastBendHit = null;

                for (Attribute attribute : bendsColl.getCollection().values()) {
                    CoordinateAttribute coordAttr = (CoordinateAttribute) attribute;
                    coord = coordAttr.getCoordinate();

                    if (hit(e.getPoint(), coord)) {
                        lastBendHit = coordAttr;
                        lastPressedPointRel = new Point2D.Double(coord.getX()
                                - e.getPoint().getX(), coord.getY()
                                - e.getPoint().getY());

                        break;
                    }
                }
            }

            // ignore rest
        }

        ((Component) e.getSource()).repaint();
    }

    /**
     * Invoked when the mouse button has been released inside the editor panel
     * and handles what has to happen.
     * 
     * @param e
     *            the mouse event
     */
    public void mouseReleased(MouseEvent e) {
        logger.info("MOUSE RELEASED");

        if (!SwingUtilities.isLeftMouseButton(e))
            return;

        super.mouseReleased(e);

        if (dragged && (lastPressedPoint != null) && (lastBendHit == null)) {
            // if selection rectangle has been drawn
            if (!isControlDown(e)) {
                unmarkAll();
            }

            markInRectangle(e, lastPressedPoint, lastPressedPoint2);
            lastPressedPoint = null;
            fireSelectionChanged();
            ((Container) e.getSource()).repaint();
        }

        originalCoordinates = null;
    }

    /**
     * Resets the tool to initial values.
     */
    public void reset() {
        lastPressedPoint = null;
    }

    /**
     * Adds all coordinates of bends of an edge into a set.
     * 
     * @param set
     *            The set where the bends are put.
     * @param edge
     *            The bends of this edge are processed.
     */
    protected void addBends(Set<CoordinateAttribute> set, Edge edge) {
        SortedCollectionAttribute bends = (SortedCollectionAttribute) edge
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR + GraphicAttributeConstants.BENDS);

        for (Attribute attribute : bends.getCollection().values()) {
            set.add((CoordinateAttribute) attribute);
        }
    }

    /**
     * Highlights all nodes / edges that lie entirely inside the rectangle given
     * by the two points. Those two points must already be zoomed.
     * 
     * @param comp
     *            mouseEvent whose source is searched
     * @param p1
     *            first corner of the rectangle
     * @param p2
     *            second corner of the rectangle
     */
    private void displayAsMarkedInRectangle(JComponent comp, Point2D p1,
            Point2D p2) {
        selRect.setFrameFromDiagonal(p1, p2);

        unDisplayAsMarked(tempSelectedComps);

        Component[] allComps = comp.getComponents();

        for (int i = 0; i < allComps.length; i++) {
            if (!selectedContain(allComps[i])
                    && selRect.contains(allComps[i].getBounds())
                    && allComps[i] instanceof GraphElementComponent) {
                this.highlight(allComps[i]);
                tempSelectedComps.add((GraphElementComponent) allComps[i]);
            }
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param pnt1
     *            DOCUMENT ME!
     * @param pnt2
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private boolean hit(Point2D pnt1, Point2D pnt2) {
        if ((Math.abs(pnt1.getX() - pnt2.getX()) < EdgeShape.CLICK_TOLERANCE)
                && (Math.abs(pnt1.getY() - pnt2.getY()) < EdgeShape.CLICK_TOLERANCE))
            return true;
        else
            return false;
    }

    /**
     * Marks all nodes / edges that lie entirely inside the rectangle given by
     * the two points. Uses me.getSource().
     * 
     * @param me
     *            mouseEvent whose source is searched
     * @param p1
     *            first corner of the rectangle
     * @param p2
     *            second corner of the rectangle
     */
    private void markInRectangle(MouseEvent me, Point2D p1, Point2D p2) {
        Rectangle selRect = new Rectangle();
        selRect.setFrameFromDiagonal(p1, p2);

        Component[] allComps = ((JComponent) me.getSource()).getComponents();

        // probably could leave that out ... :
        if (!isControlDown(me)) {
            selection.clear();
        }

        for (int i = 0; i < allComps.length; i++) {
            if (selRect.contains(allComps[i].getBounds())
                    && allComps[i] instanceof GraphElementComponent) {
                mark((GraphElementComponent) allComps[i], true, this);
            }
        }
    }

    /**
     * Draws a rectangle on the given graphics context.
     * 
     * @param comp
     *            context to draw upon
     * @param p1
     *            first corner of the rectangle
     * @param p2
     *            second corner of the rectangle
     */
    private void paintSelectionRectangle(JComponent comp, Point2D p1, Point2D p2) {
        int tlx;
        int tly;
        int w;
        int h;
        tlx = (int) Math.min(p1.getX(), p2.getX());
        tly = (int) Math.min(p1.getY(), p2.getY());
        w = (int) Math.abs(p1.getX() - p2.getX());
        h = (int) Math.abs(p1.getY() - p2.getY());

        Graphics g = comp.getGraphics();
        comp.paintImmediately(comp.getVisibleRect());

        g.drawRect(tlx, tly, w, h);

        if (dynamicSelectionRect) {
            displayAsMarkedInRectangle(comp, p1, p2);
        }

        comp.revalidate();
    }

    // next lines to test whether different tool may display marked elements
    // differently:
    //
    // private final LineBorder border =
    // new LineBorder(java.awt.Color.GREEN, 4);
    //
    // public void displayAsMarked(GraphElementComponent comp) {
    // if (comp instanceof NodeComponent) {
    // displayAsMarked((NodeComponent) comp);
    // } else {
    // displayAsMarked((EdgeComponent) comp);
    // }
    // }
    //    
    // public void displayAsMarked(NodeComponent comp) {
    // if (comp != null) ((JComponent)comp).setBorder(border);
    // }
    //    
    // public void displayAsMarked(EdgeComponent comp) {
    // if (comp != null) {
    // ((JComponent)comp).setBorder(
    // new EdgeBorder(java.awt.Color.GREEN, 7, true));
    // }
    // }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
