// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.common.piccolophet.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;

import javax.swing.JFrame;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.handles.PBoundsHandle;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 8:15:07 AM
 */

public class TestActivities {
    static long lastElapsedTime = 0;
    static double totalAngle = 0;

    public static void main( String[] args ) {
        PCanvas pCanvas = new PCanvas();
        final PText pText = new PText( "Hello PhET\nTesting" );

        PPath sticky = PPath.createRectangle( 0, 0, 50, 50 );

        sticky.setPaint( Color.YELLOW );
        sticky.setStroke( new BasicStroke( 2 ) );
        PBoundsHandle.addBoundsHandlesTo( sticky );
        pCanvas.getLayer().addChild( PPath.createRectangle( 0, 0, 100, 80 ) );
        pCanvas.getCamera().addChild( sticky );

        pCanvas.getLayer().addChild( pText );
        JFrame frame = new JFrame( "Test Piccolo" );
        pCanvas.getCamera().translateView( 50, 50 );
//        pText.animateToPositionScaleRotation( 0,0,1,Math.PI,5000);
//        pText.animateToTransform( AffineTransform.getRotateInstance( Math.PI),1000 );
        final Point center = new Point( (int) ( pText.getWidth() / 2 ), (int) ( pText.getHeight() / 2 ) );
        final int aStepRate = 30;
        PActivity activity = new PActivity( Long.MAX_VALUE / 1000, aStepRate ) {
            protected void activityStep( long elapsedTime ) {
                super.activityStep( elapsedTime );
                if ( lastElapsedTime != 0 ) {
                    long dt = elapsedTime - lastElapsedTime;
//                AffineTransform rot = AffineTransform.getRotateInstance( Math.PI / 128 );
                    System.out.println( "elapsedTime = " + elapsedTime + ", dt=" + dt + ", totalAngle=" + totalAngle );
                    double theta = Math.PI / 128 / aStepRate * dt;

                    if ( totalAngle > Math.PI * 2 ) {
                        theta = Math.PI * 2 - totalAngle;
                        pText.rotateAboutPoint( theta, center );
                        super.terminate();
                    }
                    else {
                        pText.rotateAboutPoint( theta, center );
                    }
                    totalAngle += theta;
                }
                lastElapsedTime = elapsedTime;
            }
        };
        pText.getRoot().addActivity( activity );


        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( pCanvas );
        frame.setSize( 400, 600 );
        frame.setVisible( true );
    }
}
