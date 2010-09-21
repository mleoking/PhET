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
     * Top and front use the base color.
     * Side uses a darker shade of the front color.
     */
    public static class TwoColorStrategy implements IBoxColorStrategy {

        public Color getTopColor( Color baseColor ) {
            return baseColor;
        }

        public Color getFrontColor( Color baseColor ) {
            return getTopColor( baseColor );
        }

        public Color getSideColor( Color baseColor ) {
            return getFrontColor( baseColor ).darker();
        }
    }

    /**
     * Top uses the base color.
     * Front uses a darker shade of the top color.
     * Side uses a darker shade of the front color.
     */
    public static class ThreeColorStrategy extends TwoColorStrategy {

        public Color getTopColor( Color baseColor ) {
            return baseColor;
        }

        public Color getFrontColor( Color baseColor ) {
            return getTopColor( baseColor ).darker();
        }

        public Color getSideColor( Color baseColor ) {
            return getFrontColor( baseColor ).darker();
        }
    }
}
