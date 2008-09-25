// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.conductivity.macro.battery;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.conductivity.ConductivityApplication;
import edu.colorado.phet.conductivity.ConductivityResources;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.battery:
//            Battery

public class BatterySpinner {
    private JSpinner spinner;
    private double min;
    private double max;
    private Battery battery;
    private ConductivityApplication conductivityApplication;

    public BatterySpinner( final Battery battery, ConductivityApplication conductivityApplication ) {
        this.battery = battery;
        this.conductivityApplication = conductivityApplication;
        min = 0.0D;
        max = 2D;
        spinner = new JSpinner( new SpinnerNumberModel( battery.getVoltage(), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.10000000000000001D ) );

        if ( spinner.getEditor() instanceof JSpinner.DefaultEditor ) {
            final JSpinner.DefaultEditor ed = (JSpinner.DefaultEditor) spinner.getEditor();
            ed.getTextField().addKeyListener( new KeyAdapter() {
                public void keyReleased( KeyEvent e ) {
                    String text = ed.getTextField().getText();
                    try {
                        double volts = Double.parseDouble( text );
                        if ( volts >= min && volts <= max ) {
                            battery.setVoltage( volts );
                        }
                        else {
                            handleErrorInput();
                        }
                    }
                    catch( NumberFormatException n ) {
                    }
                }
            } );
        }

        TitledBorder titledborder = BorderFactory.createTitledBorder( ConductivityResources.getString( "BatterySpinner.BorderTitle" ) );
        titledborder.setTitleFont( new PhetFont( Font.BOLD, 12 ) );
        spinner.setBorder( titledborder );
        spinner.setPreferredSize( new Dimension( 150, spinner.getPreferredSize().height ) );
        spinner.addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent changeevent ) {
                double d = getSpinnerValue();
                if ( d >= min && d <= max ) {
                    battery.setVoltage( d );
                }
                else {
                    spinner.setValue( new Double( battery.getVoltage() ) );
                }
            }

        } );

    }

    private void handleErrorInput() {
        conductivityApplication.getClock().stop();
        JOptionPane.showMessageDialog( spinner, "Value out of range: Voltage should be betwen 0 and 2 Volts.", "Invalid Voltage", JOptionPane.ERROR_MESSAGE );
        conductivityApplication.getClock().start();
        spinner.setValue( new Double( battery.getVoltage() ) );//doesn't fix the text field
        if ( spinner.getEditor() instanceof JSpinner.DefaultEditor ) {
            final JSpinner.DefaultEditor ed = (JSpinner.DefaultEditor) spinner.getEditor();
            ed.getTextField().setText( battery.getVoltage() + "" );
        }
    }

    private double getSpinnerValue() {
        Object obj = spinner.getValue();
        Double double1 = (Double) obj;
        return double1.doubleValue();
    }

    public JSpinner getSpinner() {
        return spinner;
    }

}
