/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.view.controls.valuecontrol;

import java.awt.Dimension;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Test harness for the AbstractValueControl hierarchy.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestValueControls extends JFrame {

    private static final Dimension FRAME_SIZE = new Dimension( 500, 500 );
    
    public TestValueControls() {
        
        // Linear control, center aligned
        double min = 0;
        double max = 1000;
        double value = 0;
        String label = "Linear control:";
        String valuePattern = "######0";
        String units = "meters";
        final LinearValueControl speedControl = new LinearValueControl( min, max, label, valuePattern, units );
        speedControl.setHorizontalAlignment( SwingConstants.CENTER );
        speedControl.setValue( value );
        speedControl.setTickSpacing( 250 );
        speedControl.setMinorTickLabelsVisible( true );
        speedControl.setUpDownArrowDelta( 1 );
        speedControl.setTextFieldEditable( true );
        speedControl.setTickPattern( "0" );
        speedControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                System.out.println( "TestValueControl.speedControl.stateChanged " + speedControl.getValue() );
            }
        } );
        
        // Logarithmic control, default alignment
        min = 1E-6;
        max = 1E-2;
        value = 1E-4;
        label = "Logarithmic control:";
        valuePattern = "0.0E0";
        units = "seconds";
        final LogarithmicValueControl viscosityControl = new LogarithmicValueControl( min, max, label, valuePattern, units );
        viscosityControl.setValue( value );
        viscosityControl.setTextFieldEditable( true );
        viscosityControl.setTickPattern( "0E0" );
        viscosityControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                System.out.println( "TestValueControl.viscosityControl.stateChanged " + viscosityControl.getValue() );
            }
        } );
        
        JPanel panel = new JPanel();
        BoxLayout layout = new BoxLayout( panel, BoxLayout.Y_AXIS );
        panel.setLayout( layout );
        panel.add( speedControl );
        panel.add( viscosityControl );
        
        setContentPane( panel );
        setSize( FRAME_SIZE );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
    
    public static void main( String[] args ) {
        TestValueControls test = new TestValueControls();
        test.show();
    }
}
