// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import edu.colorado.phet.fractions.FractionsResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * @author Sam Reid
 */
public class WaterGlassNode extends PNode {
    public WaterGlassNode( Integer numerator, Integer denominator ) {
        addChild( new PImage( FractionsResources.Images.WATER_GLASS ) );
    }
}
