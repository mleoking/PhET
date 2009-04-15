package edu.colorado.phet.naturalselection.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.naturalselection.NaturalSelectionResources;

public class EyesMutationNode extends MutationControlNode {

    public EyesMutationNode() {
        super( NaturalSelectionResources.getImage( "bunny_eye.png" ) );
    }

    public Point2D getBunnyLocation( BigVanillaBunny bunny ) {
        return bunny.getEyePosition();
    }
}