package edu.colorado.phet.naturalselection.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.naturalselection.NaturalSelectionResources;

public class TailMutationNode extends MutationControlNode {

    public TailMutationNode() {
        super( NaturalSelectionResources.getImage( "bunny_tail.png" ) );
    }

    public Point2D getBunnyLocation( BigVanillaBunny bunny ) {
        return bunny.getTailPosition();
    }
}