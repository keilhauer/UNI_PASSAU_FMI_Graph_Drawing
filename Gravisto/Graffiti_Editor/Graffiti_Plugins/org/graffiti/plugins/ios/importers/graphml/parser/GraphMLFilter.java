// =============================================================================
//
//   GraphMLFilter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphMLFilter.java 6112 2012-04-12 12:35:50Z gleissner $

package org.graffiti.plugins.ios.importers.graphml.parser;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.LineModeAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.graphics.PortAttribute;
import org.graffiti.graphics.RenderedImageAttribute;
import org.graffiti.util.logging.GlobalLoggerSetting;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Class <code>GraphMLFilter</code> processes the SAX events generated by the
 * parser and builds the graph according to the events. In that sense, this
 * class is the heart of the graphML reading library.
 * 
 * @author ruediger
 */
public class GraphMLFilter extends XMLFilterImpl {

    /** The logger for this class. */
    private static final Logger logger = Logger.getLogger(GraphMLFilter.class
            .getName());
    
    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /**
     * The <code>Attributable</code> that is currently being decorated with
     * attributes.
     */
    private Attributable currentAttributable;

    /**
     * The cache for keeping attributes before they are attatched to the
     * corresponding <code>Attributable</code>.
     */
    private AttributeCache attrCache;

    /** <code>AttributeCreator</code> to set default attribute values. */
    private AttributeCreator defaultCreator;

    /** The decorator used for creating edge attributes. */
    private AttributeCreator edgeAttributeCreator;

    /** The decorator used for creating graph attributes. */
    private AttributeCreator graphAttributeCreator;

    /** The decorator used for creating node attributes. */
    private AttributeCreator nodeAttributeCreator;

    /**
     * Helper for creating bends attributes for edges. TODO remove this when
     * attributes work as expected (see below).
     */
    private EdgeBendsCreator edgeBendsCreator;

    /**
     * A list of labels whose creation is delayed, too ensure all needed
     * information is already parsed.
     */
    private List<LabelEntry> delayedLabelCreation = new LinkedList<LabelEntry>();

    /** The graph to which to add the read in data. */
    private Graph graph;

    /** The mapping for mapping ids to nodes in the graph. */
    private NodeMapping nodeMap;

    /** Indicates that we are inside a default declaration. */
    private boolean defaultDecl;

    /** Determines whether an edge is directed or undirected by default. */
    private boolean edgeDefault = false;

    /**
     * Constructs a new <code>GraphMLFilter</code>.
     * 
     * @param parent
     *            the parent <code>XMLReader</code>.
     * @param graph
     *            the <code>Graph</code> to which to add the read in data.
     */
    public GraphMLFilter(XMLReader parent, Graph graph) {
        super(parent);
        this.graph = graph;
        this.nodeMap = new NodeMapping();
        this.graphAttributeCreator = new GraphAttributeCreator();
        this.nodeAttributeCreator = new NodeAttributeCreator();
        this.edgeAttributeCreator = new EdgeAttributeCreator();
        this.attrCache = new AttributeCache();
        this.defaultDecl = false;
    }

    /**
     * Filter a character data event. This method assumes that the character
     * data is not split into multiple events. Depending on whether the event
     * belongs to a default attribute value declaration or a usual attribute
     * value declaration the corrsponding attribute value will be set.
     * 
     * @param ch
     *            an array of characters.
     * @param start
     *            the starting position in the array.
     * @param length
     *            the number of characters to use from the array.
     * 
     * @exception SAXException
     *                The client may throw an exception during processing.
     */
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        // determine the value
        String value = new String(ch, start, length);

        if (logger.isLoggable(Level.FINE)) {
            logger.fine(((length - start) < 50) ? new String(ch, start, length)
                    : ((new String(ch, start, start + 40)) + "... (length: "
                            + (length - start) + ") ..." + value.substring(
                            value.length() - 10, value.length())));
        }

        String path = attrCache.getPath();
        assert path != null : "path is null.";
        
        if (path.startsWith(".")) {
            path = path.substring(1);
        }

