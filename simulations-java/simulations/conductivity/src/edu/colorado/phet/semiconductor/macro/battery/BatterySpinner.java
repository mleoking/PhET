// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor.macro.battery;

import edu.colorado.phet.common.conductivity.view.util.SimStrings;
import edu.colorado.phet.semiconductor.macro.ConductivityApplication;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.battery:
//            Battery

public class BatterySpinner {
    private double lastValue;
    JSpinner spinner;
    double min;
    double max;
    private Battery battery;
    private ConductivityApplication conductivityApplication;

    public BatterySpinner( final Battery battery, ConductivityApplication conductivityApplication ) {
        this.battery = battery;
        this.conductivityApplication = conductivityApplication;
        min = 0.0D;
        max = 2D;
        spinner = new JSpinner( new SpinnerNumberModel( battery.getVoltage(), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.10000000000000001D ) );

        final JTextField tf = ( (JSpinner.DefaultEditor)spinner.getEditor() ).getTextField();
        tf.addKeyListener( new KeyAdapter() {
            public void keyPressed( KeyEvent ke ) {
                if( ke.getKeyCode() == KeyEvent.VK_ENTER ) {
                    try {
                        double value = Double.parseDouble( tf.getText() );
                        if( value < min || value > max ) {
                            showErrorMessage();
                        }
                    }
                    catch( RuntimeException e ) {
                        e.printStackTrace();
                        showErrorMessage();
                    }
                }
            }
        } );

        TitledBorder titledborder = BorderFactory.createTitledBorder( SimStrings.get( "BatterySpinner.BorderTitle" ) );
        titledborder.setTitleFont( new Font( "Lucida Sans", Font.BOLD, 12 ) );
        spinner.setBorder( titledborder );
        spinner.setPreferredSize( new Dimension( 150, spinner.getPreferredSize().height ) );
        spinner.addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent changeevent ) {
                double d = getSpinnerValue();
                if( d >= min && d <= max ) {
                    battery.setVoltage( d );
                    lastValue = d;
                }
                else {
                    spinner.setValue( new Double( lastValue ) );
                }
            }

        } );

    }

    private void showErrorMessage() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                conductivityApplication.getClock().stop();
                JOptionPane.showMessageDialog( spinner, "Value out of range: Voltage should be betwen 0 and 2 Volts.", "Invalid Voltage", JOptionPane.ERROR_MESSAGE );
                conductivityApplication.getClock().start();
            }
        } );

    }

    private double getSpinnerValue() {
        Object obj = spinner.getValue();
        Double double1 = (Double)obj;
        return double1.doubleValue();
    }

    public JSpinner getSpinner() {
        return spinner;
    }

}
