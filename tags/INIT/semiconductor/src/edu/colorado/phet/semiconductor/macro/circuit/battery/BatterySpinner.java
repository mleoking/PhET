package edu.colorado.phet.semiconductor.macro.circuit.battery;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jan 17, 2004
 * Time: 3:22:17 PM
 * Copyright (c) Jan 17, 2004 by Sam Reid
 */
public class BatterySpinner {
    JSpinner spinner;
    private Battery battery;
    public static final double min = -10;
    public static final double max = 10;
    DecimalFormat df = new DecimalFormat( "##.00#" );

    public BatterySpinner( final Battery battery ) {
        this.battery = battery;
        spinner = new MyJSpinner( new SpinnerNumberModel( battery.getVoltage(), min, max, .1 ) );
//        SpinnerUI su=new MySpinnerUI3();
//        spinner.setUI(su);
//        spinner.revalidate();

        TitledBorder border = BorderFactory.createTitledBorder( "Voltage (V)" );
        border.setTitleFont( new Font( "Lucida Sans", 0, 18 ) );

        spinner.setBorder( border );
        spinner.setPreferredSize( new Dimension( 125, 100 ) );
        spinner.getEditor().setFont( new Font( "Lucida Sans", Font.BOLD, 20 ) );
        spinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Object o = spinner.getValue();
                Double d = (Double)o;
                double volts = d.doubleValue();

                String str = df.format( volts );
                volts = Double.parseDouble( str );
//                System.out.println("volts = " + volts);
                battery.setVoltage( volts );
            }
        } );
    }

    public JSpinner getSpinner() {
        return spinner;
    }

    public Battery getBattery() {
        return battery;
    }

    public double getMinimumVoltage() {
        return min;
    }

    public double getMaximumVoltage() {
        return max;
    }

    public void setValue( double value ) {
        spinner.setValue( new Double( value ) );
    }
}
