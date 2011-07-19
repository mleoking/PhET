// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.common.piccolophet.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.JFrame;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * User: Sam Reid
 * Date: Aug 1, 2005
 * Time: 7:58:04 AM
 */

public class TestPhetPCanvas {
    private JFrame frame;

    public TestPhetPCanvas() {
        frame = new JFrame();
        PhetPCanvas phetPCanvas = new PhetPCanvas();
        frame.setContentPane( phetPCanvas );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 800, 800 );

        PPath path = new PPath( new Rectangle( 400, 400, 100, 100 ), new BasicStroke( 3 ) );
        path.setPaint( Color.blue );
        path.setStrokePaint( Color.black );
        phetPCanvas.addWorldChild( path );
    }

    public static void main( String[] args ) {
        TestPhetPCanvas testPhetPCanvas = new TestPhetPCanvas();
        testPhetPCanvas.start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
