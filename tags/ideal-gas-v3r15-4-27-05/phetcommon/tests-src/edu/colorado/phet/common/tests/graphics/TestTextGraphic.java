/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.tests.graphics;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SwingUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Draws a rectangle centered at (100,100) for purposes of aligning
 * a PhetTextGraphic and its bounding box.
 */

public class TestTextGraphic {
    public static void main( String[] args ) {
        ApparatusPanel apparatusPanel = new ApparatusPanel();
        JFrame frame = new JFrame( TestTextGraphic.class.getName() );
        frame.setContentPane( apparatusPanel );
        frame.setSize( 800, 800 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );

        PhetShapeGraphic phetShapeGraphic = new PhetShapeGraphic( apparatusPanel, new Rectangle( 98, 98, 4, 4 ), Color.red );
        apparatusPanel.addGraphic( phetShapeGraphic );

        PhetTextGraphic textGraphic = new PhetTextGraphic( apparatusPanel, new Font( "Lucida Sans", Font.BOLD, 24 ), "Test PhET Text & graphics", Color.blue, 0, 0 );
        apparatusPanel.addGraphic( textGraphic );
        textGraphic.setLocation( 100, 100 );

        Rectangle bounds = textGraphic.getBounds();
        PhetShapeGraphic boundGraphic = new PhetShapeGraphic( apparatusPanel, bounds, new BasicStroke( 1 ), Color.green );
        apparatusPanel.addGraphic( boundGraphic );

        int dy = bounds.y - 100;
        System.out.println( "dy = " + dy );
    }
}
