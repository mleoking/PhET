// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.energyskatepark.model.physics.Particle;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Feb 5, 2007
 * Time: 3:44:20 PM
 */

public class EnergySkateParkOptionsMenu extends JMenu {
    private EnergySkateParkModule energySkateParkModule;

    public EnergySkateParkOptionsMenu( final EnergySkateParkModule energySkateParkModule ) {
        super( "Options" );
        this.energySkateParkModule = energySkateParkModule;
        setMnemonic( 'o' );
        final JRadioButtonMenuItem showEnergyError = new JRadioButtonMenuItem( "Show Energy Error", energySkateParkModule.isEnergyErrorVisible() );
        showEnergyError.setMnemonic( 's' );
        showEnergyError.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                energySkateParkModule.setEnergyErrorVisible( showEnergyError.isSelected() );
            }
        } );
        add( showEnergyError );

        final JCheckBoxMenuItem reorientOnBounce = new JCheckBoxMenuItem( "Reorient on Bounce", Particle.reorientOnBounce );
        reorientOnBounce.setMnemonic( 'r' );
        reorientOnBounce.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Particle.reorientOnBounce = reorientOnBounce.isSelected();
            }
        } );
        add( reorientOnBounce );
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
