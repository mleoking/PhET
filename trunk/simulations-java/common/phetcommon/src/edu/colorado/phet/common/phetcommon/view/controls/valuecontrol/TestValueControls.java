/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.view.controls.valuecontrol;

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

    public TestValueControls() {
        
        final LinearValueControl protonsControl;
        {
            int value = 79;
            int min = 20;
            int max = 100;
            String valuePattern = "0";
            String label = "Number of protons:";
            String units = "";
            int columns = 3;
            protonsControl = new LinearValueControl( min, max, label, valuePattern, units );
            protonsControl.setValue( value );
            protonsControl.setUpDownArrowDelta( 1 );
            protonsControl.setTextFieldEditable( true );
            protonsControl.setTextFieldColumns( columns );
            protonsControl.setMinorTickSpacing( 10 );
            protonsControl.setMinorTickLabelsVisible( false );
            protonsControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    System.out.println( "protonsControl.stateChanged " + protonsControl.getValue() );
                }
            } );
        }
        
        // Linear control
        final LinearValueControl speedControl;
        {
            double min = 0;
            double max = 1000;
            double value = 0;
            String label = "Speed:";
            String valuePattern = "######0";
            String units = "mph";
            speedControl = new LinearValueControl( min, max, label, valuePattern, units );
            speedControl.setValue( value );
            speedControl.setMajorTickSpacing( 500 );
            speedControl.setMinorTickSpacing( 100 );
            speedControl.addTickLabel( min, "min" );
            speedControl.addTickLabel( 500, "half" );
            speedControl.addTickLabel( max, "max" );
            speedControl.setMinorTickLabelsVisible( false );
            speedControl.setUpDownArrowDelta( 1 );
            speedControl.setTextFieldEditable( true );
            speedControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    System.out.println( "speedControl.stateChanged " + speedControl.getValue() );
                }
            } );
        }
        
        // Logarithmic control, default alignment
        final LogarithmicValueControl viscosityControl;
        {
            double min = 1E-6;
            double max = 1E-2;
            double value = 1E-4;
            String label = "Viscosity:";
            String valuePattern = "0.0E0";
            String units = "Pa*sec";
            viscosityControl = new LogarithmicValueControl( min, max, label, valuePattern, units );
            viscosityControl.setValue( value );
            viscosityControl.setTextFieldEditable( true );
            viscosityControl.setTickPattern( "0E0" );
            viscosityControl.addChangeListener( new ChangeListener() {

                public void stateChanged( ChangeEvent event ) {
                    System.out.println( "viscosityControl.stateChanged " + viscosityControl.getValue() );
                }
            } );
        }
        
        // Linear control, snaps to ticks
        final LinearValueControl potatoControl;
        {
            int min = 1;
            int max = 4;
            int value = 2;
            String label = "Potatoes:";
            String valuePattern = "#0.0";
            String units = "";
            potatoControl = new LinearValueControl( min, max, label, valuePattern, units );
            potatoControl.setValue( value );
            potatoControl.setMajorTickSpacing( 1 );
            potatoControl.setMinorTickSpacing( 0.5 );
            potatoControl.setTickPattern( "0" );
            potatoControl.setUpDownArrowDelta( 1 );
            potatoControl.setTextFieldEditable( true );
            potatoControl.setSnapToTicks( true );
            potatoControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    System.out.println( "potatoControl.stateChanged " + potatoControl.getValue() );
                }
            } );
        }
        
        // One control that changes the range of another control
        final LinearValueControl rangeControl;
        {
            int min = 2;
            int max = 10;
            int value = (int) potatoControl.getMaximum();
            String label = "Potatoes range:";
            String valuePattern = "#0";
            String units = "";
            rangeControl = new LinearValueControl( min, max, label, valuePattern, units );
            rangeControl.setValue( value );
            rangeControl.setMajorTickSpacing( 1 );
            rangeControl.setUpDownArrowDelta( 1 );
            rangeControl.setSnapToTicks( true );
            rangeControl.setTextFieldEditable( true );
            rangeControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    System.out.println( "rangeControl.stateChanged " + rangeControl.getValue() );
                    potatoControl.setRange( potatoControl.getMinimum(), rangeControl.getValue() );
                }
            } );
        }
        
        // Linear control with custom label table
        final LinearValueControl customLabelsControl;
        {
            int min = 0;
            int max = 10;
            int value = 5;
            String label = "Custom Tick Labels:";
            String valuePattern = "###0.0";
            String units = "";
            customLabelsControl = new LinearValueControl( min, max, label, valuePattern, units );
            customLabelsControl.setValue( value );
            customLabelsControl.setMajorTickSpacing( 5 );
            customLabelsControl.setUpDownArrowDelta( 1 );
            customLabelsControl.setTextFieldEditable( true );
            customLabelsControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    System.out.println( "customLabelsControl.stateChanged " + customLabelsControl.getValue() );
                }
            } );
            
            Hashtable labelTable = new Hashtable();
            labelTable.put( new Double( 0 ),  new JLabel( "zero" ) );
            labelTable.put( new Double( 5 ),  new JLabel( "five" ) );
            labelTable.put( new Double( 10 ),  new JLabel( "ten" ) );
            customLabelsControl.setTickLabels( labelTable );
        }
        
        JPanel panel = new JPanel();
        panel.setBorder( BorderFactory.createEmptyBorder( 20, 20, 20, 20 ) );
        BoxLayout layout = new BoxLayout( panel, BoxLayout.Y_AXIS );
        panel.setLayout( layout );
        panel.add( protonsControl );
        panel.add( new JSeparator() );
        panel.add( speedControl );
        panel.add( new JSeparator() );
        panel.add( viscosityControl );
        panel.add( new JSeparator() );
        panel.add( potatoControl );
        panel.add( rangeControl );
        panel.add( new JSeparator() );
        panel.add( customLabelsControl );
        
        setContentPane( panel );
        pack();
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
    
    public static void main( String[] args ) {
        TestValueControls test = new TestValueControls();
        test.show();
    }
}
