// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.test.debug_piccolo2d_1_3;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.LineBorder;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * Demonstrates a Component that becomes unusable when it's PSwing is subjected to extreme scaling.
 * I suspect transform precision errors are resulting in events not being delivered to the Component.
 * <p/>
 * This works fine with PhET's Piccolo snapshot (r390), does not work with Piccolo2D 1.3-rc1.
 * See also PhET Unfuddle #2141, Piccolo Issue 159.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DebugPSwingExtremeScaling extends JFrame {

    // An extreme scale, similar to what's used in ScaleNode in eating-and-exercise.
    private static final double PSWING_SCALE = 0.02;

    public DebugPSwingExtremeScaling() {
        setSize( new Dimension( 1024, 768 ) );

        // canvas
        final PCanvas canvas = new PSwingCanvas();
        canvas.setBorder( new LineBorder( Color.BLACK ) );
        canvas.removeInputEventListener( canvas.getZoomEventHandler() );
        canvas.removeInputEventListener( canvas.getPanEventHandler() );
        setContentPane( canvas );

        // root node, uses the inverse of our extreme scale (PSWING_SCALE)
        PNode rootNode = new PNode();
        rootNode.setScale( 1.0 / PSWING_SCALE );
        canvas.getLayer().addChild( rootNode );

        // PSwing panel with some radio buttons
        PSwing panelNode = new PSwing( new UnitsPanel() );
        rootNode.addChild( panelNode );
        panelNode.setScale( PSWING_SCALE );
        panelNode.setOffset( 1, 1 );
    }

    /*
    * A JPanel with English/Metric radio buttons.
    */
    private static class UnitsPanel extends JPanel {

        public UnitsPanel() {
            setBorder( new LineBorder( Color.BLACK ) );

            final JRadioButton englishButton = new JRadioButton( "English" );
            englishButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    System.out.println( "actionPerformed " + englishButton.getText() );
                }
            } );

            final JRadioButton metricButton = new JRadioButton( "Metric" );
            metricButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    System.out.println( "actionPerformed " + metricButton.getText() );
                }
            } );

            ButtonGroup group = new ButtonGroup();
            group.add( englishButton );
            group.add( metricButton );

            add( englishButton );
            add( metricButton );

            // default state
            englishButton.setSelected( true );
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new DebugPSwingExtremeScaling();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        // center on the screen
        Toolkit tk = Toolkit.getDefaultToolkit();
        int x = (int) ( tk.getScreenSize().getWidth() / 2 - frame.getWidth() / 2 );
        int y = (int) ( tk.getScreenSize().getHeight() / 2 - frame.getHeight() / 2 );
        frame.setLocation( x, y );
        frame.setVisible( true );
    }
}
