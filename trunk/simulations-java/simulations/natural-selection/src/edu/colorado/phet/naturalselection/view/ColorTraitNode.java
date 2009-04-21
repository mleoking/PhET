/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;

/**
 * The control node for the color trait
 */
public class ColorTraitNode extends TraitControlNode {

    public ColorTraitNode() {
        super(
                NaturalSelectionResources.getImage( NaturalSelectionConstants.IMAGE_BUNNY_COLOR ),
                NaturalSelectionResources.getImage( NaturalSelectionConstants.IMAGE_BUNNY_COLOR_BROWN )
        );
    }

    public Point2D getBunnyLocation( BigVanillaBunny bunny ) {
        return bunny.getColorPosition();
    }
}