/* Copyright 2007, University of Colorado */

package edu.colorado.phet.mvcexample.control;

import java.awt.Font;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.mvcexample.MVCModule;
import edu.colorado.phet.mvcexample.model.AModelElement;
import edu.colorado.phet.mvcexample.model.BModelElement;
import edu.colorado.phet.mvcexample.model.MVCModel;

/**
 * MVCControlPanel
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MVCControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AControlPanel _aControlPanel;
    private BControlPanel _bControlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     */
    public MVCControlPanel( MVCModule module, MVCModel model, Frame parentFrame ) {
        super();
        
        // A
        AModelElement aModelElement = model.getAModelElement();
        _aControlPanel = new AControlPanel( aModelElement );
        
        // B
        BModelElement bModelElement = model.getBModelElement();
        _bControlPanel = new BControlPanel( bModelElement );
        
        // Layout
        addControlFullWidth( _aControlPanel );
        addSeparator();
        addControlFullWidth( _bControlPanel );
        addSeparator();
        addResetAllButton( module );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public BControlPanel getBControlPanel() {
        return _bControlPanel;
    }
}
