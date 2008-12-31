/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.nuclearreactor;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;

/**
 * This class represents the control panel that presents the legend and allows
 * the user to control some aspects of the Nuclear Reactor tab of this sim.
 *
 * @author John Blanco
 */
public class NuclearReactorControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private NuclearReactorLegendPanel      _legendPanel;
    private NuclearReactorEnergyGraphPanel _energyGraphPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param nuclearReactorModule
     * @param parentFrame parent frame, for creating dialogs
     */
    public NuclearReactorControlPanel( NuclearReactorModule nuclearReactorModule, Frame parentFrame ) {
        super();
        
        // Set the control panel's minimum width.
        int minimumWidth = NuclearPhysicsResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Create sub-panels
        _legendPanel = new NuclearReactorLegendPanel();
        
        // Add the legend panel.
        addControlFullWidth( _legendPanel );

        // Add the energy graph.
        _energyGraphPanel = new NuclearReactorEnergyGraphPanel(nuclearReactorModule.getNuclearReactorModel());
        addControlFullWidth( _energyGraphPanel );
    }
}
