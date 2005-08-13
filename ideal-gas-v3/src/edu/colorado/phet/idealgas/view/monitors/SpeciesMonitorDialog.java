/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.view.monitors;

import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.LightSpecies;

import javax.swing.*;
import java.awt.*;

/**
 * A dialog that shows monitor panels for each of the species in the simulation
 */
public class SpeciesMonitorDialog extends JDialog {
    private String[] speciesPanelTitles = new String[]{
        SimStrings.get( "IdealGasMonitorPanel.Heavy_species" ),
        SimStrings.get( "IdealGasMonitorPanel.Light_species" )
    };
    private GasSpeciesMonitorPanel heavySpeciesPanel;
    private GasSpeciesMonitorPanel lightSpeciesPanel;

    public SpeciesMonitorDialog( PhetFrame phetFrame, IdealGasModel idealGasModel ) {
        super( phetFrame, SimStrings.get( "GasSpeciesMonitorPanel.Title" ), false );
        this.setResizable( false );

        JPanel speciesPanel = new JPanel( new GridLayout( 2, 1 ) );
        heavySpeciesPanel = new GasSpeciesMonitorPanel( HeavySpecies.class,
                                                        speciesPanelTitles[0],
                                                        idealGasModel );
        speciesPanel.add( heavySpeciesPanel );
        lightSpeciesPanel = new GasSpeciesMonitorPanel( LightSpecies.class,
                                                        speciesPanelTitles[1],
                                                        idealGasModel );
        speciesPanel.add( lightSpeciesPanel );

        this.getContentPane().add( speciesPanel );
        pack();
    }

    public void setSpeciesPanelTitles( String[] speciesPanelTitles ) {
        this.speciesPanelTitles = speciesPanelTitles;
        heavySpeciesPanel.setTitle( speciesPanelTitles[0] );
        lightSpeciesPanel.setTitle( speciesPanelTitles[1] );
    }


}
