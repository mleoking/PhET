/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.test;

import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.NumberEditor;


/**
 * SpinnerFocus demonstrates how to select the text in a JSpinner when it gains focus.
 * This functionality DOES NOT WORK due to Sun Bug ID 4699955.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SpinnerFocus extends JFrame {
    
    private static final Dimension SPINNER_SIZE = new Dimension( 65, 25 );
    
    public static void main( String[] args ) {
        SpinnerFocus frame = new SpinnerFocus();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.pack();
        frame.show();
    }
    
    public SpinnerFocus() {
        super();
        
        CommonSpinner spinner1 = new CommonSpinner( 10, 0, 20, 1, "0" );
        CommonSpinner spinner2 = new CommonSpinner( 10, 0, 20, 1, "0" );
        
        JPanel panel = new JPanel();
        panel.add( spinner1 );
        panel.add( spinner2 );
        
        getContentPane().add( panel );
    }
    
    private static class SpinnerFocusHandler extends FocusAdapter {
        public void focusGained( FocusEvent event ) {
            System.out.println( "focusGained " + event.getSource().getClass().getName() );//XXX
            if ( event.getSource() instanceof JFormattedTextField ) {
                JFormattedTextField textField = (JFormattedTextField) event.getSource();
                textField.selectAll();
            }
        }
    }

    private static class CommonSpinner extends JSpinner implements FocusListener {
        
        private JFormattedTextField _textField;
        
        public CommonSpinner( double value, double min, double max, double step, String format ) {
            super( );
            // model
            SpinnerNumberModel model = new SpinnerNumberModel( value, min, max, step );
            setModel( model );
            // editor
            NumberEditor numberEditor = new NumberEditor( this, format );
            setEditor( numberEditor );
            _textField = numberEditor.getTextField();
            _textField.addFocusListener( this );
            // size
            setPreferredSize( SPINNER_SIZE );
            setMinimumSize( SPINNER_SIZE );
        }

        /*
         * When the spinner gains focus, select its contents.
         * NOTE: This currently does not work; see Bug ID 4699955 at bugs.sun.com
         */
        public void focusGained( FocusEvent e ) {
            _textField.selectAll();
        }

        public void focusLost( FocusEvent e ) {}
    }
}
