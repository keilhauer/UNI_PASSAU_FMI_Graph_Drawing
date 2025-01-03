package quoggles.algorithms.qbfs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.NodeParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.util.Queue;

/**
 * A BFS on directed graphs in reverse direction. Especially used to find all
 * sources that lead to a specified Node.
 *
 * @version $Revision: 491 $
 */
public class QPreNodesAlgorithm
    extends AbstractAlgorithm
{
    //~ Instance fields ========================================================

    /** Start node */
    private Node sourceNode = null;
    
    /**
     * <code>List</code> (sorted according to BFS number) of nodes from which 
     * a path to the <code>sourceNode</code> exists.
     */
    private ArrayList preNodesList = null;
    
    /** 
     * Nodes without incoming edges that are in the 
     * <code>preNodesList</code> 
     */
    private ArrayList preSourceNodes = null;

    //~ Methods ================================================================

    /**
     * Returns the <code>List</code> (sorted according to BFS number) of nodes 
     * from which a path to the <code>sourceNode</code> exists.
     * 
     * @return
     */
    public ArrayList getPreNodesList() {
        return preNodesList;
    }

    /**
     * Returns a <code>List</code> of nodes without incoming edges that are 
     * in the <code>preNodesList</code>.
     *  
     * @return
     */
    public ArrayList getPreSourceNodes() {
        return preSourceNodes;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName()
    {
        return "Quoggles-PreNodes";
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#
     *      setParameters(org.graffiti.plugin.algorithm.Parameter)
     */
    public void setParameters(Parameter[] params)
    {
        this.parameters = params;
        sourceNode = ((NodeParameter)params[0]).getNode();
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getParameters()
     */
    public Parameter[] getParameters()
    {
        NodeParameter sourceNodeParam = new NodeParameter("Start node",
            "The algorithm will start with the only selected node.");

        return new Parameter[] { sourceNodeParam };
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    public void check()
        throws PreconditionException
    {
        if(sourceNode == null)
        {
            throw new PreconditionException(
                "The algorithm needs exactly one source node.");
        }

    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute(Graph)
     */
    public void execute()
    {
        Queue q = new Queue();
        preNodesList = new ArrayList();
        preSourceNodes = new ArrayList();

        // nodeNumMap contains a mapping from node to an integer, the bfsnum
        Map nodeNumMap = new HashMap();

        q.addLast(sourceNode);
        nodeNumMap.put(sourceNode, new Integer(0));
        preNodesList.add(sourceNode);

        while(!q.isEmpty())
        {
            Node v = (Node) q.removeFirst();

            // mark all neighbours and add all unmarked neighbours
            // of v to the queue
            if (v.getInDegree() == 0) {
                preSourceNodes.add(v);
            } else {
                for(Iterator neighbours = v.getInNeighborsIterator();
                    neighbours.hasNext();)
                {
                    Node neighbour = (Node) neighbours.next();
    
                    if(!nodeNumMap.containsKey(neighbour))
                    {
                        Integer bfsNum = new Integer(
                            ((Integer) nodeNumMap.get(v)).intValue() + 1);
                        nodeNumMap.put(neighbour, bfsNum);
                        preNodesList.ensureCapacity(bfsNum.intValue());
                        preNodesList.add(bfsNum.intValue(), neighbour);
                        q.addLast(neighbour);
                    }
                }
            }
        }

    }

}

//------------------------------------------------------------------------------
//   end of file
//------------------------------------------------------------------------------
