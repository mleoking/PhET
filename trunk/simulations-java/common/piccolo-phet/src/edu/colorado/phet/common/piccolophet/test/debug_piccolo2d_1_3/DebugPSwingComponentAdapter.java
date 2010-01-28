package edu.colorado.phet.common.piccolophet.test.debug_piccolo2d_1_3;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.LineBorder;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * See #2140
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DebugPSwingComponentAdapter extends JFrame {
    
    public DebugPSwingComponentAdapter() {
        setResizable( false );
        setSize( new Dimension( 400, 400 ) );
        
        // canvas
        final PCanvas canvas = new PSwingCanvas();
        canvas.setBorder( new LineBorder( Color.BLACK ) );
        canvas.removeInputEventListener( canvas.getZoomEventHandler() );
        canvas.removeInputEventListener( canvas.getPanEventHandler() );
        setContentPane( canvas );
        
        // choice panel, with two radio buttons
        JRadioButton radioButton1 = new JRadioButton( "choice1 " );
        final JRadioButton radioButton2 = new JRadioButton( "choice2 " );
        ButtonGroup group = new ButtonGroup();
        group.add( radioButton1 );
        group.add( radioButton2 );
        JPanel choicePanel = new JPanel();
        choicePanel.add( radioButton1 );
        choicePanel.add( radioButton2 );
        
        // PSwing wrapper for choice panel
        PSwing choicePanelNode = new PSwing( choicePanel );
        canvas.getLayer().addChild( choicePanelNode );
        
        // a check box related to choice2
        JCheckBox checkBox = new JCheckBox( "option related to choice2" );
        
        // PSwing wrapper for check box
        final PSwing checkBoxNode = new PSwing( checkBox ) {
            @Override
            public void setVisible( boolean b ) {
                System.out.println( "checkBoxNode.setVisible " + b );
                super.setVisible( b );
            }
        };
        canvas.getLayer().addChild( checkBoxNode );
        
        // event handling
        ActionListener buttonListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                checkBoxNode.setVisible( radioButton2.isSelected() );
            }
        };
        radioButton1.addActionListener( buttonListener );
        radioButton2.addActionListener( buttonListener );
        
        // layout
        choicePanelNode.setOffset( 50, 50 );
        checkBoxNode.setOffset( 50, 100 );
        
        // initial state
        radioButton1.setSelected( true );
        checkBoxNode.setVisible( radioButton2.isSelected() );
    }
    
    public static void main( String[] args ) {
        JFrame frame = new DebugPSwingComponentAdapter();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        // center on the screen
        Toolkit tk = Toolkit.getDefaultToolkit();
        int x = (int) ( tk.getScreenSize().getWidth() / 2 - frame.getWidth() / 2 );
        int y = (int) ( tk.getScreenSize().getHeight() / 2 - frame.getHeight() / 2 );
        frame.setLocation( x, y );
        frame.setVisible( true );
    }
}
