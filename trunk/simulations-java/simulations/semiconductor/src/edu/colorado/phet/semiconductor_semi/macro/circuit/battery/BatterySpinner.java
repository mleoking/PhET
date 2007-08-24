package edu.colorado.phet.semiconductor_semi.macro.circuit.battery;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;

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
 */
public class BatterySpinner {
    JSpinner spinner;
    public static final double min = -10;
    public static final double max = 10;
    DecimalFormat df = new DecimalFormat( "##.00#" );

    public BatterySpinner( final Battery battery ) {
        spinner = new MyJSpinner( new SpinnerNumberModel( battery.getVoltage(), min, max, .1 ) );
//        SpinnerUI su=new MySpinnerUI3();
//        spinner.setUI(su);
//        spinner.revalidate();

        TitledBorder border = BorderFactory.createTitledBorder( SimStrings.get( "BatterySpinner.BorderTitle" ) );
        border.setTitleFont( new Font( PhetDefaultFont.LUCIDA_SANS, 0, 18 ) );

        spinner.setBorder( border );
        spinner.setPreferredSize( new Dimension( 125, 100 ) );
        spinner.getEditor().setFont( new Font( PhetDefaultFont.LUCIDA_SANS, Font.BOLD, 20 ) );
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

}
