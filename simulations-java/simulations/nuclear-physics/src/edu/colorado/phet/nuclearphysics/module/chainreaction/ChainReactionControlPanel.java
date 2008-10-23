/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.chainreaction;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;

/**
 * This class represents the control panel that presents the legend and allows
 * the user to control some aspects of the chain reaction tab of this sim.
 *
 * @author John Blanco
 */
public class ChainReactionControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ChainReactionLegendPanel _legendPanel;
    private ChainReactionControlsSubPanel _controlsPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param chainReactionModule
     * @param parentFrame parent frame, for creating dialogs
     */
    public ChainReactionControlPanel( ChainReactionModule chainReactionModule, Frame parentFrame ) {
        
        super();
        
        // Set the control panel's minimum width.
        int minimumWidth = NuclearPhysicsResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Add the legend.
        _legendPanel = new ChainReactionLegendPanel();
        addControlFullWidth( _legendPanel );

        // Add the controls that allow the user to set the number of various
        // nuclei in the sim.
        _controlsPanel = new ChainReactionControlsSubPanel(chainReactionModule.getChainReactionModel());
        addControlFullWidth( _controlsPanel );
        
        // Add the Reset All button.
        addVerticalSpace( 10 );
        addResetAllButton( chainReactionModule );

    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void closeAllDialogs() {
        //XXX close any dialogs created via the control panel
    }
    
    //----------------------------------------------------------------------------
    // Access to subpanels
    //----------------------------------------------------------------------------
}
