// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.common.piccolophet.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.io.IOException;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.event.BoundedDragHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * User: Sam Reid
 * Date: Aug 26, 2005
 * Time: 9:16:40 PM
 */

public class TestBoundedDragHandler {
    private JFrame frame;
    private PCanvas piccoloCanvas;

    public TestBoundedDragHandler() throws IOException {

        //Initialize Frame
        frame = new JFrame( "Simple Piccolo Test" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 600, 600 );
        SwingUtils.centerWindowOnScreen( frame );

        //Initialize Piccolo Canvas
        piccoloCanvas = new PCanvas();
        frame.setContentPane( piccoloCanvas );
//        piccoloCanvas.getLayer().setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        //PPath is the shape primitive.
        Rectangle rectangleBounds = new Rectangle( 10, 10, 300, 300 );
        PPath path = new PPath( rectangleBounds );
        path.setStroke( new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1, new float[] { 8, 12 }, 0 ) );
        path.setStrokePaint( Color.black );
        piccoloCanvas.getLayer().addChild( path );

        //Add content to Canvas
        final PText pText = new PText( "Hello Piccolo" );
        piccoloCanvas.getLayer().addChild( pText );
        pText.setOffset( 100, 100 );
//        pText.addInputEventListener( new DefaultDragHandler() );
        pText.addInputEventListener( new BoundedDragHandler( pText, path ) );
        pText.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        pText.rotate( Math.PI );
        pText.setPaint( Color.green );
//       pText.scale(3);

        piccoloCanvas.getLayer().scale( 0.5 );

        PBounds pathBounds = path.getGlobalFullBounds();
        System.out.println( "pathBounds[global] = " + pathBounds );
        piccoloCanvas.setPanEventHandler( null );

        PActivity pActivity = new PActivity( -1, 2000 ) {
            protected void activityStep( long elapsedTime ) {
                super.activityStep( elapsedTime );
                pText.rotateAboutPoint( Math.PI / 32, pText.getWidth() / 2, pText.getHeight() / 2 );
            }
        };
        piccoloCanvas.getRoot().addActivity( pActivity );
//        piccoloCanvas.setZoomEventHandler( null );
    }

    public static void main( String[] args ) throws IOException {
        new TestBoundedDragHandler().start();
    }

    private void start() {
        frame.show();
    }
}
