package edu.colorado.phet.batteryvoltage;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ShowBattery implements ItemListener {
    JCheckBox jcb;
    BatteryImagePainter bip;

    public ShowBattery( JCheckBox jcb, BatteryImagePainter bip ) {
        this.jcb = jcb;
        this.bip = bip;
    }

    public void itemStateChanged( ItemEvent e ) {
        if( jcb.isSelected() ) {
            bip.setPaintImage( true );
        }
        else {
            bip.setPaintImage( false );
        }
    }
}
