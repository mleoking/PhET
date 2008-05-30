/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.solidliquidgas;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.colorado.phet.nuclearphysics2.module.alpharadiation.AlphaRadiationLegendPanel;
import edu.colorado.phet.nuclearphysics2.module.alpharadiation.AlphaRadiationModule;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;


public class SolidLiquidGasControlPanel extends ControlPanel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param alphaRadiationModule
     * @param parentFrame parent frame, for creating dialogs
     */
    public SolidLiquidGasControlPanel( SolidLiquidGasModule solidLiquidGasModule, Frame parentFrame ) {
        
        super();
        
        // Set the control panel's minimum width.
        int minimumWidth = StatesOfMatterResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Add the Reset All button.
        addVerticalSpace( 10 );
        addResetAllButton( solidLiquidGasModule );

    }
    
    //----------------------------------------------------------------------------
    // Access to subpanels
    //----------------------------------------------------------------------------
}
