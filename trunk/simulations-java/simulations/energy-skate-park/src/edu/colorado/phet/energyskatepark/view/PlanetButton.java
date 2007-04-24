/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.model.Planet;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Nov 16, 2005
 * Time: 11:46:24 PM
 *
 */

public class PlanetButton extends JRadioButton {
    private Planet planet;
    private boolean selected;

    public PlanetButton( final EnergySkateParkModule module, final Planet planet, boolean selected ) {
//        super( planet.getName() + " (" + ( Math.abs( planet.getGravity() ) ) + " N/kg)", selected );
        super( planet.getName() );//+ " (" + ( Math.abs( planet.getGravity() ) ) + " N/kg)", selected );
        this.planet = planet;
        this.selected = selected;
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                planet.apply( module );
            }
        } );
    }

    public Planet getPlanet() {
        return planet;
    }
}
