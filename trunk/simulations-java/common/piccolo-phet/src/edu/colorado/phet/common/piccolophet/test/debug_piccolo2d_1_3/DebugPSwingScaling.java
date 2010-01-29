package edu.colorado.phet.common.piccolophet.test.debug_piccolo2d_1_3;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Demonstrates a Component that becomes unusable when it's PSwing is subjected to extreme scaling.
 * See #2141.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DebugPSwingScaling extends JFrame {
    
    // This is the same whacky size used in EatingAndExerciseCanvas.
    private static final PDimension CANVAS_SIZE = new PDimension( 15, 15 );
    
    // An extreme scale, similiar to what's used in ScaleNode in eating-and-exercise.
    private static final double PSWING_SCALE = 0.016;  // ScaleNode uses 0.004285714285714282
    
    public DebugPSwingScaling() {
        setResizable( false );
        setSize( new Dimension( 1024, 768 ) );
        
        // canvas
        final PhetPCanvas canvas = new PhetPCanvas( CANVAS_SIZE );
        canvas.setBorder( new LineBorder( Color.BLACK ) );
        canvas.removeInputEventListener( canvas.getZoomEventHandler() );
        canvas.removeInputEventListener( canvas.getPanEventHandler() );
        setContentPane( canvas );
        
        // choice panel, with two radio buttons
        final JRadioButton radioButton1 = new JRadioButton( "English " );
        radioButton1.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "actionPerformed " + radioButton1.getText() );
            }
        });
        final JRadioButton radioButton2 = new JRadioButton( "Metric " );
        radioButton2.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "actionPerformed " + radioButton2.getText() );
            }
        });
        ButtonGroup group = new ButtonGroup();
        group.add( radioButton1 );
        group.add( radioButton2 );
        JPanel choicePanel = new JPanel();
        choicePanel.add( radioButton1 );
        choicePanel.add( radioButton2 );
        
        // PSwing wrapper for choice panel
        PSwing choicePanelNode = new PSwing( choicePanel );
        canvas.addWorldChild( choicePanelNode );
        choicePanelNode.setOffset( 1, 1 );
        choicePanelNode.setScale( PSWING_SCALE );
        
        // initial state
        radioButton1.setSelected( true );
    }
    
    public static void main( String[] args ) {
        JFrame frame = new DebugPSwingScaling();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        // center on the screen
        Toolkit tk = Toolkit.getDefaultToolkit();
        int x = (int) ( tk.getScreenSize().getWidth() / 2 - frame.getWidth() / 2 );
        int y = (int) ( tk.getScreenSize().getHeight() / 2 - frame.getHeight() / 2 );
        frame.setLocation( x, y );
        frame.setVisible( true );
    }
}
