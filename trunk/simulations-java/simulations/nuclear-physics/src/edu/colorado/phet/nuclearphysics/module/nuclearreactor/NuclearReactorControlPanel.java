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
    private NuclearReactorControlsSubPanel _controlSubPanel;
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

        // Add the sub panel with the interactive controls.
        _controlSubPanel = new NuclearReactorControlsSubPanel(parentFrame, 
                nuclearReactorModule.getNuclearReactorModel());
        addControlFullWidth( _controlSubPanel );
        
        // Register as a listener with the control panel for param changes.
        _controlSubPanel.addListener( new NuclearReactorControlsSubPanel.Listener(){
            public void parameterChanged(){
                _energyGraphPanel.setVisible( _controlSubPanel.getEnergyGraphCheckBoxState() );
            }
        });
        
        // Add the energy graph.
        _energyGraphPanel = new NuclearReactorEnergyGraphPanel(nuclearReactorModule.getNuclearReactorModel());
//        _energyGraphPanel.setPreferredSize( new Dimension(getWidth(), ENERGY_GRAPH_PANEL_HEIGHT ));
        addControlFullWidth( _energyGraphPanel );
        _energyGraphPanel.setVisible( _controlSubPanel.getEnergyGraphCheckBoxState() );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void closeAllDialogs() {
        //XXX close any dialogs created via the control panel
    }
    
    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------
    
}
