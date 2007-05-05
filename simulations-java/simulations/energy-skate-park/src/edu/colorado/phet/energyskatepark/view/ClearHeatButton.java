/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.Body;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: May 26, 2006
 * Time: 7:57:04 AM
 */

public class ClearHeatButton extends JButton {
    public ClearHeatButton( final EnergySkateParkModule module ) {
        setText( EnergySkateParkStrings.getString( "clear.heat" ) );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.clearHeat();
            }
        } );
        module.getEnergySkateParkModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void stepFinished() {
                if( module.getEnergySkateParkModel().getNumBodies() > 0 ) {
                    Body body = module.getEnergySkateParkModel().getBody( 0 );
                    setEnabled( body.getThermalEnergy() > 0 );
                }
            }
        } );
    }
}
