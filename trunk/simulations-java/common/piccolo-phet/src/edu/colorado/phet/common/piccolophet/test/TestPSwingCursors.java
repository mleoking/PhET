// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.test;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Demonstrates a problem with Cursors and PSwing.
 * If a PSwing's JComponent has a cursor set (via setCursor), the cursor is ignored.
 * In some cases this can be worked around by setting adding a CursorHandler to
 * the PSwing; in some cases, there is no workaround.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestPSwingCursors extends JFrame {

    private static final Cursor HAND_CURSOR = Cursor.getPredefinedCursor( Cursor.HAND_CURSOR );

    public TestPSwingCursors() {

        // JButton off the canvas.
        // This demonstrates typical Swing cursor behavior, when Piccolo isn't involved.
        JButton button1 = new JButton( "button1 with hand cursor" );
        button1.setCursor( HAND_CURSOR );
        JPanel controlPanel = new JPanel();
        controlPanel.add( button1 );

        // JButton directly on canvas.
        // We could workaround by adding a CursorHandler to button1Wrapper.
        JButton button2 = new JButton( "button2 with hand cursor" );
        button2.setCursor( HAND_CURSOR );
        PSwing button1Wrapper = new PSwing( button2 );
        button1Wrapper.setOffset( 50, 50 );

        // JButton inside a JPanel on the canvas.
        // No workaround, adding a CursorHandler to panelWrapper affects the entire panel.
        JButton button3 = new JButton( "button3 with hand cursor" );
        button3.setCursor( HAND_CURSOR );
        JLabel noCursorLabel = new JLabel( "no cursor on this label" );
        JPanel panel = new VerticalLayoutPanel();
        panel.setOpaque( false );
        panel.setBorder( new TitledBorder( "Panel" ) );
        panel.add( button3 );
        panel.add( noCursorLabel );
        PSwing panelWrapper = new PSwing( panel );
        panelWrapper.setOffset( 50, button1Wrapper.getFullBoundsReference().getMaxY() + 20 );

        PhetPCanvas canvas = new PhetPCanvas();
        canvas.getLayer().addChild( button1Wrapper );
        canvas.getLayer().addChild( panelWrapper );

        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( canvas, BorderLayout.CENTER );
        mainPanel.add( controlPanel, BorderLayout.SOUTH );

        getContentPane().add( mainPanel );
    }

    public static void main( String args[] ) {
        TestPSwingCursors frame = new TestPSwingCursors();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( new Dimension( 400, 400 ) );
        frame.setVisible( true );
    }
}
