/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.Planet;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Jun 8, 2006
 * Time: 10:44:31 AM
 *
 */

public class LocationControlPanel extends VerticalLayoutPanel {
    private JCheckBox showBackgroundCheckbox;
    private PlanetButton[] planetButtons;
    private EnergySkateParkModule module;

    public LocationControlPanel( EnergySkateParkModule module ) {
        this.module = module;
        Planet[] planets = module.getPlanets();
        planetButtons = new PlanetButton[planets.length];
        for( int i = 0; i < planets.length; i++ ) {
            planetButtons[i] = new PlanetButton( module, planets[i], planets[i].isDefault() );
        }
//        PlanetButton space = new PlanetButton( module, new Planet.Space(), false );
//        PlanetButton moon = new PlanetButton( module, new Planet.Moon(), false );
//        PlanetButton earth = new PlanetButton( module, new Planet.Earth(), true );
//        PlanetButton jupiter = new PlanetButton( module, new Planet.Jupiter(), false );
//        planetButtons = new PlanetButton[]{space, moon, earth, jupiter};
//        location.add( space );
//        location.add( moon );
//        location.add( earth );
//        location.add( jupiter );
        VerticalLayoutPanel verticalLayoutPanel = this;
        verticalLayoutPanel.setBorder( BorderFactory.createTitledBorder( EnergySkateParkStrings.getString( "location" ) ) );
        verticalLayoutPanel.setFillHorizontal();
        showBackgroundCheckbox = new JCheckBox( EnergySkateParkStrings.getString( "show.background" ), true );
        verticalLayoutPanel.add( showBackgroundCheckbox );
        for( int i = 0; i < planets.length; i++ ) {
//            Planet planet = planets[i];
            verticalLayoutPanel.add( planetButtons[i] );
        }
//        verticalLayoutPanel.add( space );
//        verticalLayoutPanel.add( moon );
//        verticalLayoutPanel.add( earth );
//        verticalLayoutPanel.add( jupiter );
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
//            planetButtons[i].setSelected( module.getEnergySkateParkModel().getGravity() == planet.getGravity() );
            boolean match = module.getEnergySkateParkModel().getGravity() == planet.getGravity();
            planetButtons[i].setSelected( match );
        }
        if( !matched ) {
            module.getEnergyConservationCanvas().getRootNode().clearBackground();
        }
    }
}
