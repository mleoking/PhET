/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.module.advanced;

import edu.colorado.phet.glaciers.view.PlayArea;


public class AdvancedController {

    private AdvancedModel _model;
    private PlayArea _playArea;
    private AdvancedControlPanel _controlPanel;
    
    public AdvancedController( AdvancedModel model, PlayArea playArea, AdvancedControlPanel controlPanel ) {
        
        _model = model;
        _playArea = playArea;
        _controlPanel = controlPanel;
        
        //XXX fill this in, it will be some variation of BasicController
    }
}
