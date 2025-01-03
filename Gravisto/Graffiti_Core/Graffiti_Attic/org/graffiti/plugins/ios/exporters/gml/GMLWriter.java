// =============================================================================
//
//   GMLWriter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GMLWriter.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.ios.exporters.gml;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.BooleanAttribute;
import org.graffiti.attributes.ByteAttribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.FloatAttribute;
import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.attributes.LongAttribute;
import org.graffiti.attributes.ShortAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.plugin.io.OutputSerializer;

/**
 * Provides a GML writer. This writer does not yet support all GML features yet.
 * See http://infosun.fmi.uni-passau.de/Graphlet/GML/ for more details
 * 
 * @version $Revision: 5772 $
 */
public class GMLWriter implements OutputSerializer {

    /** Prepresents a tabulator. */
    private static final String TAB = "  ";

    /** DOCUMENT ME! */
    private final String eol = System.getProperty("line.separator");

    /**
     * A collection of attribute paths that should not be written explicitly.
     * Sensible e.g. for attributes that have already been given special
     * treatment.
     */
    private Collection<String> dontWriteAttrs;

    /**
     * A map of attributes, which should be written to the stream. This is
     * something like a filter and a mapping from graffiti collection attributes
     * to GML attributes.
     */
    private Map<String, String> attMapping;

    /**
     * A map of collection attributes, which should be written to the stream.
     * This is something like a filter and a mapping from graffiti collection
     * attributes to GML (hierarchial) attributes.
     */
    private Map<String, String> colMapping;

    /**
     * Constructs a new GML writer.
     */
    public GMLWriter() {
        colMapping = new HashMap<String, String>();

        colMapping.put("", ""); // the root collection attribute
        colMapping.put("graphics", "graphics");

        // colMapping.put("label", "label");
        // colMapping.put("LabelGraphics", "LabelGraphics");
        attMapping = new HashMap<String, String>();
        attMapping.put("version", "version");
        // attMapping.put("directed", "directed");
        attMapping.put("x", "x");
        attMapping.put("y", "y");
        attMapping.put("frameThickness", "width");
        attMapping.put("shape", "type");
        attMapping.put("outline", "outline");
        attMapping.put("arrowtail", "arrow");
        attMapping.put("arrowhead", "arrow");

        dontWriteAttrs = new ArrayList<String>(9);
        dontWriteAttrs.add(".graphics.coordinate.x");
        dontWriteAttrs.add(".graphics.coordinate.y");
        dontWriteAttrs.add(".graphics.dimension.width");
        dontWriteAttrs.add(".graphics.dimension.height");
        dontWriteAttrs.add(".graphics.backgroundImage");

        // dontWriteAttrs.add(".graphics.frameThickness");
        // dontWriteAttrs.add(".graphics.shape");
        // dontWriteAttrs.add(".graphics.arrowtail");
        // dontWriteAttrs.add(".graphics.arrowhead");
        dontWriteAttrs.add(".graphics.fillcolor");
        dontWriteAttrs.add(".graphics.framecolor");
        dontWriteAttrs.add(".graphics.bends");
        dontWriteAttrs.add(".id");
    }

    /**
     * @see org.graffiti.plugin.io.Serializer#getExtensions()
     */
    public String[] getExtensions() {
        return new String[] { ".gml" };
    }

    /**
     * @see org.graffiti.plugin.io.OutputSerializer#write(OutputStream, Graph)
     */
    public void write(OutputStream o, Graph g) throws IOException {
        PrintStream p = new PrintStream(o);

        // write the graph's open tag
        p.println("graph [");

        // write the graph
        writeGraph(p, g);

        // write the nodes
        ArrayList<Node> nodeIds = writeNodes(p, g);

        // write the edges
        writeEdges(p, g, nodeIds);

        // write the graph's close tag
        p.println("]");
        p.close();
    }

