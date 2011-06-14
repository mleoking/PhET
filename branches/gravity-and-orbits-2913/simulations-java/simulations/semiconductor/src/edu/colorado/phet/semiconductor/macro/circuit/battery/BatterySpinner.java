// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.semiconductor.macro.circuit.battery;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.semiconductor.SemiconductorResources;

/**
 * User: Sam Reid
 * Date: Jan 17, 2004
 * Time: 3:22:17 PM
 */
public class BatterySpinner {
    JSpinner spinner;
    public static final double min = -4;
    public static final double max = 4;
    DecimalFormat df = new DecimalFormat( "##.00#" );
    private final Battery battery;

    public BatterySpinner( final Battery battery ) {
        this.battery = battery;
        spinner = new MyJSpinner( new SpinnerNumberModel( battery.getVoltage(), min, max, .1 ) );

        TitledBorder border = BorderFactory.createTitledBorder( SemiconductorResources.getString( "BatterySpinner.BorderTitle" ) );
        border.setTitleFont( new PhetFont( Font.PLAIN, 18 ) );

        spinner.setBorder( border );
        spinner.setPreferredSize( new Dimension( 125, 100 ) );
        spinner.getEditor().setFont( new PhetFont( Font.BOLD, 20 ) );
        if ( spinner.getEditor() instanceof JSpinner.DefaultEditor ) {
            final JSpinner.DefaultEditor ed = (JSpinner.DefaultEditor) spinner.getEditor();
            ed.getTextField().addKeyListener( new KeyAdapter() {
                @Override
                public void keyReleased( KeyEvent e ) {
                    String text = ed.getTextField().getText();
                    try {
//                        double volts = Double.parseDouble( text );
                        double volts = parse( text );
                        if ( volts >= min && volts <= max ) {
                            setVoltage( volts );
                        }
                        else {
                            PhetOptionPane.showMessageDialog( getSpinner(), "Please enter a voltage between " + min + " and " + max + " volts." );
                            spinner.setValue( new Double( battery.getVoltage() ) );//doesn't fix the text field
                            if ( spinner.getEditor() instanceof JSpinner.DefaultEditor ) {
                                final JSpinner.DefaultEditor ed = (JSpinner.DefaultEditor) spinner.getEditor();
                                ed.getTextField().setText( battery.getVoltage() + "" );
                            }
                        }
                    }
                    catch( NumberFormatException n ) {
                        System.err.println( getClass().getName() + " - Error: Number format exception caught." );
                        System.err.println( " Attempted to convert: " + text );
                        n.printStackTrace();
                    }
                }
            } );
        }
        spinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateValue();
            }
        } );
    }

    private void updateValue() {
        setVoltage( ( (Double) spinner.getValue() ).doubleValue() );
    }

    private void setVoltage( double volts ) {
        battery.setVoltage( parse( df.format( volts )) );
    }

    /**
     * This method is a locale-tolerant means for interpreting a string that
     * describes a double.  This was needed to solve some issues with the use
     * of this sim on machines where the number formatting was set up to use
     * commas instead of decimal points.  See #2462.
     *
     * @param valueText
     * @return
     */
    private double parse( String valueText ){
        double value = 0;
        try {
            value = df.parse( valueText ).doubleValue();
        }
        catch ( ParseException e1 ) {
            System.err.println( getClass().getName() + " - Error: caught parse exception." );
            e1.printStackTrace();
        }
        return value;
    }

    public JSpinner getSpinner() {
        return spinner;
    }

}
