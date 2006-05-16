/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.controls;

import edu.colorado.phet.ec3.EC3Module;
import edu.colorado.phet.ec3.Planet;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Nov 16, 2005
 * Time: 11:46:24 PM
 * Copyright (c) Nov 16, 2005 by Sam Reid
 */

public class PlanetButton extends JRadioButton {
    private Planet planet;
    private boolean selected;

    public PlanetButton( final EC3Module module, final Planet planet, boolean selected ) {
        super( planet.getName(), selected );
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
