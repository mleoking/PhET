/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;

/**
 * The control node for the teeth trait
 *
 * @author Jonathan Olson
 */
public class TeethTraitNode extends TraitControlNode {

    public TeethTraitNode() {
        super( NaturalSelectionResources.getImage( NaturalSelectionConstants.IMAGE_BUNNY_TEETH ) );
    }

    public Point2D getBunnyLocation( BigVanillaBunny bunny ) {
        return bunny.getTeethPosition();
    }
}