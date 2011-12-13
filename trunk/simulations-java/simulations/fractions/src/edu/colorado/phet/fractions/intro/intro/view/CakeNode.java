// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import edu.colorado.phet.fractions.FractionsResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Shows just one cake with the specified pieces.
 *
 * @author Sam Reid
 */
public class CakeNode extends PNode {
    public CakeNode( int denominator, int[] pieces ) {
        addChild( new PImage( FractionsResources.RESOURCES.getImage( "cake/cake_grid_" + denominator + ".png" ) ) );
        for ( int piece : pieces ) {
            addChild( new PImage( FractionsResources.RESOURCES.getImage( "cake/cake_" + denominator + "_" + piece + ".png" ) ) );
        }
    }
}