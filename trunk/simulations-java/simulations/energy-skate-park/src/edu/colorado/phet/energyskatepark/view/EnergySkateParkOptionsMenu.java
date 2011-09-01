// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.colorado.phet.energyskatepark.model.physics.Particle;

/**
 * User: Sam Reid
 * Date: Feb 5, 2007
 * Time: 3:44:20 PM
 */

public class EnergySkateParkOptionsMenu extends JMenu {
    private final AbstractEnergySkateParkModule energySkateParkModule;

    public EnergySkateParkOptionsMenu( final AbstractEnergySkateParkModule energySkateParkModule ) {
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
    }
}