        // if there is a default declaration set the default value
        if (this.defaultDecl) {
            this.defaultCreator.addDefaultValue(value);

            return;
        }

        // the view will expect a NodeLabelAttribute or an EdgeLabelAttribute
        // therefor we have to make sure the CollectionAttribute is converted
        // afterwards into the corresponding type of LabelAttribute
        if (path.endsWith("." + GraphicAttributeConstants.LABEL)) {
            String parent = path.substring(0, path.lastIndexOf('.'));
            LabelEntry entry = new LabelEntry(parent, currentAttributable);
            delayedLabelCreation.add(entry);
        }

        // TODO attribute hack
        // handle special paths: this should not be necessary once the
        // attributes work as expected, in particular the methods
        // set[type](value)
        if (path.equals("graphics.backgroundImage.image")) {
            byte[] val = Base64.decode(value);
            assert val.length > 0 : "byte encoding of image has length zero.";

            // TODO special treatment for graphics.backgroundImage.image
            ByteArrayInputStream is = new ByteArrayInputStream(val);

            // BufferedInputStream bis = new BufferedInputStream(is);
            try {
                // BufferedImage buffImg = ImageIO.read(bis);
                BufferedImage buffImg = ImageIO.read(is);
                assert buffImg != null : "the created image is null.";

                RenderedImageAttribute iattr = (RenderedImageAttribute) currentAttributable
                        .getAttribute(path);
                iattr.setImage(buffImg);

                logger.fine("background image has been read in.");
            } catch (IOException ioe) {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.warning("could not read in image: "
                            + ioe.getMessage());
                }
            }
        }

        // TODO attribute hack
        // handling of attribute .graphics.linemode will have to be changed
        // once LineModeAttribute is implemented correctly, i.e. is a composite
        // attribute that contains a collection of float attributes.
        else if (path.equals("graphics.linemode")) {
            LineModeAttribute lma = (LineModeAttribute) currentAttributable
                    .getAttribute(path);

            logger
                    .fine("value of attribute .graphics.linemode: " + value
                            + ".");

            if (value.equals("( ) 0.0")) {
                lma.setDefaultValue();
            } else {
                List<Float> dash = new LinkedList<Float>();
                String array = value.substring(1, value.indexOf(")")).trim();

                if (array.length() > 0) {
                    String[] floats = array.split(",");

                    for (int i = 0; i < floats.length; ++i) {
                        dash.add(Float.valueOf(floats[i].trim()));
                    }

                    float[] dashArray = new float[dash.size()];
                    int i = -1;

                    for (Float f : dash) {
                        dashArray[++i] = f;
                    }

                    lma.setDashArray(dashArray);
                }

                String phaseString = value.substring(value.indexOf(")") + 1);
                float phase = Float.parseFloat(phaseString.trim());
                lma.setDashPhase(phase);
            }
        }

        // TODO attribute hack
        // it should be possible to write the bends using the method
        // setDouble(".graphics.bends.bend<i>", value)
        else if (path.startsWith("graphics.bends.bend")) {
            String[] ids = path.split("\\.");

            // CoordinateAttribute coords = null;
            String bend = ids[ids.length - 2];

            // make sure the preceeding attributes exist
            int no = Integer.parseInt(bend.replaceAll("bend", ""));

            if (ids[ids.length - 1].equals("x")) {
                edgeBendsCreator.addX(no, Double.parseDouble(value));
            } else if (ids[ids.length - 1].equals("y")) {
                edgeBendsCreator.addY(no, Double.parseDouble(value));
            } else {
                assert ids[ids.length - 1].equals("z");

                edgeBendsCreator.addZ(no, Double.parseDouble(value));

            }
        } else {
            logger.fine("writing attribute with value " + value
                    + "\n\tat path " + path + ".");

            switch (attrCache.getType()) {
            case AttributeCache.BOOLEAN:

                boolean booleanValue = Boolean.valueOf(value).booleanValue();
                logger.fine("writing boolean value " + booleanValue
                        + " at path " + path + ".");
                currentAttributable.setBoolean(path, booleanValue);

                break;

            case AttributeCache.INT:

                int intValue = Integer.parseInt(value);
                logger.fine("writing integer value " + intValue + " at path "
                        + path + ".");
                currentAttributable.setInteger(path, intValue);

                break;

            case AttributeCache.LONG:

                long longValue = Long.parseLong(value);
                logger.fine("writing long value " + longValue + " at path "
                        + path + ".");
                currentAttributable.setLong(path, longValue);

                break;

            case AttributeCache.FLOAT:

                float floatValue = Float.parseFloat(value);
                logger.fine("writing float value " + floatValue + " at path "
                        + path + ".");
                currentAttributable.setFloat(path, floatValue);

                break;

            case AttributeCache.DOUBLE:

                double doubleValue = Double.parseDouble(value);
                logger.fine("writing double value " + doubleValue + " at path "
                        + path + ".");
                currentAttributable.setDouble(path, doubleValue);

                break;

            case AttributeCache.STRING:
                logger.fine("writing string value " + value + " at path "
                        + path + ".");
                currentAttributable.setString(path, value);

                break;

            default:
                logger.warning("internal error: type of attribute not set "
                        + "correctly.");

                break;
            }
        }

        attrCache.reset();
    }

    /**
     * Create NodeLabelAttributes or EdgeLabelAttributes for all found labels.
     * This is done after parsing the whole graphml file to make sure all needed
     * attributes have been read.
     */
    public void endDocument() {
        for (LabelEntry entry : delayedLabelCreation) {
            // extract path, id and path of the parent attribute
            String path = entry.getPath();
            final int sepPos = path.lastIndexOf(Attribute.SEPARATOR);
            String id = path.substring(sepPos + 1);
            String parent = (sepPos >= 0) ? path.substring(0, sepPos) : "";
            Attributable attr = entry.getAttributable();

            // remove the collection attribute holding all necessary
            // information for a label attribute from the attributable
            // and add the correct label attribute instead
            CollectionAttribute cattr = (CollectionAttribute) attr
                    .getAttribute(path);
            LabelAttribute lattr = null;
            if (attr instanceof Node) {
                lattr = new NodeLabelAttribute(id);
            } else if (attr instanceof Edge) {
                lattr = new EdgeLabelAttribute(id);
            } else {
                logger.warning("Unknown attributable for LabelAttribute "
                        + attr);
                continue;
            }
            attr.removeAttribute(path);
            lattr.setCollection(cattr.getCollection());
            attr.addAttribute(lattr, parent);
        }
    }

    /**
     * Filter an end element event. Depending on the kind of element certain
     * states of the filter will be reset.
     * 
     * @param uri
     *            the element's Namespace URI, or the empty string.
     * @param localName
     *            the element's local name, or the empty string.
     * @param qName
     *            the element's qualified (prefixed) name, or the empty string.
     * 
     * @exception SAXException
     *                The client may throw an exception during processing.
     */
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("processing end-element </" + qName + ">.");
        }

        if (qName.equals("graphml")) {
            // nothing specific required to be done
        } else if (qName.equals("key")) {
            assert this.defaultCreator != null : "default attribute creator "
                    + "is null";

            // reset the default attribute creator
            this.defaultCreator = null;
        } else if (qName.equals("default")) {
            this.defaultDecl = false;
        } else if (qName.equals("graph")) {
            this.currentAttributable = null;
        } else if (qName.equals("node")) {
            Attribute allPorts = currentAttributable
                    .getAttribute(".graphics.ports");
            if (allPorts != null && allPorts instanceof CollectionAttribute) {
                CollectionAttribute ca = (CollectionAttribute) allPorts;
                for (Attribute ps : ca.getCollection().values()) {
                    if (ps != null && ps instanceof CollectionAttribute) {
                        CollectionAttribute ports = (CollectionAttribute) ps;
                        List<Attribute> portsCopy = new LinkedList<Attribute>();
                        portsCopy.addAll(ports.getCollection().values());
                        for (Attribute p : portsCopy) {
                            if (p != null && p instanceof CollectionAttribute) {
                                CollectionAttribute port = (CollectionAttribute) p;
                                ports.remove(port);
                                StringAttribute name = (StringAttribute) port
                                        .getAttribute("name");
                                CollectionAttribute coord = (CollectionAttribute) port
                                        .getAttribute("coordinate");
                                DoubleAttribute x = (DoubleAttribute) coord
                                        .getAttribute("x");
                                DoubleAttribute y = (DoubleAttribute) coord
                                        .getAttribute("y");

                                PortAttribute pa = new PortAttribute(port
                                        .getId(), name, x.getDouble(), y
                                        .getDouble());
                                ports.add(pa, false);
                            }
                        }
                    }
                }
            }

            // current attributable is again the graph
            this.currentAttributable = this.graph;
        } else if (qName.equals("edge")) {
            // get the list of bends and add them
            SortedCollectionAttribute bends = edgeBendsCreator.getBends();
            EdgeGraphicAttribute ega = (EdgeGraphicAttribute) currentAttributable
                    .getAttribute("graphics");
            ega.setBends(bends);

            // current attributable is again the graph
            this.currentAttributable = this.graph;
        } else if (qName.equals("data")) {
            if (!attrCache.isReset()) {
                // there was an empty data tag -> make sure an
                // empty attribute is created
                characters(new char[] {}, 0, 0);
                // cache is no longer needed and can be reset
                attrCache.reset();
            }
        } else {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning("don't know how to handle element </" + qName
                        + ">.");
            }
        }
    }

    /**
     * Filter a start element event. Depending on the kind of attribute either
     * an element will be added to the graph or the filter is prepared to modify
     * the graph appropriately depending on the next coming events.
     * 
     * @param uri
     *            the element's Namespace URI, or the empty string.
     * @param localName
     *            the element's local name, or the empty string.
     * @param qName
     *            the element's qualified (prefixed) name, or the empty string.
     * @param atts
     *            the element's attributes.
     * 
     * @exception SAXException
     *                the client may throw an exception during processing.
     */
    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("processing element <" + qName + ">.");
        }

        // graphml declaration
        if (qName.equals("graphml")) {
            logger.fine("sending element <graphml> to the parent parser");
            super.startElement(uri, localName, qName, atts);
        }

        // key declaration
        else if (qName.equals("key")) {
            String forDecl = atts.getValue("for");
            assert forDecl != null : "no for-attribute at key declaration.";

            if (logger.isLoggable(Level.FINE)) {
                logger.fine("processing key declaration for " + forDecl + ".");
            }

            // get id, attr.name and attr.type from the attribute declarations
            // if attr.name is not specified we use the id instead.
            String id = atts.getValue("id");
            String name = atts.getValue("attr.name");

            if (name == null) {
                assert id != null : "attribute id is null as well as "
                        + "attr.name.";
                name = id;
            }

            // get the type declaration - if not present use string
            String type = atts.getValue("attr.type");

            if (type == null) {
                type = "string";
            }

            assert this.defaultCreator == null : "defaultCreator is not null.";

            if (forDecl.equals("graph")) {
                this.graphAttributeCreator.addKeyDeclaration(id, name, type);
                this.defaultCreator = this.graphAttributeCreator;
            } else if (forDecl.equals("node")) {
                this.nodeAttributeCreator.addKeyDeclaration(id, name, type);
                this.defaultCreator = this.nodeAttributeCreator;
            } else if (forDecl.equals("edge")) {
                this.edgeAttributeCreator.addKeyDeclaration(id, name, type);
                this.defaultCreator = this.edgeAttributeCreator;
            } else {
                if (logger.isLoggable(Level.WARNING)) {
                    logger
                            .warning("key declaration with unknown for-attribute "
                                    + forDecl + " - ignored.");
                }

                return;
            }

            this.defaultCreator.setDefault(name, type);
        }

        // default attribute values
        else if (qName.equals("default")) {
            assert this.defaultCreator != null : "default creator is null.";
            this.defaultDecl = true;
        }

        // graph declaration
        else if (qName.equals("graph")) {
            // set the default for the direction of edges
            this.edgeDefault = atts.getValue("edgedefault").equals("directed");

            if (logger.isLoggable(Level.FINE)) {
                logger.fine("processing declaration for "
                        + (this.edgeDefault ? "" : "un") + "directed graph.");
            }

            addDefaultAttributeValues(this.graph, graphAttributeCreator
                    .getDefaults());

            // add the default attributes
            graph
                    .addAttribute(graphAttributeCreator.getDefaultAttributes(),
                            "");
            // set the current attributable to the graph
            assert this.currentAttributable == null : "current attributable "
                    + "is not null.";
            this.currentAttributable = this.graph;
        }

        // node declaration
        else if (qName.equals("node")) {
            String id = atts.getValue("id");
            assert id != null : "id of node is null.";
            assert !id.equals("") : "id of node is empty string.";

            if (logger.isLoggable(Level.FINE)) {
                logger.fine("processing node with id " + id + ".");
            }

            // create a new node, add it to the node mapping and attatch some
            // default attributes
            // Node n =
            // this.graph.addNode(nodeAttributeCreator.getDefaultAttributes());
            Node n = this.graph.addNode();
            this.nodeMap.addNode(id, n);

            n.addAttribute(nodeAttributeCreator.getDefaultAttributes(), "");

            addDefaultAttributeValues(n, nodeAttributeCreator.getDefaults());

            // set current attributable to add attributes
            assert this.currentAttributable == this.graph : "current "
                    + "attributable is not the graph.";
            this.currentAttributable = n;
        }

        // edge declaration
        else if (qName.equals("edge")) {
            logger.fine("processing edge declaration for.");

            // determine the source of the edge from the declaration
            String sourceId = atts.getValue("source");
            assert sourceId != null : "id of edge source is null.";
            assert !sourceId.equals("") : "id of edge source is empty string.";

            Node source = this.nodeMap.getNode(sourceId);

            // determine the target of the edge from the declaration
            String targetId = atts.getValue("target");
            assert targetId != null : "id of edge target is null.";
            assert !targetId.equals("") : "id of edge target is empty string.";

            Node target = this.nodeMap.getNode(targetId);

            // determine whether the edge is directed
            String directed = atts.getValue("directed");
            boolean dir;

            if (directed == null) {
                dir = this.edgeDefault;
            } else {
                dir = Boolean.valueOf(directed).booleanValue();
            }

            // create a new edge and attatch some default attributes
            Edge e = this.graph.addEdge(source, target, dir);
            e.addAttribute(edgeAttributeCreator.getDefaultAttributes(), "");

            addDefaultAttributeValues(e, edgeAttributeCreator.getDefaults());

            // set current attributable to add attributes
            assert this.currentAttributable == this.graph : "current "
                    + "attributable is not the graph.";
            this.currentAttributable = e;

            // prepare the bends attribute which will be added after all the
            // edge attributes are processed
            this.edgeBendsCreator = new EdgeBendsCreator();
        }

        // data declarations
        else if (qName.equals("data")) {
            assert this.currentAttributable != null;

            String key = atts.getValue("key");

            if (logger.isLoggable(Level.FINE)) {
                logger.fine("processing data declaration for key \"" + key
                        + "\".");
            }

            // determine path and type of the attribute
            AttributeCreator acrtr = null;

            if (currentAttributable instanceof Graph) {
                acrtr = graphAttributeCreator;
            } else if (currentAttributable instanceof Node) {
                acrtr = nodeAttributeCreator;
            } else if (currentAttributable instanceof Edge) {
                acrtr = edgeAttributeCreator;
            } else {
                logger.warning("currentAttributable is neither graph nor "
                        + "node nor edge");
            }

            assert acrtr != null : "attribute creator is null.";

            String path = acrtr.getName(key);
            String type = acrtr.getType(key);

            // if there is no path declared we use the (unique) key-id
            if ((path == null) || path.equals("")) {
                path = key;
            }

            assert (path != null) && !path.equals("") : "illegal value " + path
                    + " for path.";
            assert (type != null) && !type.equals("") : "illegal value " + type
                    + " for type.";
            assert attrCache.isReset() : "attribute cache is not reset.";

            if (type.equals("boolean")) {
                attrCache.prepare(path, AttributeCache.BOOLEAN);
            } else if (type.equals("int")) {
                attrCache.prepare(path, AttributeCache.INT);
            } else if (type.equals("long")) {
                attrCache.prepare(path, AttributeCache.LONG);
            } else if (type.equals("float")) {
                attrCache.prepare(path, AttributeCache.FLOAT);
            } else if (type.equals("double")) {
                attrCache.prepare(path, AttributeCache.DOUBLE);
            } else if (type.equals("string")) {
                attrCache.prepare(path, AttributeCache.STRING);
            } else {
                assert false : "unknown type " + type + ".";
            }
        } else {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning("don't know how to handle element <" + qName
                        + ">.");
            }
        }
    }

    /**
     * Sets the default attribute value for each path and value object in the
     * specified <code>Map</code>.
     * 
     * @param attbl
     *            the <code>Attributable</code> at which to set the attribute
     *            value.
     * @param map
     *            the <code>Map</code> containing a mapping from attribute path
     *            to value objects.
     */
    private void addDefaultAttributeValues(Attributable attbl,
            Map<String, Object> map) {
        for (String path : map.keySet()) {
            Object attr = map.get(path);

            logger.fine("adding default attribute  at path " + path
                    + " with value " + attr.toString() + ".");

            if (attr instanceof Boolean) {
                boolean value = ((Boolean) attr).booleanValue();
                attbl.setBoolean(path, value);
            } else if (attr instanceof Integer) {
                int value = ((Integer) attr).intValue();
                attbl.setInteger(path, value);
            } else if (attr instanceof Long) {
                long value = ((Long) attr).longValue();
                attbl.setLong(path, value);
            } else if (attr instanceof Float) {
                float value = ((Float) attr).floatValue();
                attbl.setFloat(path, value);
            } else if (attr instanceof Double) {
                double value = ((Double) attr).doubleValue();
                attbl.setDouble(path, value);
            } else if (attr instanceof String) {
                String value = (String) attr;
                attbl.setString(path, value);
            } else {
                logger.warning("could not set attribute value of type"
                        + attr.getClass().getName());
            }

            // attbl.addAttribute(attr, addPath);
        }
    }

    /**
     * LabelEntry represents a label of an attributable.
     */
    private class LabelEntry {
        /** Path to label attribute. */
        private String path;

        /** Attributable containing the label attribute. */
        private Attributable attributable;

        /**
         * Create a new label entry.
         * 
         * @param path
         *            Path of label entry.
         * @param attributable
         *            Attributable containing label attribute.
         */
        public LabelEntry(String path, Attributable attributable) {
            this.path = path;
            this.attributable = attributable;
        }

        /**
         * Get path for label attribute.
         * 
         * @return Path for label attribute.
         */
        public String getPath() {
            return path;
        }

        /**
         * Get attributable containing label attribute.
         * 
         * @return Attributable.
         */
        public Attributable getAttributable() {
            return attributable;
        }
    }

    /**
     * Class <code>EdgeBendsCreator</code> manages the bends of edges. TODO this
     * inner class should become superflous once the set[type]() methods for
     * setting values of attributes work as expected (cf. above).
     */
    private class EdgeBendsCreator {
        /** The list containing the bends. */
        private List<CoordinateAttribute> coordList;

        /**
         * Constructs a new <code>EdgeBendsCreator</code>.
         */
        EdgeBendsCreator() {
            this.coordList = new ArrayList<CoordinateAttribute>();
        }

        /**
         * Returns a <code>SortedCollectionAttribute</code> containing all the
         * <code>CoordinateAttribute</code>s that are not <code>null</code>.
         * 
         * @return a <code>SortedCollectionAttribute</code> containing all the
         *         <code>CoordinateAttribute</code>s that are not
         *         <code>null</code>.
         */
        SortedCollectionAttribute getBends() {
            SortedCollectionAttribute bends = new LinkedHashMapAttribute(
                    "bends");

            for (CoordinateAttribute c : coordList)
                if (c != null) {
                    bends.add(c);
                }

            return bends;
        }

        /**
         * Sets the x-coordinate of the coordinate attribute at position pos to
         * the specified value.
         * 
         * @param pos
         *            the position of the corresponding coordinate attribute.
         * @param value
         *            the value to be set.
         */
        void addX(int pos, double value) {
            assert pos >= 0 : "negative index of bend.";

            // check if the list is long enough and fill in nulls if not
            // this prevents us from getting IndexOutOfBoundsExceptions and
            // enables us to preserve the ordering of the bends
            if (coordList.size() <= pos) {
                for (int i = coordList.size(); i <= (pos + 1); ++i) {
                    coordList.add(i, null);
                }
            }

            assert pos < coordList.size() : "position: " + pos + ", size: "
                    + coordList.size() + ".";

            CoordinateAttribute ca = coordList.get(pos);

            if (ca == null) {
                ca = new CoordinateAttribute("bend" + pos);
                ca.setX(value);
                coordList.set(pos, ca);
            } else {
                ca.setX(value);
            }
        }

        /**
         * Sets the y-coordinate of the coordinate attribute at position pos to
         * the specified value.
         * 
         * @param pos
         *            the position of the corresponding coordinate attribute.
         * @param value
         *            the value to be set.
         */
        void addY(int pos, double value) {
            assert pos >= 0 : "negative index of bend.";

            // check if the list is long enough and fill in nulls if not
            // this prevents us from getting IndexOutOfBoundsExceptions and
            // enables us to preserve the ordering of the bends
            if (coordList.size() <= pos) {
                for (int i = coordList.size(); i <= pos; ++i) {
                    coordList.add(i, null);
                }
            }

            CoordinateAttribute ca = coordList.get(pos);

            if (ca == null) {
                ca = new CoordinateAttribute("bend" + pos);
                ca.setY(value);
                coordList.set(pos, ca);
            } else {
                ca.setY(value);
            }
        }

        /**
         * Sets the z-coordinate of the coordinate attribute at position pos to
         * the specified value.
         * 
         * @param pos
         *            the position of the corresponding coordinate attribute.
         * @param value
         *            the value to be set.
         */
        void addZ(int pos, double value) {
            assert pos >= 0 : "negative index of bend.";

            // check if the list is long enough and fill in nulls if not
            // this prevents us from getting IndexOutOfBoundsExceptions and
            // enables us to preserve the ordering of the bends
            if (coordList.size() <= pos) {
                for (int i = coordList.size(); i <= pos; ++i) {
                    coordList.add(i, null);
                }
            }

            CoordinateAttribute ca = coordList.get(pos);

            if (ca == null) {
                ca = new CoordinateAttribute("bend" + pos);
                ca.setZ(value);
                coordList.set(pos, ca);
            } else {
                ca.setZ(value);
            }
        }
    }

    /**
     * This class provides a mapping from <code>Node</code>s to
     * <code>String</code> identifiers.
     */
    private class NodeMapping {
        /** The map that contains the mapping. */
        private Map<String, Node> nodeMap;

        /**
         * Constructs a new <code>NodeMapping</code>.
         */
        NodeMapping() {
            this.nodeMap = new Hashtable<String, Node>();
        }

        /**
         * Returns the <code>Node</code> for the specified identifier,
         * <code>null</code> if there is none.
         * 
         * @param id
         *            the identifier for which to return the <code>Node</code>.
         * 
         * @return the <code>Node</code> for the specified identifier,
         *         <code>null</code> if there is none.
         */
        Node getNode(String id) {
            Object o = this.nodeMap.get(id);

            if (o == null)
                return null;
            else
                return (Node) o;
        }

        /**
         * Adds a <code>Node</code> with the specified identifier to the
         * mapping.
         * 
         * @param id
         *            the identifier for the <code>Node</code>.
         * @param node
         *            the <code>Node</code> to be added.
         */
        void addNode(String id, Node node) {
            this.nodeMap.put(id, node);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------