/*==============================================================================
*
*   SugiyamaDrawingStyle.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: SugiyamaDrawingStyle.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.*;

import org.visnacom.model.*;
import org.visnacom.model.ActionExpand.Mapping;
import org.visnacom.sugiyama.algorithm.*;
import org.visnacom.sugiyama.eval.Profiler;
import org.visnacom.sugiyama.model.*;
import org.visnacom.view.*;


/**
 * main interface to the framework
 */
public class SugiyamaDrawingStyle extends DrawingStyle {
    //~ Static fields/initializers =============================================

    public static final int DEBUG_STYLE = 0;
    public static final int FINAL_STYLE = 1;
    public static final int EDITOR_STYLE = 2;

    //~ Instance fields ========================================================

    /** the internal compound graph, is public for testing only */
    public SugiCompoundGraph s;

    /**
     * this flag is used by the tests to evaluate the updateschema as layout
     * creation algorithm
     */
    private boolean supressMetricalLayoutAtExpand = false;

    //indicates during metric layout the parameters to use.
    private int drawingStyle = EDITOR_STYLE;

    //~ Constructors ===========================================================

    /**
     * only for debug purposes. can be combined with calls to drawImpl() ...
     *
     * @param c DOCUMENT ME!
     * @param style DOCUMENT ME!
     */
    public SugiyamaDrawingStyle(CompoundGraph c, int style) {
        super(null);
        s = new SugiCompoundGraph(c);
        s.setDrawingStyle(style);
    }

    /**
     * Creates a new SugiyamaDrawingStyle object. Is the usual constructor.
     *
     * @param geometry DOCUMENT ME!
     */
    public SugiyamaDrawingStyle(Geometry geometry) {
        super(geometry);
    }

    /**
     * Creates a new SugiyamaDrawingStyle object.
     *
     * @param geometry
     * @param drawing_style the wished drawing style.
     */
    public SugiyamaDrawingStyle(Geometry geometry, int drawing_style) {
        this(geometry);
        setDrawingStyle(drawing_style);
    }

    //~ Methods ================================================================

    /**
     * sets the wished drawing style. will be used in metric layout
     *
     * @param drawingStyle DOCUMENT ME!
     */
    public void setDrawingStyle(int drawingStyle) {
        this.drawingStyle = drawingStyle;
    }

    /**
     * DOCUMENT ME!
     *
     * @return the current drawing style
     */
    public int getDrawingStyle() {
        return drawingStyle;
    }

    /**
     * DOCUMENT ME!
     *
     * @return the number of edges, that are in the internal sugicompoundgraph
     */
    public int getInternalNumEdges() {
        return s.getNumOfEdges();
    }

    /**
     * DOCUMENT ME!
     *
     * @return the number of nodes, that are in the internal sugicompoundgraph
     */
    public int getInternalNumOfNodes() {
        return s.getNumOfNodes();
    }

    /**
     * @see org.visnacom.view.DrawingStyle#collapse(org.visnacom.model.Node, org.visnacom.view.Geometry)
     */
    public void contract(Node clus, Geometry newGeo, ActionContract action) {
        assert clus == action.getAffected();
        assert geometry.getView() == newGeo.getView();

        CompoundGraph view = newGeo.getView();
        contractImpl(view, action);
        transferLayout(newGeo);

        assert geometry != null && geometry != newGeo;

        /*if collapse is called and not collapseImpl, then geo must not be null
         * and newGeo has to be a clone.
         */
        prepareEdgesForAnimation(newGeo, action);
    }

