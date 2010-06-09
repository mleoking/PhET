/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import java.util.Arrays;
import java.util.Comparator;

import edu.umd.cs.piccolo.PNode;

/**
 * Strategies for determining the rendering order of molecule types.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface IMoleculeLayeringStrategy {
    
    public void setRenderingOrder( PNode parentHA, PNode parentA, PNode parentH3O, PNode parentOH, PNode parentH2O );
    
    /**
     * Layering strategy is fix, back-to-front order is: H2O reactant product H3O OH 
     */
    public static class FixedMoleculeLayeringStrategy implements IMoleculeLayeringStrategy {
        
        public void setRenderingOrder( PNode parentReactant, PNode parentProduct, PNode parentH3O, PNode parentOH, PNode parentH2O ) {
            parentOH.moveToBack();
            parentH3O.moveToBack();
            parentProduct.moveToBack();
            parentReactant.moveToBack();
            parentH2O.moveToBack();
        }
    }
    
    /**
     * Layers are sorted so that majority specified are in back, minority in front.
     * Water is always in back.
     */
    public static class SortedMoleculeLayeringStrategy implements IMoleculeLayeringStrategy {

        public void setRenderingOrder( PNode parentHA, PNode parentA, PNode parentH3O, PNode parentOH, PNode parentH2O ) {
            PNode[] dotParents = new PNode[] { parentHA, parentA, parentH3O, parentOH };
            Arrays.sort( dotParents, new ChildrenCountComparator() );
            for ( int i = 0; i < dotParents.length; i++ ) {
                dotParents[i].moveToBack();
            }
            parentH2O.moveToBack(); // H2O is always in the background
        }
        
        // Sorts PNodes based on how many children they have (least to most).
        private static class ChildrenCountComparator implements Comparator<PNode> {

            public int compare( final PNode node1, final PNode node2 ) {
                final int count1 = node1.getChildrenCount();
                final int count2 = node2.getChildrenCount();
                if ( count1 > count2 ) {
                    return 1;
                }
                else if ( count1 < count2 ) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        }
    }
}
