/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.naturalselection.module.example;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.control.ExampleSubPanel;

/**
 * ExampleControlPanel is the control panel for ExampleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ExampleSubPanel _exampleSubPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     * @param parentFrame parent frame, for creating dialogs
     */
    public ExampleControlPanel( ExampleModule module, Frame parentFrame ) {
        super();
        
        // Set the control panel's minimum width.
        int minimumWidth = NaturalSelectionResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Create sub-panels
        _exampleSubPanel = new ExampleSubPanel();
        
        // Layout
        {
            addControlFullWidth( _exampleSubPanel );
            addSeparator();
            addResetAllButton( module );
        }
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
    
    public ExampleSubPanel getExampleSubPanel() {
        return _exampleSubPanel;
    }

}
