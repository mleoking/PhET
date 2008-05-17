/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.module.advanced;

import edu.colorado.phet.glaciers.view.PlayArea;


public class AdvancedController {

    private final AdvancedModel _model;
    private final PlayArea _playArea;
    private final AdvancedControlPanel _controlPanel;
    
    public AdvancedController( AdvancedModel model, PlayArea playArea, AdvancedControlPanel controlPanel ) {
        
        _model = model;
        _playArea = playArea;
        _controlPanel = controlPanel;
        
        //XXX fill this in, it will be some variation of BasicController
    }
}
