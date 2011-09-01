// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JRadioButton;

import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.colorado.phet.energyskatepark.model.Planet;

/**
 * User: Sam Reid
 * Date: Nov 16, 2005
 * Time: 11:46:24 PM
 */

public class PlanetButton extends JRadioButton {
    private final Planet planet;
    private final boolean selected;

    public PlanetButton( final AbstractEnergySkateParkModule module, final Planet planet, boolean selected ) {
        super( planet.getName() );//+ " (" + ( Math.abs( planet.getGravity() ) ) + " N/kg)", selected );
        this.planet = planet;
        this.selected = selected;
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setSelected( true );//planet radio buttons don't deselect when pressed, and there is no ButtonGroup to manage their state (since sometimes none is selected)
                planet.apply( module );
            }
        } );
    }

    public Planet getPlanet() {
        return planet;
    }
}
