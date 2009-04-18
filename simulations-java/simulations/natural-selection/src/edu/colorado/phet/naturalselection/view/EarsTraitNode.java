package edu.colorado.phet.naturalselection.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.naturalselection.NaturalSelectionResources;

public class EarsTraitNode extends TraitControlNode {

    public EarsTraitNode() {
        super( NaturalSelectionResources.getImage( "bunny_ears.png" ), NaturalSelectionResources.getImage( "bunny_ears_long.png" ) );
    }

    public Point2D getBunnyLocation( BigVanillaBunny bunny ) {
        return bunny.getEarsPosition();
    }
}
