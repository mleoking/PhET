// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.model.Planet;

/**
 * User: Sam Reid
 * Date: Jun 8, 2006
 * Time: 10:44:31 AM
 */

public class LocationControlPanel extends VerticalLayoutPanel {
    private final JCheckBox showBackgroundCheckbox;
    private final PlanetButton[] planetButtons;
    private final AbstractEnergySkateParkModule module;
    private final PlanetButtonLayout layout;

    public LocationControlPanel( AbstractEnergySkateParkModule module ) {
        this( module, new VerticalPlanetButtonLayout() );
    }

    public LocationControlPanel( AbstractEnergySkateParkModule module, PlanetButtonLayout layout ) {
        this( module, layout, false );
    }

    public LocationControlPanel( AbstractEnergySkateParkModule module, PlanetButtonLayout layout, boolean centered ) {
        this.layout = layout;
        this.module = module;
        Planet[] planets = module.getPlanets();
        planetButtons = new PlanetButton[planets.length];
        for ( int i = 0; i < planets.length; i++ ) {
            planetButtons[i] = new PlanetButton( module, planets[i], planets[i].isDefault() );
        }
        setBorder( BorderFactory.createTitledBorder( EnergySkateParkResources.getString( "location" ) ) );
        setFillHorizontal();
        showBackgroundCheckbox = new JCheckBox( EnergySkateParkResources.getString( "controls.show-background" ), true );

        JPanel planetPanel = layout.getPlanetPanel( planetButtons );
        setAnchor( GridBagConstraints.WEST );
        if ( !centered ) {
            setFillNone();
        }
        add( planetPanel );
        final JComponent gravitySlider = new GravitySlider( module );
        addFullWidth( gravitySlider );

        module.getEnergySkateParkModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {

            public void gravityChanged() {
                synchronizePlanet();
            }

            public void stateSet() {
                synchronizePlanet();
            }
        } );

        showBackgroundCheckbox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                synchronizePlanet();
            }
        } );

        synchronizePlanet();
        new Planet.Earth().apply( module );
    }

    public void reset() {
        showBackgroundCheckbox.setSelected( true );//todo: convert to MVC pattern
        synchronizePlanet();
    }

    public static interface PlanetButtonLayout {
        JPanel getPlanetPanel( PlanetButton[] planets );
    }

    public static class VerticalPlanetButtonLayout implements PlanetButtonLayout {
        public JPanel getPlanetPanel( PlanetButton[] planetButtons ) {
            VerticalLayoutPanel verticalLayoutPanel = new VerticalLayoutPanel();
            for ( int i = 0; i < planetButtons.length; i++ ) {
                verticalLayoutPanel.add( planetButtons[i] );
            }
            return verticalLayoutPanel;
        }
    }

    public static class TwoColumnLayout implements PlanetButtonLayout {

        public JPanel getPlanetPanel( PlanetButton[] planets ) {
            JPanel jp = new JPanel( new GridBagLayout() );
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            //todo: number of planets is hard-coded here, will fail if planets are added or removed
            add( jp, planets[0], gridBagConstraints, 0, 0 );
            add( jp, planets[1], gridBagConstraints, 1, 0 );
            add( jp, planets[2], gridBagConstraints, 0, 1 );
            add( jp, planets[3], gridBagConstraints, 1, 1 );
            return jp;
        }

        private void add( JPanel jp, PlanetButton planet, GridBagConstraints gridBagConstraints, int x, int y ) {
            gridBagConstraints.gridx = x;
            gridBagConstraints.gridy = y;
            jp.add( planet, gridBagConstraints );
        }
    }


    private void synchronizePlanet() {
        module.getEnergySkateParkSimulationPanel().getRootNode().setBackgroundVisible( showBackgroundCheckbox.isSelected() );
        boolean matched = false;
        for ( int i = 0; i < planetButtons.length; i++ ) {
            Planet planet = planetButtons[i].getPlanet();
            if ( module.getEnergySkateParkModel().getGravity() == planet.getGravity() ) {
                planet.apply( module );
                matched = true;
            }
            boolean match = module.getEnergySkateParkModel().getGravity() == planet.getGravity();
            planetButtons[i].setSelected( match );
        }
        if ( !matched ) {
            module.getEnergySkateParkSimulationPanel().getRootNode().clearBackground();
        }
    }
}