    /**
     * is usually called by contract. can be used directly for testing.
     *
     * @param c DOCUMENT ME!
     * @param action DOCUMENT ME!
     */
    public void contractImpl(CompoundGraph c, ActionContract action) {
        testConsistence(c, action);

        Node origV = (Node) action.getAffected();

        SugiNode v = s.getCorrespondingNode(origV);

        assert v != s.getRoot();
        assert s.checkLHs(2);

        ContractNormalization.contract(s, action, v);

        /*delete children of v*/
        v.setLHactive(false);

        s.setMappingAutomatic(true); //for automatic deleting the edgemappings

        //works on purpose with clone of children list
        for(Iterator it = s.getChildren(v).iterator(); it.hasNext();) {
            SugiNode child = (SugiNode) it.next();
            s.deleteLeaf(child);
        }

        s.setMappingAutomatic(false);

        //bring the LH into state, where it should be
        v.setLHactive(true);
        v.getLocalHierarchy().discardHorizontalEdges();

        assert s.checkEdgeMappings(1, true);
        assert s.checkLHs(2, false);

        UpdateMetricLayout.contract(s, v);
    }

    /**
     * should be called after fully expanding, if supressMetricaLayoutAtExpand
     * was called before. used in evaluation.
     *
     * @param geo DOCUMENT ME!
     */
    public void doMetricLayoutAfterExpand(Geometry geo) {
        supressMetricalLayoutAtExpand = false;
        MetricLayout.layout(s);
        transferLayout(geo);
    }

    /**
     * @see org.visnacom.view.DrawingStyle#draw(org.visnacom.view.Geometry)
     */
    public void draw(Geometry newGeo) {
        Profiler.start(Profiler.TIME_STATIC_LAYOUT);
        assert geometry.getView() == newGeo.getView();

        s = new SugiCompoundGraph(newGeo.getView());
        s.setDrawingStyle(drawingStyle);
        s.setPreferences(newGeo.getPrefs());
        drawImpl();
        //show();
        transferLayout(newGeo);
        notToBeContracted.clear();
        setNoContract();
        Profiler.stop(Profiler.TIME_STATIC_LAYOUT);
    }

    /**
     * Is public for debug purposes only. usually called by draw.
     */
    public void drawImpl() {
        //System.out.println("begin of static layout");
        assert s != null;

        Profiler.start(Profiler.TIME_HIERARCHIZATION);
        Hierarchization.hierarchize(s);
        Profiler.stop(Profiler.TIME_HIERARCHIZATION);

        Profiler.start(Profiler.TIME_NORMALIZATION);
        Normalization.normalize(s);
        Profiler.stop(Profiler.TIME_NORMALIZATION);

        if(geometry != null) {
            Profiler.set(Profiler.NUM_DUMMY_NODES,
                new Integer(getInternalNumOfNodes()
                    - geometry.getView().getNumOfNodes()));
        }

        Profiler.set(Profiler.NUM_EDGE_SEGMENTS,
            new Integer(getInternalNumEdges()));

        Profiler.start(Profiler.TIME_VERTEX_ORDERING);
        VertexOrdering.order(s);
        Profiler.stop(Profiler.TIME_VERTEX_ORDERING);

        Profiler.start(Profiler.TIME_METRICAL_LAYOUT);
        MetricLayout.layout(s);
        Profiler.stop(Profiler.TIME_METRICAL_LAYOUT);

        Profiler.set(Profiler.AREA_WIDTH,
            new Integer(s.getMetricRoot().getWidth()));
        Profiler.set(Profiler.AREA_HEIGHT,
            new Integer(s.getMetricRoot().getHeight()));

        //all nodes that are present in static layout, cannot be contracted
        //this is actually redundant now, as the framework offers in its final
        //implemenation a mechanism to prevent wrong contracts.
        //Is not deleted for robustness.
        for(Iterator it = s.getAllNodesIterator(); it.hasNext();) {
            ((SugiNode) it.next()).setContractAllowed(false);
        }
    }

    /**
     * @see org.visnacom.view.DrawingStyle#expand(org.visnacom.model.Node, org.visnacom.view.Geometry,
     *      org.visnacom.model.ActionExpand)
     */
    public void expand(Node clus, Geometry newGeo, ActionExpand action) {
        assert clus == action.getAffected();
        assert clus == action.v;
        assert geometry != null && geometry != newGeo;
        assert geometry.getView() == newGeo.getView();

        Profiler.start(Profiler.TIME_SINGLE_EXPAND);

        CompoundGraph view = newGeo.getView();
        expandImpl(view, action);
        transferLayout(newGeo);

        prepareEdgesForAnimation(newGeo, action);
    }

