/* Copyright 2007, University of Colorado */

package edu.colorado.phet.simtemplate.module.example;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.simtemplate.TemplateResources;
import edu.colorado.phet.simtemplate.control.ExampleModelElementControlPanel;
import edu.colorado.phet.simtemplate.model.ExampleModelElement;

/**
 * ExampleControlPanel is the control panel for ExampleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleControlPanel extends ControlPanel {

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
     * @param parentFrame parent frame, for creating dialogs
     */
    public ExampleControlPanel( ExampleModule module, Frame parentFrame ) {
        super();
        
        // Set the control panel's minimum width.
        int minimumWidth = TemplateResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Create sub-panels
        ExampleModelElement exampleModelElement = module.getExampleModel().getExampleModelElement();
        _exampleModelElementControlPanel = new ExampleModelElementControlPanel( exampleModelElement );
        
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
