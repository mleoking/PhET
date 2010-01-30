package edu.colorado.phet.common.piccolophet.test.debug_piccolo2d_1_3;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
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
 * Radio buttons in a JPanel will not work at all.
 * Radio buttons directly on the canvas might work once.
 * <p>
 * This works fine with our SVN snapshot, does not work with Piccolo2D 1.3-rc1.
 * See #2141.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DebugPSwingExtremeScaling extends JFrame {
    
    // An extreme scale, similar to what's used in ScaleNode in eating-and-exercise.
    private static final double PSWING_SCALE = 0.02;
    
    public DebugPSwingExtremeScaling() {
        setSize( new Dimension( 1024, 768 ) );
        
        // canvas
        final PCanvas canvas = new PSwingCanvas( );
        PNode rootNode = new PNode();
        rootNode.setScale(1.0/PSWING_SCALE);
        canvas.getLayer().addChild(rootNode);
        canvas.setBorder( new LineBorder( Color.BLACK ) );
        canvas.removeInputEventListener( canvas.getZoomEventHandler() );
        canvas.removeInputEventListener( canvas.getPanEventHandler() );
        setContentPane( canvas );
        
        // PSwing panel
        PSwing panelNode = new PSwing( new UnitsPanel() );
        rootNode.addChild( panelNode );
        panelNode.setScale( PSWING_SCALE );
        
        // PSwing ON button
        final JRadioButton onButton = new JRadioButton( "ON" );
        onButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "actionPerformed " + onButton.getText() );
            }
        });
        PSwing onButtonNode = new PSwing( onButton );
        rootNode.addChild( onButtonNode );
        onButtonNode.setScale( PSWING_SCALE );
        
        // PSwing OFF button
        final JRadioButton offButton = new JRadioButton( "OFF" );
        offButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "actionPerformed " + offButton.getText() );
            }
        });
        PSwing offButtonNode = new PSwing( offButton );
        rootNode.addChild( offButtonNode );
        offButtonNode.setScale( PSWING_SCALE );
        
        ButtonGroup onOffGroup = new ButtonGroup();
        onOffGroup.add( onButton );
        onOffGroup.add( offButton );
        onButton.setSelected( true );
        
        // layout
        final double ySpacing = 0.05 * onButtonNode.getFullBoundsReference().getHeight();
        final double x = 1;
        double y = 1;
        panelNode.setOffset( x, y );
        y += panelNode.getFullBoundsReference().getHeight() + ySpacing;
        onButtonNode.setOffset( x, y );
        y += onButtonNode.getFullBoundsReference().getHeight() + ySpacing;
        offButtonNode.setOffset( x, y );
    }
    
    /*
     * A panel with English/Metric radio buttons.
     */
    private static class UnitsPanel extends JPanel {
        
        public UnitsPanel() {
            
            final JRadioButton englishButton = new JRadioButton( "English" );
            englishButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    System.out.println( "actionPerformed " + englishButton.getText() );
                }
            });
            
            final JRadioButton metricButton = new JRadioButton( "Metric" );
            metricButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    System.out.println( "actionPerformed " + metricButton.getText() );
                }
            });
            
            ButtonGroup group = new ButtonGroup();
            group.add( englishButton );
            group.add( metricButton );
            englishButton.setSelected( true );
            
            setBorder( new LineBorder( Color.BLACK ) );
            add( englishButton );
            add( metricButton );
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
