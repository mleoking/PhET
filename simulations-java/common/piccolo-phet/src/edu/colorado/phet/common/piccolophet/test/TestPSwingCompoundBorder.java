
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * See Unfuddle #2018.
 * On Windows and Mac OS, the PSwing compound border doesn't look the same as the pure Swing border.
 * Problem occurs with both Java 5 and 6.
 * See screenshots attached to the ticket.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestPSwingCompoundBorder extends JFrame {

    public TestPSwingCompoundBorder() {
        super( "TestPSwingCompoundBorder" );
        setPreferredSize( new Dimension( 600, 400 )  );

        // canvas
        PhetPCanvas canvas = new PhetPCanvas( new PDimension( 600, 400 ) );
        PSwing pswing = new PSwing( new BorderedLabel( "PSwing" ) );
        pswing.scale( 2 );
        pswing.setOffset( 100, 100 );
        canvas.addWorldChild( pswing );
        
        // control panel
        JPanel controlPanel = new JPanel();
        controlPanel.add( new BorderedLabel( "Swing" ) );

        // layout similar to a PhET sim
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( controlPanel, BorderLayout.EAST );
        mainPanel.add( canvas, BorderLayout.CENTER );
        setContentPane( mainPanel );

        pack();
    }

    private static class BorderedLabel extends JLabel {

        private static final Border BORDER = new CompoundBorder( new LineBorder( Color.BLUE, 4 ), new CompoundBorder( new LineBorder( Color.BLACK, 2 ), new EmptyBorder( 5, 14, 5, 14 ) ) );
        
        public BorderedLabel( String label ) {
            super( label );
            setBorder( BORDER );
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new TestPSwingCompoundBorder();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }

}
