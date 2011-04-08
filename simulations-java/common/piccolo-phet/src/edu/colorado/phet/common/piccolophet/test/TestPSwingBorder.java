// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.test;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * On Windows and Mac OS, with Java 5 and Java 6, a JComponent with a border
 * is rendered oddly by PSwing.  This example compares PSwing and pure-Swing rendering.
 * The PSwing border contains horizontal and vertical lines that shouldn't be there,
 * when the PSwing is scaled up, visible at some scaling values and not at others.
 * <p/>
 * This is caused by the implementation of LineBorder.paintBorder.
 * The workaround is to use a MatteBorder (see APPLY_WORKAROUND).
 * Note that MatteBorder does not support rounded borders, while LineBorder does.
 * <p/>
 * PhET Unfuddle ticket = #2018
 * Piccolo issue = #213
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestPSwingBorder extends JFrame {

    private static final boolean APPLY_WORKAROUND = true;

    public TestPSwingBorder() {
        super( "TestPSwingBorder" );

        // PSwing
        PSwingCanvas canvas = new PSwingCanvas();
        PSwing pswing = new PSwing( new BorderedLabel( "PSwing" ) );
        pswing.scale( 1.5 );
        final double margin = 20;
        pswing.setOffset( margin, margin );
        canvas.getLayer().addChild( pswing );
        canvas.setPreferredSize( new Dimension( (int) ( pswing.getFullBounds().getWidth() + ( 2 * margin ) ), (int) ( pswing.getFullBounds().getHeight() + ( 2 * margin ) ) ) );

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
        public BorderedLabel( String text ) {
            super( text );
            setFont( new Font( "Default", Font.PLAIN, 24 ) );
            if ( APPLY_WORKAROUND ) {
                setBorder( new MatteBorder( 10, 10, 10, 10, Color.BLUE ) );
            }
            else {
                setBorder( new LineBorder( Color.BLUE, 10 ) );
            }
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new TestPSwingBorder();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
