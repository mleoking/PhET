/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;

/**
 * The control node for the tail trait
 *
 * @author Jonathan Olson
 */
public class TailTraitNode extends TraitControlNode {

    public TailTraitNode() {
        super( NaturalSelectionResources.getImage( NaturalSelectionConstants.IMAGE_BUNNY_TAIL ) );
    }

    public Point2D getBunnyLocation( BigVanillaBunny bunny ) {
        return bunny.getTailPosition();
    }
}