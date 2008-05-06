/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.nuclearreactor;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.colorado.phet.nuclearphysics2.module.alpharadiation.AlphaRadiationLegendPanel;
import edu.colorado.phet.nuclearphysics2.module.alpharadiation.AlphaRadiationModule;

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
        
        // Add the Reset All button.
        addVerticalSpace( 10 );
        addResetAllButton( nuclearReactorModule );

    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void closeAllDialogs() {
        //XXX close any dialogs created via the control panel
    }
}
