/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.nuclearreactor;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;

/**
 * This class represents the control panel that presents the legend and allows
 * the user to control some aspects of the Nuclear Reactor tab of this sim.
 *
 * @author John Blanco
 */
public class NuclearReactorControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private NuclearReactorLegendPanel _legendPanel;
    private NuclearReactorControlsSubPanel _controlSubPanel;
    
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
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void closeAllDialogs() {
        //XXX close any dialogs created via the control panel
    }
}