    /**
     * implementation of expand, is independent of the whole view stuff. Is
     * public for debug purposes only. the parameters have to fit together.
     * usually called by expand
     *
     * @param c the original compound graph
     * @param action the expand action
     */
    public void expandImpl(CompoundGraph c, ActionExpand action) {
        testConsistence(c, action);

        Node origV = (Node) action.getAffected();
        SugiActionExpand myAction = new SugiActionExpand(s);
        myAction.origAction = action;
        myAction.v = s.getCorrespondingNode(origV);

        assert myAction.v != s.getRoot();
        assert s.checkLHs(2);
        myAction.v.setLHactive(false); /*like in static layout, the localhierarchy
           cannot be created before hierarchization and normalization
         * the difference is that only LH(v) is deactivated, not all LHs.
         * It will be created in one shot at VertexOrdering */

        ((SugiNode) s.getParent(myAction.v)).getLocalHierarchy()
         .reactivateHorizontalEdges();
        assert c.getAllNodes().contains(origV);

        if(!c.hasChildren(origV)) {
            return;
        }

        //insert children(v)
        for(Iterator it = c.getChildrenIterator(origV); it.hasNext();) {
            Node n = (Node) it.next();
            assert !s.getAllNodes().contains(n);
            s.transferLeaf(n, origV);
        }

        //the old contracted edges are not deleted here
        //the new expanded edges are not inserted here
        //only internal edges of children(v) are inserted
        for(Iterator it = action.contDerEdges.iterator(); it.hasNext();) {
            Edge e = (Edge) it.next();
            assert c.getChildren(origV).contains(e.getSource());
            assert c.getChildren(origV).contains(e.getTarget());
            myAction.internalEdges.add(s.transferEdge(e));
        }

        assert s.hasChildren(myAction.v);
        Hierarchization.expand(myAction);
        //at this point, some internal edges might be reversed, but the references
        //are still valid
        ExpandNormalization.expand(myAction);
        ExpandVertexOrdering.expand(myAction);
        if(!supressMetricalLayoutAtExpand) {
            UpdateMetricLayout.expand(myAction);
        }

        myAction.v.setContractAllowed(true);
        assert s.checkLHs(2, false);
    }

    /**
     * computes end-coordinates for an edge at the bottom of a node
     *
     * @param rect the incident node's rectangle
     *
     * @return the endpoint for the edge
     */
    public static Point lowerPoint(Rectangle rect) {
        return lowerPoint(rect.x, rect.y, rect.width, rect.height);
    }

    /**
     * computes end-coordinates for an edge at the bottom of a node
     *
     * @param n DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Point lowerPoint(SugiNode n) {
        return lowerPoint(n.getAbsoluteX(), n.getAbsoluteY(), n.getWidth(),
            n.getHeight());
    }

    /**
     * with this method, one can supress the metric layout at expanding. is
     * used in evaluation.
     */
    public void supressMetricLayoutAtExpand() {
        supressMetricalLayoutAtExpand = true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return s.toString();
    }

    /**
     * computes end-coordinates for an edge at the top of a node
     *
     * @param rect DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Point upperPoint(Rectangle rect) {
        return upperPoint(rect.x, rect.y, rect.width, rect.height);
    }

    /**
     * computes end-coordinates for an edge at the top of a node
     *
     * @param n DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Point upperPoint(SugiNode n) {
        return upperPoint(n.getAbsoluteX(), n.getAbsoluteY(), n.getWidth(),
            n.getHeight());
    }

    /**
     * determines whether the representation of an edge in the original
     * compound graph has been reversed
     *
     * @param s a sugi compound graph
     * @param origE the originalEdge of the corresponding compound graph
     *
     * @return true, if the representation of the edge has been reversed in the
     *         given sugi compoundgraph
     */
    private static boolean isEdgeReversed(SugiCompoundGraph s, Edge origE) {
        Node origSource = s.getCorrespondingNode(origE.getSource());
        List edgePath = s.getCorrespondingEdges(origE);
        SugiEdge firstSegment = (SugiEdge) edgePath.get(0);
        if(firstSegment.getSource() == origSource) {
            assert ((Edge) edgePath.get(edgePath.size() - 1)).getTarget() == s
            .getCorrespondingNode(origE.getTarget());
            return false;
        } else {
            assert ((Edge) edgePath.get(edgePath.size() - 1)).getTarget() == origSource;
            assert firstSegment.getSource() == s.getCorrespondingNode(origE
                .getTarget());
            return true;
        }
    }

