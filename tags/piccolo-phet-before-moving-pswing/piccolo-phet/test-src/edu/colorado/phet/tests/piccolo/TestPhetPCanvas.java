/* Copyright 2004, Sam Reid */
package edu.colorado.phet.tests.piccolo;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.nodes.PPath;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 1, 2005
 * Time: 7:58:04 AM
 * Copyright (c) Aug 1, 2005 by Sam Reid
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
