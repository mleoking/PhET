/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.ec3.controls.GravitySlider;
import edu.colorado.phet.ec3.controls.PlanetButton;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Jun 8, 2006
 * Time: 10:44:31 AM
 * Copyright (c) Jun 8, 2006 by Sam Reid
 */

public class LocationControlPanel extends VerticalLayoutPanel {
    private JCheckBox showBackgroundCheckbox;
    private PlanetButton[] planetButtons;
    private EnergySkateParkModule module;

    public LocationControlPanel( EnergySkateParkModule module ) {
        this.module = module;

        PlanetButton space = new PlanetButton( module, new Planet.Space(), false );
        PlanetButton moon = new PlanetButton( module, new Planet.Moon(), false );
        PlanetButton earth = new PlanetButton( module, new Planet.Earth(), true );
        PlanetButton jupiter = new PlanetButton( module, new Planet.Jupiter(), false );
        planetButtons = new PlanetButton[]{space, moon, earth, jupiter};
//        location.add( space );
//        location.add( moon );
//        location.add( earth );
//        location.add( jupiter );
        VerticalLayoutPanel verticalLayoutPanel = this;
        verticalLayoutPanel.setBorder( BorderFactory.createTitledBorder( EnergySkateParkStrings.getString( "location" ) ) );
        verticalLayoutPanel.setFillHorizontal();
        showBackgroundCheckbox = new JCheckBox( EnergySkateParkStrings.getString( "show.background" ), true );
        verticalLayoutPanel.add( showBackgroundCheckbox );
        verticalLayoutPanel.add( space );
        verticalLayoutPanel.add( moon );
        verticalLayoutPanel.add( earth );
        verticalLayoutPanel.add( jupiter );
        final JComponent gravitySlider = new GravitySlider( module );
        verticalLayoutPanel.addFullWidth( gravitySlider );

        module.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                synchronizePlanet();
            }

        } );
        synchronizePlanet();
        new Planet.Earth().apply( module );
    }

    private void synchronizePlanet() {
        module.getEnergyConservationCanvas().getRootNode().getBackground().setVisible( showBackgroundCheckbox.isSelected() );
        boolean matched = false;
        for( int i = 0; i < planetButtons.length; i++ ) {
            Planet planet = planetButtons[i].getPlanet();
            if( module.getEnergySkateParkModel().getGravity() == planet.getGravity() ) {
                planet.apply( module );
                matched = true;
            }
//            planetButtons[i].setSelected( module.getEnergyConservationModel().getGravity() == planet.getGravity() );
            boolean match = module.getEnergySkateParkModel().getGravity() == planet.getGravity();
            planetButtons[i].setSelected( match );
        }
        if( !matched ) {
            module.getEnergyConservationCanvas().getRootNode().clearBackground();
        }
    }
}