    /**
     * This method does not actually write the hierarchy to the stream, but
     * stores it into a StringBuffer. That is the means to remove empty and
     * unnecessary sub structures. An example is the coordinate attribute: All
     * sub attributes (x, y) of coordinate have already been written somewhere
     * else. Therefore, the coordinate attribute itself will be empty and can be
     * ommitted. This can be seen only after having checked all sub-
     * attributess, though.
     * 
     * @param a
     *            the collection attribute to get the attribute from.
     * @param level
     *            the indentation level.
     * 
     * @return the sub herarchy starting at Attribute a
     */
    private StringBuffer getWrittenAttributeHierarchy(Attribute a, int level) {
        StringBuffer sb = new StringBuffer();

        if (dontWriteAttrs.contains(a.getPath()))
            return sb;

        if (a instanceof CollectionAttribute) {
            CollectionAttribute c = (CollectionAttribute) a;

            if (c instanceof LabelAttribute) {
                try {
                    sb.append(createTabs(level) + "label \""
                            + ((LabelAttribute) c).getLabel() + "\"");
                    sb.append(eol);
                } catch (AttributeNotFoundException anfe) {
                    warning(anfe.getMessage());
                }

                return sb;
            }

            if (colMapping.containsKey(c.getId())) {
                Map<String, Attribute> m = c.getCollection();

                sb.append(createTabs(level));
                sb.append(c.getId() + " [");
                sb.append(eol);

                // FIXME: remove this HACK
                // add a Graffiti to GML mapping class instead.
                if ("graphics".equals(c.getId())) {
                    try {
                        sb.append(createTabs(level + 1) + "x "
                                + c.getAttribute("coordinate.x").getValue());
                        sb.append(eol);
                    } catch (AttributeNotFoundException anfe) {
                        // warning(anfe.getMessage());
                    }

                    try {
                        sb.append(createTabs(level + 1) + "y "
                                + c.getAttribute("coordinate.y").getValue());
                        sb.append(eol);
                    } catch (AttributeNotFoundException anfe) {
                        // warning(anfe.getMessage());
                    }

                    try {
                        sb.append(createTabs(level + 1) + "w "
                                + c.getAttribute("dimension.width").getValue());
                        sb.append(eol);
                    } catch (AttributeNotFoundException anfe) {
                        // warning(anfe.getMessage());
                    }

                    try {
                        sb
                                .append(createTabs(level + 1)
                                        + "h "
                                        + c.getAttribute("dimension.height")
                                                .getValue());
                        sb.append(eol);
                    } catch (AttributeNotFoundException anfe) {
                        // warning(anfe.getMessage());
                    }

                    try {
                        sb
                                .append(createTabs(level + 1)
                                        + "fill \""
                                        + colToHex(c
                                                .getAttribute(GraphicAttributeConstants.FILLCOLOR))
                                        + "\"");
                        sb.append(eol);
                    } catch (AttributeNotFoundException anfe) {
                        // warning(anfe.getMessage());
                    }

                    try {
                        sb
                                .append(createTabs(level + 1)
                                        + "outline \""
                                        + colToHex(c
                                                .getAttribute(GraphicAttributeConstants.FRAMECOLOR))
                                        + "\"");
                        sb.append(eol);
                    } catch (AttributeNotFoundException anfe) {
                        // warning(anfe.getMessage());
                    }

                    try {
                        SortedCollectionAttribute bends = (SortedCollectionAttribute) c
                                .getAttribute("bends");

                        if (!bends.isEmpty()) {
                            sb.append(createTabs(level + 1) + "Line [" + eol);
                            sb.append(createTabs(level + 2)
                                    + "point [ x 0.0 y 0.0 ]" + eol);

                            for (Attribute attribute : bends.getCollection()
                                    .values()) {
                                try {
                                    CoordinateAttribute coord = (CoordinateAttribute) attribute;
                                    sb.append(createTabs(level + 2)
                                            + "point [ x ");
                                    sb.append(coord.getX());
                                    sb.append(" y ");
                                    sb.append(coord.getY());
                                    sb.append(" ]");
                                    sb.append(eol);
                                } catch (ClassCastException cce) {
                                    // ignore wrong type
                                }
                            }

                            sb.append(createTabs(level + 2)
                                    + "point [ x 0.0 y 0.0 ]" + eol);
                            sb.append(createTabs(level + 1) + "]" + eol);
                        }
                    } catch (AttributeNotFoundException anfe) {
                        // warning(anfe.getMessage());
                    } catch (ClassCastException cce) {
                        // warning(anfe.getMessage());
                    }
                }

                for (String id : m.keySet()) {
                    Attribute subCol = c.getAttribute(id);
                    sb.append(getWrittenAttributeHierarchy(subCol, level + 1));
                }

                sb.append(createTabs(level));
                sb.append("]");
                sb.append(eol);
            } else {
                // warning("did not write: " + c.getPath());
                Map<String, Attribute> m = c.getCollection();

                if (!m.isEmpty()) {
                    StringBuffer sub = new StringBuffer();

                    for (Attribute subCol : m.values()) {
                        sub.append(getWrittenAttributeHierarchy(subCol,
                                level + 1));
                    }

                    if (!"".equals(sub.toString())) {
                        sb.append(createTabs(level));
                        sb.append(c.getId() + " [");
                        sb.append(eol);

                        sb.append(sub);

                        sb.append(createTabs(level));
                        sb.append("]");
                        sb.append(eol);
                    }
                }
            }
        } else if (a instanceof StringAttribute) {
            // FIXME: remove this HACK
            // add a Graffiti to GML mapping class instead.
            if (attMapping.containsKey(a.getId())) {
                if ("shape".equals(a.getId())) {
                    // if("org.graffiti.plugins.views.defaults.StraightLineEdgeShape".equals(
                    // a.getValue()))
                    // {
                    // sb.append("\"line\"");
                    // }
                    // else
                    if ("org.graffiti.plugins.views.defaults.SmoothLineEdgeShape"
                            .equals(a.getValue())) {
                        sb.append(createTabs(level) + "smooth 1" + eol);
                    } else {
                        if ("org.graffiti.plugins.views.defaults.RectangleNodeShape"
                                .equals(a.getValue())) {
                            sb.append(createTabs(level)
                                    + attMapping.get(a.getId()) + " ");
                            sb.append("\"rectangle\"");
                            sb.append(eol);
                        } else if ("org.graffiti.plugins.views.defaults.CircleNodeShape"
                                .equals(a.getValue())) {
                            sb.append(createTabs(level)
                                    + attMapping.get(a.getId()) + " ");
                            sb.append("\"oval\"");
                            sb.append(eol);
                        } else if ("org.graffiti.plugins.views.defaults.EllipseNodeShape"
                                .equals(a.getValue())) {
                            sb.append(createTabs(level)
                                    + attMapping.get(a.getId()) + " ");
                            sb.append("\"oval\"");
                            sb.append(eol);
                        }
                    }
                } else if ("arrowhead".equals(a.getId())) {
                    if (!a.getValue().equals("")) {
                        sb.append(createTabs(level));

                        try {
                            if (((StringAttribute) a.getParent().getAttribute(
                                    GraphicAttributeConstants.ARROWTAIL))
                                    .getString().equals("")) {
                                sb.append("arrow \"last\"");
                                sb.append(eol);
                            } else {
                                sb.append("arrow \"both\"");
                                sb.append(eol);
                            }
                        } catch (AttributeNotFoundException anfe) {
                            sb.append("arrow \"last\"");
                            sb.append(eol);
                        }
                    } else {
                        try {
                            if (((StringAttribute) a.getParent().getAttribute(
                                    GraphicAttributeConstants.ARROWTAIL))
                                    .getString().equals("")) {
                                sb.append(createTabs(level));
                                sb.append("arrow \"none\"");
                                sb.append(eol);
                            }
                        } catch (AttributeNotFoundException anfe) {
                            sb.append(createTabs(level));
                            sb.append("arrow \"none\"");
                            sb.append(eol);
                        }
                    }
                } else if ("arrowtail".equals(a.getId())) {
                    if (!a.getValue().equals("")) {
                        try {
                            if (((StringAttribute) a.getParent().getAttribute(
                                    GraphicAttributeConstants.ARROWHEAD))
                                    .getString().equals("")) {
                                sb.append(createTabs(level));
                                sb.append("arrow \"first\"");
                                sb.append(eol);
                            } else {
                                // "both" will be / has been written in
                                // "arrowhead" case above
                            }
                        } catch (AttributeNotFoundException anfe) {
                            sb.append(createTabs(level));
                            sb.append("arrow \"first\"");
                            sb.append(eol);
                        }
                    }
                } else {
                    sb.append(createTabs(level));
                    sb.append(a.getId() + " \"" + a.getValue() + "\"");
                    sb.append(eol);
                }
            } else {
                if (!a.getValue().equals("")) {
                    sb.append(createTabs(level));
                    sb.append(a.getId() + " \"" + a.getValue() + "\"");
                    sb.append(eol);
                }

                // warning("did not write: " + a.getPath());
            }
        } else {
            if (a instanceof BooleanAttribute) {
                sb.append(createTabs(level));
                sb.append(a.getId() + " "
                        + (((BooleanAttribute) a).getBoolean() ? 1 : 0));
                sb.append(eol);
            } else if (a instanceof ByteAttribute
                    || a instanceof IntegerAttribute
                    || a instanceof LongAttribute
                    || a instanceof ShortAttribute) {
                sb.append(createTabs(level));
                sb.append(a.getId() + " " + a.getValue().toString());
                sb.append(eol);
            } else if (a instanceof DoubleAttribute
                    || a instanceof FloatAttribute) {
                sb.append(createTabs(level));

                String val = a.getValue().toString();
                // sb.append(a.getId() + " " +val.substring(0, Math.min(17,
                // val.length())));
                sb.append(a.getId() + " " + val);
                sb.append(eol);
            } else if (attMapping.containsKey(a.getId())) {
                sb.append(createTabs(level));
                sb.append(attMapping.get(a.getId()) + " " + a.getValue());
                sb.append(eol);
            } else {
                warning("did not write complex attribute: " + a.getPath());
            }
        }

        return sb;
    }