    /**
     * registers all nodes that cannot be contracted in the appropriate list
     * offered by the framework
     */
    private void setNoContract() {
        View v = geometry.getView();
        Iterator it = v.getAllNodesIterator();
        while(it.hasNext()) {
            Node n = (Node) it.next();
            if(v.isCluster(n) && !v.isContracted(n)) {
                notToBeContracted.add(n);
            }
        }
    }

    /**
     * traverses the connected edges along the dummy node path from up to down
     * and calculates the controlpoints for the polyline
     *
     * @param pl the polyline to fill with controlpoints
     * @param edgePath a list of edges corresponding to a single edge in the
     *        original compoundgraph
     */
    private static void constructPathDown(Polyline pl, List edgePath) {
        assert edgePath.size() > 0;

        Iterator it = edgePath.iterator();
        SugiEdge edge = (SugiEdge) it.next();
        SugiNode source;
        SugiNode target = (SugiNode) edge.getTarget();
        Point currentP = new Point(0, 0);
        while(target.isDummyNode()) {
            Point possibleP = upperPoint(target);
            if(!currentP.equals(possibleP)) {
                pl.addControl(possibleP);
                currentP = possibleP;
            } else {
                assert false; //two dummy nodes would crash
            }

            edge = (SugiEdge) it.next();
            source = (SugiNode) edge.getSource();
            target = (SugiNode) edge.getTarget();
            possibleP = lowerPoint(source);
            if(!currentP.equals(possibleP)) {
                pl.addControl(possibleP);
                currentP = possibleP;
            } else {
                
                /* even if the control points have identical coordinates at the 
                 * moment, they are not superfluous.
                 * At a later time after some expand operation, the controlpoints
                 * can drift apart */
                pl.addControl(possibleP);
                currentP = possibleP;
            }
        }
    }

    /**
     * traverses the connected edges along the dummy node path from lower to
     * higher levels and calculates the controlpoints for the polyline
     *
     * @param pl DOCUMENT ME!
     * @param edgePath DOCUMENT ME!
     */
    private static void constructPathUp(Polyline pl, List edgePath) {
        assert edgePath.size() > 0;

        ListIterator it = edgePath.listIterator(edgePath.size());
        SugiEdge nextEdge = (SugiEdge) it.previous();
        SugiNode source = (SugiNode) nextEdge.getSource();
        SugiNode target;
        Point currentP = new Point(0, 0);
        while(source.isDummyNode()) {
            Point possibleP = lowerPoint(source);
            if(!currentP.equals(possibleP)) {
                pl.addControl(possibleP);
                currentP = possibleP;
            }

            nextEdge = (SugiEdge) it.previous();
            source = (SugiNode) nextEdge.getSource();
            target = (SugiNode) nextEdge.getTarget();
            possibleP = upperPoint(target);
            if(!currentP.equals(possibleP)) {
                pl.addControl(possibleP);
                currentP = possibleP;
            } else {
                /*06.09.2005: see constructPathDown for explanation */
                pl.addControl(possibleP);
                currentP = possibleP;
            }
        }
    }

    /**
     * computes end-coordinates for an edge at the bottom of a node
     *
     * @param x DOCUMENT ME!
     * @param y DOCUMENT ME!
     * @param width DOCUMENT ME!
     * @param height DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static Point lowerPoint(int x, int y, int width, int height) {
        return new Point(x + width / 2, y + height);
    }

    /**
     * some consistency test
     *
     * @param c DOCUMENT ME!
     * @param e DOCUMENT ME!
     *
     * @throws IllegalArgumentException DOCUMENT ME!
     */
    private void checkEdge(CompoundGraph c, Edge e) {
        if(!c.containsEdge(e)) {
            throw new IllegalArgumentException("edge " + e
                + " was not in graph");
        }
    }

