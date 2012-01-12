// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.BasicStroke;
import java.awt.Color;

import javax.swing.JFrame;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * ZCrosshairNodeTester is the test harness for CrosshairNode.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ZFineCrosshairNodeTester {

    public static void main( String[] args ) {

        PCanvas canvas = new PCanvas();

        // Exercise the first constructor
        FineCrosshairNode crosshair1 = new FineCrosshairNode( 20, new BasicStroke( 2f ), Color.RED );
        crosshair1.setOffset( 100, 200 );
        canvas.getLayer().addChild( crosshair1 );

        // Exercise the second constructor
        FineCrosshairNode crosshair2 = new FineCrosshairNode( new PDimension( 20, 40 ), new BasicStroke( 1f ), Color.GREEN );
        crosshair2.setOffset( 200, 200 );
        canvas.getLayer().addChild( crosshair2 );

        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( 400, 400 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.show();
    }
}
