/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.nuclearreactor;

import java.awt.Dimension;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.umd.cs.piccolo.util.PDimension;

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
    
    // Using a fixed constant for this, but there may be a better way to do it
    // more dynamically.
    private static final int ENERGY_GRAPH_PANEL_HEIGHT = 300;
    
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
        int minimumWidth = NuclearPhysics2Resources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Create sub-panels
        _legendPanel = new NuclearReactorLegendPanel();
        
        // Add the legend panel.
        addControlFullWidth( _legendPanel );

        // Add the sub panel with the interactive controls.
        _controlSubPanel = new NuclearReactorControlsSubPanel(nuclearReactorModule.getNuclearReactorModel());
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