    /**
     * performs all necessary operations before the start of the animation as 
     * described in the thesis.
     * 
     * mainly corrects the number of control points of corresponding polylines.
     * 
     * @param newGeo DOCUMENT ME!
     * @param action DOCUMENT ME!
     */
    private void prepareEdgesForAnimation(Geometry newGeo, ActionContract action) {
        for(Iterator it = action.getMappinsIterator(); it.hasNext();) {
            org.visnacom.model.ActionContract.Mapping m =
                (ActionContract.Mapping) it.next();
            Polyline poly = geometry.shape(m.newEdge);
            Polyline newPoly = newGeo.shape(m.newEdge);

            /*
             * typically, two controlpoints are superfluent.
             * it is possible, that it is only one.
             * the points to remove are choosen near v.
             */
            boolean removeFirst;

            //edge is from v to w or from w to v
            if(action.getAffected() == m.newEdge.getSource()) {
                removeFirst = true;
            } else {
                removeFirst = false;
            }

            int numPoints =
                poly.getControlPoints().size()
                - newPoly.getControlPoints().size();
            for(int i = 1; i <= numPoints; i++) {
                if(removeFirst) {
                    poly.delFirstCtrlPoint();
                } else {
                    poly.delLastCtrlPoint();
                }
            }

            assert newPoly.getControlPoints().size() == poly.getControlPoints()
                                                            .size();
            assert numPoints == 1 || numPoints == 2;
        }
    }

    /**
     * adds two controlpoints to the initial polylines of new edges, so that
     * they match the lengths of the polylines in final drawing
     *
     * @param newGeo DOCUMENT ME!
     * @param action DOCUMENT ME!
     */
    private void prepareEdgesForAnimation(Geometry newGeo, ActionExpand action) {
        for(Iterator it = action.getMappinsIterator(); it.hasNext();) {
            Mapping m = (Mapping) it.next();
            for(Iterator it2 = m.newEdges.iterator(); it2.hasNext();) {
                Edge e = (Edge) it2.next();

                //that is the polyline of m.oldEdge
                Polyline polyE = geometry.shape(e);

                //that is the target polyline
                Polyline polyENew = newGeo.shape(e);

                assert geometry.shape((Node) action.getAffected()).equals(polyE
                    .getStart())
                || geometry.shape((Node) action.getAffected()).equals(polyE
                    .getEnd());
                assert geometry.shape(m.oldEdge.getTarget()).equals(polyE
                    .getEnd());
                assert geometry.shape(m.oldEdge.getSource()).equals(polyE
                    .getStart());
                assert newGeo.shape(e.getTarget()).equals(polyENew.getEnd());
                assert newGeo.shape(e.getSource()).equals(polyENew.getStart());

                /* add two control points to match target polyline */

                //there are four cases:
                //edge is from v to w or from w to v
                //edge from up to down or from down to up
                int numNewPs;
                if(action.getAffected() == m.oldEdge.getSource()) {
                    //case "from v to w"
                    Point p;
                    if(polyE.getStart().y < polyE.getEnd().y) {
                        //case "from up to down"
                        p = lowerPoint(polyE.getStart());
                    } else {
                        //case "from down to up"
                        p = upperPoint(polyE.getStart());
                    }

                    numNewPs =
                        polyENew.getControlPoints().size()
                        - polyE.getControlPoints().size();
                    for(int i = 1; i <= numNewPs; i++) {
                        polyE.addFirstCtrlPoint(p);
                    }
                } else {
                    //case "from w to v"
                    Point p;
                    if(polyE.getEnd().y < polyE.getStart().y) {
                        p = lowerPoint(polyE.getEnd());
                    } else {
                        p = upperPoint(polyE.getEnd());
                    }

                    numNewPs =
                        polyENew.getControlPoints().size()
                        - polyE.getControlPoints().size();
                    for(int i = 1; i <= numNewPs; i++) {
                        polyE.addControl(p);
                    }
                }

                assert polyENew.getControlPoints().size() == polyE.getControlPoints()
                                                                  .size();
                assert numNewPs == 1 || numNewPs == 2;
            }
        }

        /* internal edges */
        for(Iterator it = action.contDerEdges.iterator(); it.hasNext();) {
            Edge e = (Edge) it.next();
            Polyline originalP = geometry.shape(e);
            Polyline tempP = newGeo.shape(e);
            assert newGeo.shape(e.getSource()).equals(tempP.getStart());
            assert newGeo.shape(e.getTarget()).equals(tempP.getEnd());
            assert geometry.shape(e.getSource()).equals(originalP.getStart());
            assert geometry.shape(e.getTarget()).equals(originalP.getEnd());
            assert originalP.getStart().equals(originalP.getEnd());

            Rectangle start = originalP.getStart();
            Point p =
                new Point((int) start.getCenterX(), (int) start.getCenterY());
            int numNewPs =
                tempP.getControlPoints().size()
                - originalP.getControlPoints().size();
            for(int i = 1; i <= numNewPs; i++) {
                originalP.addControl(new Point(p));
            }

            assert originalP.getControlPoints().size() == tempP.getControlPoints()
                                                               .size();
        }
    }

