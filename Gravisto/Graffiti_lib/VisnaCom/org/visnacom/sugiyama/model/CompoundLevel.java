/*==============================================================================
*
*   CompoundLevel.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: CompoundLevel.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.model;

import java.awt.Rectangle;
import java.util.*;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Representation of compound levels. Only one object per level is created.
 * They are connected to each other in a lexicographic tree.
 */
public class CompoundLevel implements Comparable {
    //~ Instance fields ========================================================

    /*some attributes for painting the levels*/
    public int height;
    public int leftX;
    public int leftXafterLabel;
    public int rightX;
    public int y;

    /* the tree */
    private CompoundLevel parent;

    /* a list of Integer objects*/
    private LinkedList clev;
    private List children = new ArrayList();
    private boolean isUsed = false;

    //~ Constructors ===========================================================

    /**
     * Creates a new CompoundLevel object. will represent the root
     */
    private CompoundLevel() {
        this.clev = new LinkedList();
        clev.add(new Integer(1));
        parent = null;
    }

    /**
     * Creates a new CompoundLevel object.
     *
     * @param parent DOCUMENT ME!
     * @param sublevel DOCUMENT ME!
     */
    private CompoundLevel(CompoundLevel parent, int sublevel) {
        this.parent = parent;
        clev = (LinkedList) parent.clev.clone();
        clev.addLast(new Integer(sublevel));
    }

    //~ Methods ================================================================

    /**
     * this is the entrance for creating CompoundLevel objects. In further
     * steps, use  "getSublevel"
     *
     * @return DOCUMENT ME!
     */
    public static CompoundLevel getClevForRoot() {
        return new CompoundLevel();
    }

    /**
     * for drawing only
     *
     * @return DOCUMENT ME!
     */
    public Rectangle getBoundingRect() {
        return new Rectangle(leftX, y, rightX - leftX, height);
    }

    /**
     * for drawing only
     *
     * @return DOCUMENT ME!
     */
    public List getChildren() {
        return children;
    }

    /**
     * for drawing only
     *
     * @return DOCUMENT ME!
     */
    public Rectangle getDrawingRect() {
        return new Rectangle(leftXafterLabel, y, rightX - leftXafterLabel,
            height);
    }

    /**
     * DOCUMENT ME!
     *
     * @param otherClev DOCUMENT ME!
     *
     * @return true, if this clev is a real initial substring of the given clev
     */
    public boolean isInitialSubstringOf(CompoundLevel otherClev) {
        Iterator itOther = otherClev.levelIterator();
        Iterator itThis = this.levelIterator();
        while(itThis.hasNext()) {
            int valueThis = ((Integer) itThis.next()).intValue();
            if(itOther.hasNext()) {
                int valueOther = ((Integer) itOther.next()).intValue();
                if(valueThis != valueOther) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return itOther.hasNext();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getLength() {
        return clev.size();
    }

    /**
     * used in normalization
     *
     * @param other DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getLengthOfCommonPart(CompoundLevel other) {
        int result = 0;
        Iterator itOther = other.clev.iterator();
        Iterator itThis = clev.iterator();
        while(itThis.hasNext()) {
            int valueThis = ((Integer) itThis.next()).intValue();
            if(itOther.hasNext()) {
                int valueOther = ((Integer) itOther.next()).intValue();
                if(valueThis != valueOther) {
                    return result;
                } else {
                    result++;
                    //continue with while loop
                }
            } else {
                //other is anfangsstueck
                return result;
            }
        }

        return result;
    }

    /**
     * fills the given list with all clevs in the subtree of this clev. for
     * drawing only
     *
     * @param list DOCUMENT ME!
     */
    public void getLevelSubTree(List list) {
        list.add(this);
        for(Iterator it = children.iterator(); it.hasNext();) {
            ((CompoundLevel) it.next()).getLevelSubTree(list);
        }
    }

    /**
     * checks whether the given compoundlevel represents an sibling level to
     * this one.
     *
     * @param otherClev DOCUMENT ME!
     *
     * @return true, if the given clev is a sibling
     */
    public boolean isSiblingTo(CompoundLevel otherClev) {
        return this.parent == otherClev.parent;
    }

    /**
     * main method for accessing CompoundLevel objects.
     *
     * @param sublevel the wanted sublevel value
     *
     * @return a CompoundLevel object, that represents the appropriate sublevel
     *         of this object
     */
    public CompoundLevel getSubLevel(int sublevel) {
        for(int i = children.size(); i <= sublevel; i++) {
            children.add(new CompoundLevel(this, i));
        }

        return (CompoundLevel) children.get(sublevel);
    }

    /**
     * DOCUMENT ME!
     *
     * @param clev DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isSubLevelOf(CompoundLevel clev) {
        return parent == clev;
    }

    /**
     * DOCUMENT ME!
     *
     * @param other DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isSuccessorOf(CompoundLevel other) {
        return isSiblingTo(other) && (this.getTail() == other.getTail() + 1);
    }

    /**
     * DOCUMENT ME!
     *
     * @return the local level of the clev
     */
    public int getTail() {
        return ((Integer) clev.getLast()).intValue();
    }

    /**
     * only for drawing.
     *
     * @return DOCUMENT ME!
     */
    public boolean isUsed() {
        return isUsed;
    }

    /**
     * implements a lexicographic ordering
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object obj) {
        CompoundLevel other = (CompoundLevel) obj;
        Iterator itOther = other.clev.iterator();
        Iterator itThis = clev.iterator();
        while(itThis.hasNext()) {
            int valueThis = ((Integer) itThis.next()).intValue();
            if(itOther.hasNext()) {
                int valueOther = ((Integer) itOther.next()).intValue();
                if(valueThis < valueOther) {
                    return -1;
                } else if(valueThis > valueOther) {
                    return 1;
                } else {
                    //continue with while loop
                }
            } else {
                //other is anfangsstueck
                return 1; //other is less;
            }
        }

        return 0;
    }

    /**
     * for drawing only
     *
     * @param y DOCUMENT ME!
     * @param height DOCUMENT ME!
     */
    public void initializeAttributes(int y, int height) {
        isUsed = true;
        this.y = y;
        this.height = height;
        leftX = 0;
        leftXafterLabel = 0;
        rightX = 0;
    }

    /**
     * returns an iterator over the Integer objects.
     *
     * @return DOCUMENT ME!
     */
    public Iterator levelIterator() {
        return clev.iterator();
    }

    /**
     * is called at the beginning of drawing to reset the isUsed attribute
     */
    public void resetAttributes() {
        isUsed = false;
        for(Iterator it = children.iterator(); it.hasNext();) {
            ((CompoundLevel) it.next()).resetAttributes();
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String result = ""; //"[";
        Iterator it = clev.iterator();
        while(it.hasNext()) {
            result += it.next();
            if(it.hasNext()) {
                result += ".";
            }
        }

        //        result += "]";
        return result;
    }
}
