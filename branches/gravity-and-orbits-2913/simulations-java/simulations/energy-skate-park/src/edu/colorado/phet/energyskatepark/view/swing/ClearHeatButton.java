// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.swing;

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
    private EnergySkateParkModel model;

    public ClearHeatButton( final EnergySkateParkModel model ) {
        this.model = model;
        setText( EnergySkateParkStrings.getString( "controls.clear-heat" ) );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.clearHeat();
            }
        } );
        model.addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void primaryBodyChanged() {
                update();
            }
        } );
    }

    private void update() {
        if( model.getNumBodies() > 0 ) {
            Body body = model.getBody( 0 );
            setEnabled( body.getThermalEnergy() > 0 );
        }
    }
}
