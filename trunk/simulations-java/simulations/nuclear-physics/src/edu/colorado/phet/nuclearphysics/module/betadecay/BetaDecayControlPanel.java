/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.betadecay;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.module.alphadecay.NucleusTypeControl;

/**
 * This class represents the control panel that presents the legend and allows
 * the user to control some aspects of the beta decay behavior.
 *
 * @author John Blanco
 */
public class BetaDecayControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BetaDecayLegendPanel _legendPanel;
    private BetaDecayNucleusSelectionPanel _selectionPanel;
    
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
    public BetaDecayControlPanel( PiccoloModule piccoloModule, Frame parentFrame, 
    		NucleusTypeControl alphaDecayModel ) {
        
        // Set the control panel's minimum width.
        int minimumWidth = NuclearPhysicsResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Create sub-panels
        _legendPanel = new BetaDecayLegendPanel();
        _selectionPanel = new BetaDecayNucleusSelectionPanel( alphaDecayModel );
        
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
