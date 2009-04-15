package edu.colorado.phet.naturalselection.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.naturalselection.NaturalSelectionResources;

public class ColorMutationNode extends MutationControlNode {

    public ColorMutationNode() {
        super( NaturalSelectionResources.getImage( "bunny_color.png" ) );
    }

    public Point2D getBunnyLocation( BigVanillaBunny bunny ) {
        return bunny.getColorPosition();
    }
}