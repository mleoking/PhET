/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.piccolophet.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Demonstrates problems with JTextField and PSwing.
 * The JTextField gains focus for every click in the text field,
 * even when the text field already has focus.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestPSwingTextField extends JFrame {

    public TestPSwingTextField() {
        
        // JTextField off the canvas.
        // This demonstrates typical Swing cursor behavior, when Piccolo isn't involved.
        final JTextField textField1 = new JTextField( "textField1 outside of Piccolo" );
        textField1.setColumns( 20 );
        textField1.addFocusListener( new FocusAdapter() {
            public void focusGained( FocusEvent e ) {
                System.out.println( "textField1 gained focus" );
                textField1.selectAll();
            }
        });
        JPanel controlPanel = new JPanel();
        controlPanel.add( textField1 );
        
        // JTextField on the canvas.
        final JTextField textField2 = new JTextField( "textField2 on a PCanvas" );
        textField2.setColumns( 20 );
        textField2.addFocusListener( new FocusAdapter() {
            public void focusGained( FocusEvent e ) {
                System.out.println( "textField2 gained focus" );
                textField2.selectAll();
            }
        });
        PSwing textField2Wrapper = new PSwing( textField2 );
        textField2Wrapper.setOffset( 10, 50 );
        
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.getLayer().addChild( textField2Wrapper );
        
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( canvas, BorderLayout.CENTER );
        mainPanel.add( controlPanel, BorderLayout.SOUTH );
        
        getContentPane().add( mainPanel );
    }
    
    public static void main( String args[] ) {
        TestPSwingTextField frame = new TestPSwingTextField();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( new Dimension( 400, 400 ) );
        frame.setVisible( true );
    }
}
