/* Copyright 2004, Sam Reid */
package edu.colorado.phet.tests.piccolo;

import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SwingUtils;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.IOException;

/**
 * A simple demonstration to depict the three piccolo terminals & usage.
 */

public class DemonstratePiccoloTerminals {
    private JFrame frame;
    private PCanvas piccoloCanvas;

    public DemonstratePiccoloTerminals() throws IOException {

        //Initialize Frame
        frame = new JFrame( "Simple Piccolo Test" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 600, 600 );
        SwingUtils.centerWindowOnScreen( frame );

        //Initialize Piccolo Canvas
        piccoloCanvas = new PCanvas();
        frame.setContentPane( piccoloCanvas );
//        piccoloCanvas.getLayer().setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        //Add content to Canvas
        PText pText = new PText( "Hello Piccolo" );
        piccoloCanvas.getLayer().addChild( pText );

        //PPath is the shape primitive.
        PPath path = new PPath( new Ellipse2D.Double( 0, 0, 50, 50 ) );
        path.setPaint( Color.blue );
        path.setStroke( new BasicStroke( 6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1, new float[]{8, 12}, 0 ) );
        path.setStrokePaint( new GradientPaint( 0, 0, Color.red, 50, 50, Color.green ) );
        path.setOffset( 10, 100 );
        piccoloCanvas.getLayer().addChild( path );

        //PImage is the image primitive
        PImage image = new PImage( ImageLoader.loadBufferedImage( PhetLookAndFeel.PHET_LOGO_120x50 ) );
        image.setOffset( 100, 50 );
        image.rotateInPlace( Math.PI / 16 );
        piccoloCanvas.getLayer().addChild( image );

//        piccoloCanvas.setPanEventHandler( null);
//        piccoloCanvas.setZoomEventHandler( null);
    }

    public static void main( String[] args ) throws IOException {
        new DemonstratePiccoloTerminals().start();
    }

    private void start() {
        frame.show();
    }
}
