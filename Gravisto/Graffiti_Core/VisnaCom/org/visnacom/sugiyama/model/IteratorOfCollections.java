/*==============================================================================
*
*   IteratorOfCollections.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: IteratorOfCollections.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.model;

import java.util.*;
import java.util.Collection;
import java.util.Iterator;

/**
 * this class is an comfortable Iterator for elements in Collections of Collections
 */
public class IteratorOfCollections implements Iterator {
    //~ Instance fields ========================================================

    private Iterator elementIterator;
    private Iterator iteratorOfLists;

    //~ Constructors ===========================================================

    /**
     * Creates a new IteratorOfCollections object.
     *
     * @param coll a Collection, that must contain Collection object
     */
    public IteratorOfCollections(Collection coll) {
        iteratorOfLists = coll.iterator();
    }

    //~ Methods ================================================================

    /**
     * additional functionality of this class. One could say, the method flatens the
     * structure by one dimension.
     *
     * @param matrix the given Collection must contain Collection object.
     *
     * @return a list of the elements of those Collections
     */
    public static List getElementList(Collection matrix) {
        List result = new LinkedList();
        for(Iterator it = matrix.iterator(); it.hasNext();) {
            result.addAll((Collection) it.next());
        }

        return result;
    }

    /**
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        if(elementIterator != null && elementIterator.hasNext()) {
            return true;
        } else {
            while(openNextIterator()) {
                if(elementIterator.hasNext()) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * @see java.util.Iterator#next()
     */
    public Object next() {
        if(!hasNext()) {
            throw new NoSuchElementException();
        }
        return elementIterator.next();
    }

    /**
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        elementIterator.remove();
//        throw new UnsupportedOperationException();
    }

    /**
     * opens the next hashmap entry
     *
     * @return DOCUMENT ME!
     */
    private boolean openNextIterator() {
        if(iteratorOfLists.hasNext()) {
            elementIterator = ((Collection) iteratorOfLists.next()).iterator();
            return true;
        } else {
            return false;
        }
    }
}
