/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.naturalselection.NaturalSelectionResources;

public class TailTraitNode extends TraitControlNode {

    public TailTraitNode() {
        super( NaturalSelectionResources.getImage( "bunny_tail.png" ) );
    }

    public Point2D getBunnyLocation( BigVanillaBunny bunny ) {
        return bunny.getTailPosition();
    }
}