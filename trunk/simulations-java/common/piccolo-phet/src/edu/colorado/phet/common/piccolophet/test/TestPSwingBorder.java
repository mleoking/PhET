
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;

import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * On Windows and Mac OS, with Java 5 and Java 6, a JComponent with a border
 * is rendered oddly by PSwing.  This example compares PSwing and pure-Swing rendering.
 * The PSwing border contains horizontal and vertical lines that shouldn't be there,
 * when the PSwing is scaled up, visible at some scaling values and not at others.
 * <p>
 * PhET internal ticket = #2018
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestPSwingBorder extends JFrame {

    public TestPSwingBorder() {
        super( "TestPSwingBorder" );

        // PSwing
        PSwingCanvas canvas = new PSwingCanvas();
        PSwing pswing = new PSwing( new BorderedLabel( "PSwing" ) );
        pswing.scale( 1.5 );
        final double margin = 20;
        pswing.setOffset( margin, margin );
        canvas.getLayer().addChild( pswing );
        canvas.setPreferredSize( new Dimension( (int)( pswing.getFullBounds().getWidth() + ( 2 * margin ) ), (int)( pswing.getFullBounds().getHeight() + ( 2 * margin ) ) )  );

        // pure Swing
        JPanel swingPanel = new JPanel();
        swingPanel.add( new BorderedLabel( "Swing" ) );

        // layout
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( canvas, BorderLayout.CENTER );
        mainPanel.add( swingPanel, BorderLayout.SOUTH );
        setContentPane( mainPanel );

        pack();
    }

    private static class BorderedLabel extends JLabel {
        public BorderedLabel( String label ) {
            super( label );
            setFont( new Font( "Default", Font.PLAIN, 24 ) );
            setBorder( new LineBorder( Color.BLUE, 10 ) );
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new TestPSwingBorder();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