    /**
     * Converts the given color attribute into a hex string. Returns
     * <code>#000000</code>, if the given color attribute could not be
     * converted.
     * 
     * @param colorAtt
     *            DOCUMENT ME!
     * 
     * @return a hex string representing the value of the given color attribute.
     *         e.g.: &quot;#FFFFFF&quot; or &quot;#00AAEE&quot;.
     */
    private String colToHex(Attribute colorAtt) {
        // String color = "";
        try {
            Color c = ((ColorAttribute) colorAtt).getColor();

            String r = Integer.toHexString(c.getRed());
            String g = Integer.toHexString(c.getGreen());
            String b = Integer.toHexString(c.getBlue());

            if (r.length() < 2) {
                r = "0" + r;
            }

            if (g.length() < 2) {
                g = "0" + g;
            }

            if (b.length() < 2) {
                b = "0" + b;
            }

            return "#" + (r + g + b).toUpperCase();
        } catch (Exception e) {
            return "#000000";
        }
    }

    /**
     * Creates and returns TAB + TAB + ... + TAB (level).
     * 
     * @param level
     *            the indentation level.
     * 
     * @return a string, of level TAB.
     */
    private String createTabs(int level) {
        StringBuffer b = new StringBuffer();

        for (int i = 0; i < level; i++) {
            b.append(TAB);
        }

        return b.toString();
    }

