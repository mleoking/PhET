/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.naturalselection.NaturalSelectionResources;

public class TeethTraitNode extends TraitControlNode {

    public TeethTraitNode() {
        super( NaturalSelectionResources.getImage( "bunny_teeth.png" ) );
    }

    public Point2D getBunnyLocation( BigVanillaBunny bunny ) {
        return bunny.getTeethPosition();
    }
}