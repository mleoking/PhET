// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.nuclearphysics.module.fissiononenucleus;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;


public class FissionOneNucleusControlPanel extends ControlPanel {
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FissionOneNucleusLegendPanel _legendPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param fissionOneNucleusModule
     * @param parentFrame parent frame, for creating dialogs
     */
    public FissionOneNucleusControlPanel( FissionOneNucleusModule fissionOneNucleusModule, Frame parentFrame ) {
        super();
        
        // Set the control panel's minimum width.
        int minimumWidth = NuclearPhysicsResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Create sub-panels
        _legendPanel = new FissionOneNucleusLegendPanel();
        
        // Add the legend panel.
        addControlFullWidth( _legendPanel );
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
