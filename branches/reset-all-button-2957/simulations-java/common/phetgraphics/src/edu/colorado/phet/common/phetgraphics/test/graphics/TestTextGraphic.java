// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.phetgraphics.test.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic;

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

        PhetTextGraphic textGraphic = new PhetTextGraphic( apparatusPanel, new PhetFont( Font.BOLD, 24 ), "Test PhET Text & graphics", Color.blue, 0, 0 );
        apparatusPanel.addGraphic( textGraphic );
        textGraphic.setLocation( 100, 100 );

        Rectangle bounds = textGraphic.getBounds();
        PhetShapeGraphic boundGraphic = new PhetShapeGraphic( apparatusPanel, bounds, new BasicStroke( 1 ), Color.green );
        apparatusPanel.addGraphic( boundGraphic );

        int dy = bounds.y - 100;
        System.out.println( "dy = " + dy );
    }
}