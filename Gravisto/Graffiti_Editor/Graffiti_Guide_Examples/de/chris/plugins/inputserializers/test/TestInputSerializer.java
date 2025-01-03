// =============================================================================
//
//   TestInputSerializer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TestInputSerializer.java 5769 2010-05-07 18:42:56Z gleissner $

package de.chris.plugins.inputserializers.test;

import java.io.IOException;
import java.io.InputStream;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.io.AbstractInputSerializer;
import org.graffiti.plugin.io.ParserException;

/**
 * An implementation of a simple exporter that writes only structural
 * information of the graph in GML format in a file.
 * 
 * @author chris
 */
public class TestInputSerializer extends AbstractInputSerializer {

    /** The parser for reading in the graph. */
    private parser p;

    /**
     * Determines the extension of the written files. This extension occurs in
     * Gravistos File->Save and File->Save as.. Files of type combo box which
     * chooses the serializer which should be used and thus the format of the
     * file.
     * 
     * @see org.graffiti.plugin.io.Serializer#getExtensions()
     */
    public String[] getExtensions() {
        return new String[] { ".cgml" };
    }

    /**
     * Reads in a graph from the given input stream. <code>GraphElements</code>
     * read are <b>cloned</b> when added to the graph. Consider using the
     * <code>read(InputStream)</code> method when you start with an empty graph.
     * 
     * @param in
     *            the <code>InputStream</code> from which to read in the graph.
     * @param g
     *            the graph in which to read in the file.
     * 
     * @exception ParserException
     *                if an error occurs while parsing the .gml file.
     */
    @Override
    public void read(InputStream in, Graph g) throws ParserException {
        p = new parser(new Yylex(in));

        try {
            p.parse();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new ParserException(e.getMessage());
        }

        g.addGraph(p.getGraph());
    }

    /**
     * Reads in a graph from the given input stream. This implementation returns
     * an instance of <code>OptAdjListGraph</code> (that's what the parser
     * returns).
     * 
     * @param in
     *            The input stream to read the graph from.
     * 
     * @return The newly read graph (an instance of <code>OptAdjListGraph</code>
     *         ).
     * 
     * @exception IOException
     *                If an IO error occurs.
     * @throws ParserException
     *             DOCUMENT ME!
     */
    @Override
    public Graph read(InputStream in) throws IOException {
        p = new parser(new Yylex(in));

        try {
            p.parse();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new ParserException(e.getMessage());
        }

        in.close();

        return p.getGraph();
    }

    /*
     * @see org.graffiti.plugin.Parametrizable#getName()
     */
    @Override
    public String getName() {
        return "TestInputSerializer";
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
