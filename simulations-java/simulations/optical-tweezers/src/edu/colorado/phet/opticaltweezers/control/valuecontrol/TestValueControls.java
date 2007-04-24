/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control.valuecontrol;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
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
        
        double min = 0;
        double max = 1000000;
        double value = 0;
        String label = "Fluid speed:";
        String valuePattern = "######0";
        String units = "nm/sec";
        final LinearValueControl speedControl = new LinearValueControl( min, max, label, valuePattern, units );
        speedControl.setValue( value );
        speedControl.setDelta( 1 );
        speedControl.setTextFieldEditable( true );
        speedControl.setTickPattern( "0" );
        speedControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                System.out.println( "TestValueControl.speedControl.stateChanged " + speedControl.getValue() );
            }
        } );
        
        min = 1E-6;
        max = 1E-2;
        value = 1E-4;
        label = "Fluid viscosity:";
        valuePattern = "0.0E0";
        units = "Pa*sec";
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
