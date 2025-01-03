/*==============================================================================
*
*   CompoundLevelTest.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: CompoundLevelTest.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.test;

import org.visnacom.sugiyama.model.CompoundLevel;

import junit.framework.TestCase;

/**
 *
 */
public class CompoundLevelTest extends TestCase {
    //~ Methods ================================================================

    /**
     *
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.swingui.TestRunner.run(CompoundLevelTest.class);
    }

    /**
     *
     */
    public final void testIsInitialSubstringOf1() {
        CompoundLevel root = CompoundLevel.getClevForRoot();
        CompoundLevel clev =
            root.getSubLevel(2).getSubLevel(1)
                         .getSubLevel(1).getSubLevel(3);
        CompoundLevel clev2 =
            root.getSubLevel(2).getSubLevel(1)
                         .getSubLevel(1).getSubLevel(3);
        assertFalse(clev.isInitialSubstringOf(clev2));
    }

    /**
     *
     */
    public final void testIsInitialSubstringOf2() {
        CompoundLevel root = CompoundLevel.getClevForRoot();
        CompoundLevel clev =
            root.getSubLevel(2).getSubLevel(1)
                         .getSubLevel(1).getSubLevel(3);
        CompoundLevel clev2 =
            root.getSubLevel(2).getSubLevel(1)
                         .getSubLevel(2).getSubLevel(3).getSubLevel(4);
        assertFalse(clev.isInitialSubstringOf(clev2));
    }

    /**
     *
     */
    public final void testIsInitialSubstringOf3() {
        CompoundLevel root = CompoundLevel.getClevForRoot();
        CompoundLevel clev =
            root.getSubLevel(2).getSubLevel(1)
                         .getSubLevel(1).getSubLevel(3);
        CompoundLevel clev2 =
            root.getSubLevel(2).getSubLevel(1)
                         .getSubLevel(1).getSubLevel(3).getSubLevel(1);
        assertTrue(clev.isInitialSubstringOf(clev2));
        assertFalse(clev2.isInitialSubstringOf(clev));
    }

    /**
     *
     */
    public final void testIsInitialSubstringOf4() {
        CompoundLevel root = CompoundLevel.getClevForRoot();
        CompoundLevel clev =
            root.getSubLevel(2).getSubLevel(1)
                         .getSubLevel(1).getSubLevel(3);
        CompoundLevel clev2 =
            root.getSubLevel(2).getSubLevel(1)
                         .getSubLevel(1).getSubLevel(2);
        assertFalse(clev.isInitialSubstringOf(clev2));
    }

    /**
     *
     */
    public final void testIsInitialSubstringOf5() {
        CompoundLevel root = CompoundLevel.getClevForRoot();
        CompoundLevel clev = root.getSubLevel(2);
        CompoundLevel clev2 =
            root.getSubLevel(2).getSubLevel(1)
                         .getSubLevel(1).getSubLevel(3).getSubLevel(1);
        assertTrue(clev.isInitialSubstringOf(clev2));
        assertFalse(clev2.isInitialSubstringOf(clev));
    }

    /**
     *
     */
    public final void testIsSiblingTo() {
        CompoundLevel root = CompoundLevel.getClevForRoot();
        CompoundLevel clev = root.getSubLevel(2);
        CompoundLevel clev2 =root.getSubLevel(2).getSubLevel(1)
                         .getSubLevel(1).getSubLevel(3).getSubLevel(1);
        assertFalse(clev.isSiblingTo(clev2));
        assertFalse(clev2.isSiblingTo(clev));
    }

    /**
     *
     */
    public final void testIsSiblingTo2() {
        CompoundLevel root = CompoundLevel.getClevForRoot();
        CompoundLevel clev =root.getSubLevel(2).getSubLevel(1)
                         .getSubLevel(1).getSubLevel(3);
        CompoundLevel clev2 =
           root.getSubLevel(2).getSubLevel(1)
                         .getSubLevel(1).getSubLevel(2);
        assertTrue(clev.isSiblingTo(clev2));
        assertTrue(clev2.isSiblingTo(clev));
    }

    /**
     *
     */
    public final void testIsSiblingTo3() {
        CompoundLevel root = CompoundLevel.getClevForRoot();
        CompoundLevel clev =root.getSubLevel(2).getSubLevel(1)
                         .getSubLevel(1).getSubLevel(3).getSubLevel(1);
        CompoundLevel clev2 =root.getSubLevel(2).getSubLevel(1)
                         .getSubLevel(1).getSubLevel(2).getSubLevel(1);
        assertFalse(clev.isSiblingTo(clev2));
        assertFalse(clev2.isSiblingTo(clev));
    }
}
