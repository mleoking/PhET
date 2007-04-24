package edu.colorado.phet.energyskatepark;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Feb 5, 2007
 * Time: 3:44:20 PM
 *
 */

public class EnergySkateParkOptionsMenu extends JMenu {

    public EnergySkateParkOptionsMenu() {
        super( "Options" );
        setMnemonic( 'o' );

//        final JRadioButtonMenuItem thermal = new JRadioButtonMenuItem( "Thermal Landing", EnergySkateParkModel.isThermalLanding() );
//        thermal.setMnemonic( 't' );
//        thermal.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                EnergySkateParkModel.setThermalLanding( thermal.isSelected() );
//            }
//        } );
//        add( thermal );
    }
}
