package edu.colorado.phet.semiconductor.macro.circuit.battery;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
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
    private Battery battery;

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
                public void keyReleased( KeyEvent e ) {
                    String text = ed.getTextField().getText();
                    try {
                        double volts = Double.parseDouble( text );
                        if ( volts >= min && volts <= max ) {
                            setVoltage( volts );
                        }
                        else {
                            JOptionPane.showMessageDialog( getSpinner(), "Please enter a voltage between " + min + " and " + max + " volts." );
                            spinner.setValue( new Double( battery.getVoltage() ) );//doesn't fix the text field
                            if ( spinner.getEditor() instanceof JSpinner.DefaultEditor ) {
                                final JSpinner.DefaultEditor ed = (JSpinner.DefaultEditor) spinner.getEditor();
                                ed.getTextField().setText( battery.getVoltage() + "" );
                            }
                        }
                    }
                    catch( NumberFormatException n ) {
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
        battery.setVoltage( Double.parseDouble( df.format( volts ) ) );
    }

    public JSpinner getSpinner() {
        return spinner;
    }

}
