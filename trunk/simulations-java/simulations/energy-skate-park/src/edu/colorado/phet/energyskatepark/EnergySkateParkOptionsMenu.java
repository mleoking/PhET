package edu.colorado.phet.energyskatepark;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * User: Sam Reid
 * Date: Feb 5, 2007
 * Time: 3:44:20 PM
 *
 */

public class EnergySkateParkOptionsMenu extends JMenu {
    private EnergySkateParkModule energySkateParkModule;

    public EnergySkateParkOptionsMenu( final EnergySkateParkModule energySkateParkModule) {
        super( "Options" );
        this.energySkateParkModule = energySkateParkModule;
        setMnemonic( 'o' );
        final JRadioButtonMenuItem showEnergyError=new JRadioButtonMenuItem( "Show Energy Error",energySkateParkModule.isEnergyErrorVisible());
        showEnergyError.setMnemonic( 's');
        showEnergyError.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                energySkateParkModule.setEnergyErrorVisible(showEnergyError.isSelected());
            }
        } );
        add(showEnergyError);
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