    /**
     * Prints the given warning to system.out.
     * 
     * @param msg
     *            the warning msg.
     */
    private void warning(String msg) {
        System.out.println("Warning: " + msg);
    }

    /**
     * Writes the attribute hierarchy of the specified attributable.
     * 
     * @param p
     *            the print stream to write to.
     * @param a
     *            the attributable to read the attributes from.
     * @param level
     *            the indentation level.
     */
    private void writeAttributable(PrintStream p, Attributable a, int level) {
        CollectionAttribute c = a.getAttributes();

        // if (colMapping.containsKey(c.getId())) {
        Map<String, Attribute> m = c.getCollection();

        for (Iterator<String> i = m.keySet().iterator(); i.hasNext();) {
            String id = i.next();

            Attribute subCol = c.getAttribute(id);
            p.print(getWrittenAttributeHierarchy(subCol, level + 1));
        }

        // } else {
        // System.out.println("Warning: did not write: " + c.getPath());
        // }
    }

    /**
     * Writes the edge of the given graph to the given print stream.
     * 
     * @param p
     *            the stream to write to.
     * @param g
     *            the graph to get the data from.
     * @param nodeIds
     *            the ordered list of node ids.
     */
    private void writeEdges(PrintStream p, Graph g, ArrayList<Node> nodeIds) {
        ArrayList<Edge> edgeIds = new ArrayList<Edge>();

        for (Iterator<Edge> i = g.getEdgesIterator(); i.hasNext();) {
            Edge e = i.next();

            edgeIds.add(e);

            p.println(createTabs(1) + "edge [");

            // FIXME edgeIds.indexOf(e) is inefficient
            p.println(createTabs(2) + "id " + edgeIds.indexOf(e));

            p.println(createTabs(2) + "source "
                    + nodeIds.indexOf(e.getSource()));

            p.println(createTabs(2) + "target "
                    + nodeIds.indexOf(e.getTarget()));

            writeAttributable(p, e, 1);

            p.println(createTabs(1) + "]");
        }
    }

    /**
     * Method writeGraph.
     * 
     * @param p
     * @param g
     */
    private void writeGraph(PrintStream p, Graph g) {
        writeAttributable(p, g, 0);
        try {
            g.getAttribute("directed");
        } catch (AttributeNotFoundException anfe) {
            p.println(createTabs(1) + "directed "
                    + (g.isDirected() ? "1" : "0"));
        }
    }

    /**
     * Writes the nodes of the given graph to the given print stream.
     * 
     * @param p
     *            the stream to write to.
     * @param g
     *            the graph to get the data from.
     * 
     * @return the ordered array list of nodes.
     */
    private ArrayList<Node> writeNodes(PrintStream p, Graph g) {
        ArrayList<Node> nodeIds = new ArrayList<Node>();

        for (Iterator<Node> i = g.getNodesIterator(); i.hasNext();) {
            Node n = i.next();

            nodeIds.add(n);

            p.println(createTabs(1) + "node [");

            // FIXME nodeIds.indexOf(n) is inefficient
            p.println(createTabs(2) + "id " + nodeIds.indexOf(n));
            writeAttributable(p, n, 1);
            p.println(createTabs(1) + "]");
        }

        return nodeIds;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
