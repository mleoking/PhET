/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.module.example;

import edu.colorado.phet.glaciers.GlaciersResources;
import edu.colorado.phet.glaciers.control.ExampleModelElementControlPanel;
import edu.colorado.phet.glaciers.model.ExampleModelElement;
import edu.colorado.phet.glaciers.module.GlaciersAbstractControlPanel;

/**
 * ExampleControlPanel is the control panel for ExampleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleControlPanel extends GlaciersAbstractControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ExampleModelElementControlPanel _exampleModelElementControlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     */
    public ExampleControlPanel( ExampleModule module) {
        super( module );
        
        // Set the control panel's minimum width.
        int minimumWidth = GlaciersResources.getInt( "int.minControlPanelWidth", 215 );
        setMinumumWidth( minimumWidth );
        
        // Create sub-panels
        ExampleModelElement exampleModelElement = module.getExampleModel().getExampleModelElement();
        _exampleModelElementControlPanel = new ExampleModelElementControlPanel( TITLE_FONT, CONTROL_FONT, exampleModelElement );
        
        // Layout
        {
            addControlFullWidth( _exampleModelElementControlPanel );
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
    
    public ExampleModelElementControlPanel getExampleModelElementControlPanel() {
        return _exampleModelElementControlPanel;
    }

}