    /**
     * computes end-coordinates for an edge at the top of a node
     *
     * @param x DOCUMENT ME!
     * @param y DOCUMENT ME!
     * @param width DOCUMENT ME!
     * @param height DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static Point upperPoint(int x, int y, int width, int height) {
        return new Point(x + width / 2, y);
    }

    /**
     * some consistency check
     *
     * @param c DOCUMENT ME!
     * @param action DOCUMENT ME!
     *
     * @throws IllegalArgumentException DOCUMENT ME!
     */
    private void testConsistence(CompoundGraph c, ActionExpand action) {
        if(s == null) {
            throw new IllegalArgumentException(
                "graph has not been drawn with Sugiyama");
        }

        for(Iterator it = c.getAllNodesIterator(); it.hasNext();) {
            Node n = (Node) it.next();

            //throws exception, if no corresponding node exists
            if(s.getCorrespondingNode(n) == null
                && c.getParent(n) != action.getAffected()) {
                throw new IllegalArgumentException("unknown node" + n);
            }
        }

        for(Iterator it = c.getAllEdgesIterator(); it.hasNext();) {
            Edge e = (Edge) it.next();

            //thows exception, if no corresponding edge path exists.
            List path = s.getCorrespondingEdges(e);
            if(path == null
                && (action.getAffected() != c.getParent(e.getSource())
                && (action.getAffected() != c.getParent(e.getTarget())))) {
                throw new IllegalArgumentException("unknown edge" + e);
            }
        }

        for(Iterator it = action.getMappinsIterator(); it.hasNext();) {
            Mapping m = (Mapping) it.next();
            if(c.containsEdge(m.oldEdge) == m.oldEdgeDeleted) {
                throw new IllegalArgumentException("edge " + m.oldEdge
                    + "was inconsistent");
            }

            if(m.newEdges.isEmpty()) {
                throw new IllegalArgumentException("Mapping: (" + m
                    + ") is inconsistent");
            }

            for(Iterator it2 = m.newEdges.iterator(); it2.hasNext();) {
                checkEdge(c, (Edge) it2.next());
            }
        }

        for(Iterator it = action.contDerEdges.iterator(); it.hasNext();) {
            checkEdge(c, (Edge) it.next());
        }
    }

