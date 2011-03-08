// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.*;
import java.awt.geom.AffineTransform;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * ThreeImageNode combines 3 images (left,center,right) into a single graphic, stretching the center component horizontally to the specified width.
 *
 * @author Sam Reid
 * @author Chris Malley
 */
public class ThreeImageNode extends PNode {
    public final PImage leftPatch;
    public final PImage centerPatch;
    public final PImage rightPatch;

    /*
     * Constructor that uses the leaves the center image size unchanged.
     */
    public ThreeImageNode( Image left, Image center, Image right ) {
        this( left, center, right, center.getWidth( null ) );
    }

    /*
     * Constructs a ThreeImageNode with the specified images, and the specified width for the center component.
     */
    public ThreeImageNode( Image left, Image center, Image right, double centerWidth ) {
        leftPatch = new PImage( left );
        addChild( leftPatch );

        centerPatch = new PImage( center );
        addChild( centerPatch );

        rightPatch = new PImage( right );
        addChild( rightPatch );

        setCenterWidth( centerWidth );
    }

    public void setCenterWidth( double centerWidth ) {
        //Stretch center piece to fit the text, it is always an exact fit and there is no minimum.
        centerPatch.setTransform( new AffineTransform() );//reset the centerPatch so that its bounds can be used to compute the right scale sx
        double sx = centerWidth / centerPatch.getFullBounds().getWidth();//how much to scale the centerPatch
        centerPatch.setTransform( AffineTransform.getScaleInstance( sx, 1 ) );
        centerPatch.translate( leftPatch.getFullBounds().getMaxX() / sx, 0 );

        //Position the right patch to the side of the stretched center patch so they don't overlap
        rightPatch.setOffset( leftPatch.getFullBounds().getWidth() + centerWidth, 0 );
    }
}
