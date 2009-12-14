
package edu.colorado.phet.reactantsproductsandleftovers.test;

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

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * See Unfuddle #2018.
 * On Mac OS 10.6.2 with Java 1.6.0_17, the PSwing compound border doesn't look the same as the pure Swing border.
 * See screenshot attached to the ticket.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestCompoundBorderMacOS extends JFrame {

    public TestCompoundBorderMacOS() {
        super( "TestCompoundBorderMacOS" );
        setPreferredSize( new Dimension( 600, 400 )  );

        JPanel controlPanel = new JPanel();
        controlPanel.add( new BorderedLabel( "Swing" ) );

        PhetPCanvas canvas = new PhetPCanvas( new PDimension( 600, 400 ) );
        PSwing pswing = new PSwing( new BorderedLabel( "PSwing" ) );
        pswing.scale( 2 );
        pswing.setOffset( 100, 100 );
        canvas.addWorldChild( pswing );

        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( canvas, BorderLayout.CENTER );
        mainPanel.add( controlPanel, BorderLayout.EAST );
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
        JFrame frame = new TestCompoundBorderMacOS();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }

}