    /**
     * tests the given action object for consistence with the given
     * compoundgraph. not completely sufficient.
     *
     * @param c DOCUMENT ME!
     * @param action DOCUMENT ME!
     *
     * @throws IllegalArgumentException DOCUMENT ME!
     */
    private void testConsistence(CompoundGraph c, ActionContract action) {
        if(s == null) {
            throw new IllegalArgumentException(
                "graph has not been drawn with Sugiyama");
        }

        if(!s.getCorrespondingNode((Node) action.getAffected())
             .isContractAllowed()) {
            throw new IllegalArgumentException(
                "node has not been expanded before");
        }

        if(!c.getAllNodes().contains(action.getAffected())) {
            throw new IllegalArgumentException("action object is inconsistent");
        }

        if(c.hasChildren((Node) action.getAffected())) {
            throw new IllegalArgumentException("action object is inconsistent");
        }

        SugiNode v = s.getCorrespondingNode((Node) action.getAffected());
        for(Iterator it = s.getChildrenIterator(v); it.hasNext();) {
            if(s.hasChildren((Node) it.next())) {
                throw new IllegalArgumentException(
                    "action object is inconsistent");
            }
        }

        for(Iterator it = action.getMappinsIterator(); it.hasNext();) {
            ActionContract.Mapping m = (ActionContract.Mapping) it.next();
            List edges =
                c.getEdge(m.newEdge.getSource(), m.newEdge.getTarget());

            if(!edges.contains(m.newEdge)
                || !(m.newEdge.getSource() == action.getAffected()
                && c.getAllNodes().contains(m.newEdge.getTarget())
                || m.newEdge.getTarget() == action.getAffected()
                && c.getAllNodes().contains(m.newEdge.getSource()))) {
                throw new IllegalArgumentException(
                    "action object is inconsistent");
            }

            for(Iterator it2 = m.oldEdges.iterator(); it2.hasNext();) {
                Edge olde = (Edge) it2.next();
                if(m.newEdge.getSource() == action.getAffected()) {
                    if(olde.getTarget() != m.newEdge.getTarget()) {
                        throw new IllegalArgumentException(
                            "action object is inconsistent");
                    }

                    if(c.getAllNodes().contains(olde.getSource())) {
                        throw new IllegalArgumentException(
                            "action object is inconsistent");
                    }

                    if(!(s.isAncestor(s.getCorrespondingNode(
                                (Node) action.getAffected()),
                            s.getCorrespondingNode(olde.getSource())))) {
                        throw new IllegalArgumentException(
                            "action object is inconsistent");
                    }
                } else {
                    if(!(olde.getSource() == m.newEdge.getSource())) {
                        throw new IllegalArgumentException(
                            "action object is inconsistent");
                    }

                    if(c.getAllNodes().contains(olde.getTarget())) {
                        throw new IllegalArgumentException(
                            "action object is inconsistent");
                    }

                    if(!s.isAncestor(s.getCorrespondingNode(
                                (Node) action.getAffected()),
                            s.getCorrespondingNode(olde.getTarget()))) {
                        throw new IllegalArgumentException(
                            "action object is inconsistent");
                    }
                }
            }
        }
    }

    /**
     * transfers the computed coordinates from sugi compound graph to geometry
     * object
     * 
     * @param newGeo DOCUMENT ME!
     */
    private void transferLayout(Geometry newGeo) {
        //assign coordinates to nodes
        for(Iterator it = newGeo.getView().getAllNodesIterator(); it.hasNext();) {
            Node n = (Node) it.next();
            SugiNode sn = s.getCorrespondingNode(n);

            Rectangle rect = newGeo.shape(n);
            assert rect != null; //view has a node, that has no entry in geometry 
            rect.x = sn.getAbsoluteX();
            rect.y = sn.getAbsoluteY();
            rect.width = sn.getWidth();
            rect.height = sn.getHeight();
        }

        //assign coordinates to edges
        for(Iterator it = newGeo.getView().getAllEdgesIterator(); it.hasNext();) {
            Edge e = (Edge) it.next();
            Polyline pl = newGeo.shape(e);
            int oldNumControlpoints = pl.getControlPoints().size();
            pl.clearControlPoints();

            List edgePath = s.getCorrespondingEdges(e);
            if(!isEdgeReversed(s, e)) {
                constructPathDown(pl, edgePath);
            } else {
                constructPathUp(pl, edgePath);
            }

            if(pl.getControlPoints().size() != oldNumControlpoints) {
                //System.out.println("Warning: differnent number at " + e);
            }
        }
    }
}
