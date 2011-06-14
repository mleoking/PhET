// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.test.debug_piccolo2d_1_3;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.LineBorder;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * Demonstrates a problem with PSwing in Piccolo2D 1.3 rc1.
 * The first time a PSwing is added to the scenegraph, its Component works fine.
 * If it's removed and added again, the Component is not fully functional.
 * <p/>
 * In this example, a JTextField is added, and it receives mouse and key events.
 * If it's removed and added again, the JTextField does not receive key events.
 * <p/>
 * See also PhET Unfuddle #2137, Piccolo Issue 158.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DebugAddPSwing extends JFrame {

    public DebugAddPSwing() {
        setResizable( false );
        setSize( new Dimension( 400, 400 ) );

        // canvas
        final PCanvas canvas = new PSwingCanvas();
        canvas.setBorder( new LineBorder( Color.BLACK ) );
        canvas.removeInputEventListener( canvas.getZoomEventHandler() );
        canvas.removeInputEventListener( canvas.getPanEventHandler() );

        // JTextField
        JTextField textField = new JTextField( "edit me" );
        textField.addMouseListener( new MouseAdapter() {
            @Override
            public void mousePressed( MouseEvent e ) {
                System.out.println( "mousePressed in JTextField" );
            }
        } );
        textField.addKeyListener( new KeyAdapter() {
            @Override
            public void keyPressed( KeyEvent e ) {
                System.out.println( "keyPressed in JTextField" );
            }
        } );

        // PSwing wrapper for the JTextField
        final PSwing textFieldNode = new PSwing( textField );
        textFieldNode.addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mousePressed( final PInputEvent event ) {
                System.out.println( "mousePressed in PSwing" );
            }

            @Override
            public void keyPressed( final PInputEvent event ) {
                System.out.println( "keyPressed in PSwing" );
            }
        } );
        textFieldNode.setOffset( 100, 200 );

        // JCheckBox for setting whether text field is in the scenegraph.
        final JCheckBox checkBox = new JCheckBox( "add JTextField to scenegraph" );
        checkBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( checkBox.isSelected() ) {
                    canvas.getLayer().addChild( textFieldNode );
                }
                else {
                    canvas.getLayer().removeChild( textFieldNode );
                }
            }
        } );

        // PSwing wrapper for the JCheckBox
        PSwing checkBoxNode = new PSwing( checkBox );
        checkBoxNode.setOffset( 100, 100 );
        canvas.getLayer().addChild( checkBoxNode );

        setContentPane( canvas );
    }

    public static void main( String[] args ) {
        JFrame frame = new DebugAddPSwing();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        // center on the screen
        Toolkit tk = Toolkit.getDefaultToolkit();
        int x = (int) ( tk.getScreenSize().getWidth() / 2 - frame.getWidth() / 2 );
        int y = (int) ( tk.getScreenSize().getHeight() / 2 - frame.getHeight() / 2 );
        frame.setLocation( x, y );
        frame.setVisible( true );
    }
}
