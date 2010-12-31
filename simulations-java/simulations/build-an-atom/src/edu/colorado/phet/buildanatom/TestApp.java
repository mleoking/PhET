/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.geom.Rectangle2D;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

/**
 *
 * @author John Blanco
 */
public class TestApp {
    public static void main( String[] args ) {

        final JCheckBox replaceMeCheckBox = new JCheckBox("Temporary check box");

        // Control panel
        JPanel controlPanel = new JPanel(new GridLayout(1, 1));
        controlPanel.add( replaceMeCheckBox );

        // Canvas
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 400, 400 ) );
        canvas.addWorldChild( new PhetPPath( new Rectangle2D.Double( 10, 10, 20, 20 ), Color.PINK ) );

        // App panel
        JPanel appPanel = new JPanel();
        appPanel.add( canvas );
        appPanel.add( controlPanel );

        // Frame
        JFrame frame = new JFrame();
        frame.setContentPane( appPanel );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
