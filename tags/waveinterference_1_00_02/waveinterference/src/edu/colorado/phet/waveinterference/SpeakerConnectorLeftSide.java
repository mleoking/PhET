/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.phetcommon.VerticalConnector;
import edu.colorado.phet.waveinterference.view.ImageOscillatorPNode;
import edu.umd.cs.piccolo.PNode;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: May 18, 2006
 * Time: 6:14:38 PM
 * Copyright (c) May 18, 2006 by Sam Reid
 */

public class SpeakerConnectorLeftSide extends VerticalConnector {
    private ImageOscillatorPNode dst;

    public SpeakerConnectorLeftSide( PNode src, ImageOscillatorPNode dst ) {
        super( src, dst );
        this.dst = dst;
        super.update();
    }

    /**
     * This is a workaround since we can't use the full bounds of the speaker graphic due to the oscillating component.
     *
     * @param r1c
     * @param r2c
     */
    protected void updateShape( Point2D r1c, Point2D r2c ) {
        if( dst != null ) {
            double yMin = Math.min( r1c.getY(), r2c.getY() );
            double yMax = Math.max( r1c.getY(), r2c.getY() );
            double height = yMax - yMin;
//            System.out.println( "dst.getFullBounds() = " + dst.getFullBounds() );
//            System.out.println( "dst.getImageNode().getFullBounds() = " + dst.getImageNode().getFullBounds() );
            Rectangle2D r = dst.getImageNode().getFullBounds();
//            System.out.println( "r = " + r );
            dst.getImageNode().localToGlobal( r );
            globalToLocal( r );
            localToParent( r );
//            System.out.println( "r2 = " + r );
//        double x=dst.getImageNode().getGlobalFullBounds().getX();
            double x = r.getX();
//            System.out.println( "x = " + x );
//        x+=dst.getOffset().getX();
//        double x = getDestination().getFullBounds().getX();
//        double origX = r1c.getX() - getDestination().getFullBounds().getWidth() / 2.0;
            Rectangle2D.Double rect = new Rectangle2D.Double( x, yMin, 20, height );
            super.setPathTo( rect );
        }
    }
}
