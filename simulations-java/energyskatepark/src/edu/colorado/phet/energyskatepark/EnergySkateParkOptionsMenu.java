package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.energyskatepark.model.SplineMode;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Feb 5, 2007
 * Time: 3:44:20 PM
 * Copyright (c) Feb 5, 2007 by Sam Reid
 */

public class EnergySkateParkOptionsMenu extends JMenu {

    public EnergySkateParkOptionsMenu() {
        super( "Options" );
        setMnemonic( 'o' );

        final JRadioButtonMenuItem thermal = new JRadioButtonMenuItem( "Thermal Landing", SplineMode.isThermalLanding() );
        thermal.setMnemonic( 't' );
        thermal.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                SplineMode.setThermalLanding( thermal.isSelected() );
            }
        } );
        add( thermal );
    }
}
