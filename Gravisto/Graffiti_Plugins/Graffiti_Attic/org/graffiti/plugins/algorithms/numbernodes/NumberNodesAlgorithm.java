// =============================================================================
//
//   NumberNodesAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NumberNodesAlgorithm.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.algorithms.numbernodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.selection.Selection;

/**
 * Labels all selected nodes with unique numbers. Does not touch existing
 * labels.
 */
public class NumberNodesAlgorithm extends AbstractAlgorithm {

    /** DOCUMENT ME! */
    private Selection selection;

    /** DOCUMENT ME! */
    private final String LABEL_PATH;

    /** DOCUMENT ME! */
    private boolean ignore = false;

    /** DOCUMENT ME! */
    private int number;

    /** DOCUMENT ME! */
    private int start_number = 1;

    /**
     * Constructs a new instance.
     */
    public NumberNodesAlgorithm() {
        if (GraphicAttributeConstants.LABEL_ATTRIBUTE_PATH.equals("")) {
            LABEL_PATH = GraphicAttributeConstants.LABEL;
        } else {
            LABEL_PATH = GraphicAttributeConstants.LABEL_ATTRIBUTE_PATH
                    + Attribute.SEPARATOR + GraphicAttributeConstants.LABEL;
        }

        number = start_number;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Number nodes";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        selection = ((SelectionParameter) params[0]).getSelection();
        start_number = ((IntegerParameter) params[1]).getValue();
        ignore = ((BooleanParameter) params[2]).getBoolean().booleanValue();
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("Start node",
                "Connect will start with the only selected node.");
        IntegerParameter snParam = new IntegerParameter(start_number,
                "Start value", "Labelling will start with this number.");
        BooleanParameter ignoreParam = new BooleanParameter(ignore, "Ignore",
                "Ignore existing (standard) labels.");

        return new Parameter[] { selParam, snParam, ignoreParam };
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        if (selection == null) {
            selection = new Selection("__temp__");
        }

        if (selection.isEmpty()) {
            selection.addAll(graph.getGraphElements());
        }

        Collection<Node> nodes = selection.getNodes();

        number = start_number;

        graph.getListenerManager().transactionStarted(this);

        Collection<Integer> existing = new HashSet<Integer>();
        NodeLabelAttribute labelAttr;

        if (!ignore) {
            for (Iterator<Node> it = nodes.iterator(); it.hasNext();) {
                try {
                    labelAttr = (NodeLabelAttribute) (it.next())
                            .getAttribute(LABEL_PATH);

                    existing.add(new Integer(Integer.parseInt(labelAttr
                            .getLabel())));
                } catch (AttributeNotFoundException anfe) {
                    // no label - no problem
                } catch (NumberFormatException nfe) {
                    // no integer label - no problem
                }
            }
        }

        Node node;

        for (Iterator<Node> it = nodes.iterator(); it.hasNext();) {
            while (!ignore && existing.contains(new Integer(number))) {
                number++;
            }

            node = it.next();

            try {
                labelAttr = (NodeLabelAttribute) node.getAttribute(LABEL_PATH);

                // has label - don't touch it; except it is empty
                if (ignore || labelAttr.getLabel().equals("")) {
                    labelAttr.setLabel(String.valueOf(number));
                    number++;
                }
            } catch (AttributeNotFoundException anfe) {
                // no label - associate one
                labelAttr = new NodeLabelAttribute(
                        GraphicAttributeConstants.LABEL, String.valueOf(number));
                node.addAttribute(labelAttr,
                        GraphicAttributeConstants.LABEL_ATTRIBUTE_PATH);
                number++;
            } catch (NumberFormatException nfe) {
                // existing (non-integer) label - don't touch it
            }
        }

        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        graph = null;
        selection = null;
        number = start_number;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
