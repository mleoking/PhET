/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.Color;

/**
 * Strategies for coloring the faces of a BoxNode.
 * Assumes that the face colors can be derived from a base color.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface IBoxColorStrategy {

    public Color getTopColor( Color baseColor );

    public Color getFrontColor( Color baseColor );

    public Color getSideColor( Color baseColor );
    
    /**
     * Top uses the base color.
     * Front uses a darker shade of the top color.
     * Side uses a darker shade of the front color.
     */
    public static class ThreeColorStrategy implements IBoxColorStrategy {
        
        public Color getTopColor( Color baseColor ) {
            return baseColor;
        }

        public Color getFrontColor( Color baseColor ) {
            return getDarkerColor( getTopColor( baseColor ) );
        }

        public Color getSideColor( Color baseColor ) {
            return getDarkerColor( getFrontColor( baseColor ) );
        }
        
        /*
         * Color.darker doesn't preserve alpha, so we need our own method.
         * Notes that this results in the allocation of 2 Colors,
         * but that's not an issue for this sim.
         */
        protected Color getDarkerColor( Color color ) {
            Color c = color.darker();
            return new Color( c.getRed(), c.getGreen(), c.getBlue(), color.getAlpha() );
        }
    }
}
