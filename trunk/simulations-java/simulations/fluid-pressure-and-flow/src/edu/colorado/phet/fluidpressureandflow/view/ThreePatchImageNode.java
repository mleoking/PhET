// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.*;
import java.awt.geom.AffineTransform;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * The ThreePatchImageNode combines 3 images with text to make a readout node.  The center image is stretched to
 * accommodate different lengths of string.
 *
 * @author Sam Reid
 */
public class ThreePatchImageNode extends PNode {
    public PImage leftPatch;
    public PImage centerPatch;
    public PImage rightPatch;

    public ThreePatchImageNode( Image left, Image middle, Image right ) {
        leftPatch = new PImage( left );
        addChild( leftPatch );

        centerPatch = new PImage( middle );
        addChild( centerPatch );

        rightPatch = new PImage( right );
        addChild( rightPatch );
    }

    public void setCenterComponentWidth( double width ) {
        //Stretch center piece to fit the text, it is always an exact fit and there is no minimum.
        centerPatch.setTransform( new AffineTransform() );//reset the centerPatch so that its bounds can be used to compute the right scale sx
        double sx = width / centerPatch.getFullBounds().getWidth();//how much to scale the centerPatch
        centerPatch.setTransform( AffineTransform.getScaleInstance( sx, 1 ) );
        centerPatch.translate( leftPatch.getFullBounds().getMaxX() / sx, 0 );

        //Position the right patch to the side of the stretched center patch so they don't overlap
        rightPatch.setOffset( leftPatch.getFullBounds().getWidth() + width, 0 );
    }
}
