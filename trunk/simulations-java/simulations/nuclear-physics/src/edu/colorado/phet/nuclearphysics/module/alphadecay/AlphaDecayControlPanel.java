/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.alphadecay;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;

/**
 * This class represents the control panel that presents the legend and allows
 * the user to control some aspects of the alpha decay behavior.
 *
 * @author John Blanco
 */
public class AlphaDecayControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AlphaDecayLegendPanel _legendPanel;
    private AlphaDecayNucleusSelectionPanel _selectionPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param piccoloModule
     * @param parentFrame parent frame, for creating dialogs
     * @param alphaDecayModel 
     */
    public AlphaDecayControlPanel( PiccoloModule piccoloModule, Frame parentFrame, 
    		NucleusTypeControl alphaDecayModel ) {
        
        // Set the control panel's minimum width.
        int minimumWidth = NuclearPhysicsResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Create sub-panels
        _legendPanel = new AlphaDecayLegendPanel();
        _selectionPanel = new AlphaDecayNucleusSelectionPanel( alphaDecayModel );
        
        // Add the legend panel.
        addControlFullWidth( _legendPanel );
        
        // Add the selection panel.
        addControlFullWidth( _selectionPanel );
        
        // Add the Reset All button.
        addVerticalSpace( 10 );
        addResetAllButton( piccoloModule );
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
