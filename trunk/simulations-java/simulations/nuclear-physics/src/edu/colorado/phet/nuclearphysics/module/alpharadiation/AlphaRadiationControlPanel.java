/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.alpharadiation;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;

/**
 * This class represents the control panel that presents the legend and allows
 * the user to control some aspects of the alpha radiation tab of this sim.
 *
 * @author John Blanco
 */
public class AlphaRadiationControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AlphaRadiationLegendPanel _legendPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param alphaRadiationModule
     * @param parentFrame parent frame, for creating dialogs
     */
    public AlphaRadiationControlPanel( AlphaRadiationModule alphaRadiationModule, Frame parentFrame ) {
        super();
        
        // Set the control panel's minimum width.
        int minimumWidth = NuclearPhysicsResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Create sub-panels
        _legendPanel = new AlphaRadiationLegendPanel();
        
        // Add the legend panel.
        addControlFullWidth( _legendPanel );
        
        // Add the Reset All button.
        addVerticalSpace( 10 );
        addResetAllButton( alphaRadiationModule );

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
