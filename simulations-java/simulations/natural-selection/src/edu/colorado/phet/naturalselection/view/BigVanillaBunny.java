/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Displays the big vanilla bunny in the trait control panel. Contains coordinates for parts of the bunny
 *
 * @author Jonathan Olson
 */
public class BigVanillaBunny extends PNode {
    public BigVanillaBunny() {
        PImage child = NaturalSelectionResources.getImageNode( NaturalSelectionConstants.IMAGE_BIG_VANILLA_BUNNY );

        addChild( child );
    }

    //----------------------------------------------------------------------------
    // Position functions
    //----------------------------------------------------------------------------

    public Point2D getColorPosition() {
        return new Point2D.Double( 58 + getOffset().getX(), 75 + getOffset().getY() );
    }

    public Point2D getTeethPosition() {
        return new Point2D.Double( 70 + getOffset().getX(), 55 + getOffset().getY() );
    }

    public Point2D getTailPosition() {
        return new Point2D.Double( 3 + getOffset().getX(), 84 + getOffset().getY() );
    }

    public Point2D getEarsPosition() {
        return new Point2D.Double( 39 + getOffset().getX(), 17 + getOffset().getY() );
    }

    public Point2D getEyePosition() {
        return new Point2D.Double( 58 + getOffset().getX(), 41 + getOffset().getY() );
    }
}