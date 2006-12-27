// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor.macro.battery;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.battery:
//            Battery

public class BatterySpinner {

    public BatterySpinner( final Battery battery ) {
        min = 0.0D;
        max = 2D;
        spinner = new JSpinner( new SpinnerNumberModel( battery.getVoltage(), min, max, 0.10000000000000001D ) );
        TitledBorder titledborder = BorderFactory.createTitledBorder( "Voltage (V)" );
        titledborder.setTitleFont( new Font( "Lucida Sans", 0, 18 ) );
        spinner.setBorder( titledborder );
        spinner.setPreferredSize( new Dimension( 120, 50 ) );
        spinner.addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent changeevent ) {
                Object obj = spinner.getValue();
                Double double1 = (Double)obj;
                double d = double1.doubleValue();
                battery.setVoltage( d );
            }

        } );
    }

    public JSpinner getSpinner() {
        return spinner;
    }

    JSpinner spinner;
    double min;
    double max;
}
