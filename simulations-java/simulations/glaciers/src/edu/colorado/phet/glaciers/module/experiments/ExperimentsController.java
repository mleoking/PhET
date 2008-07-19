/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.module.experiments;

import edu.colorado.phet.glaciers.view.GlaciersPlayArea;


public class ExperimentsController {

    private final ExperimentsModel _model;
    private final GlaciersPlayArea _playArea;
    private final ExperimentsControlPanel _controlPanel;
    
    public ExperimentsController( ExperimentsModel model, GlaciersPlayArea playArea, ExperimentsControlPanel controlPanel ) {
        
        _model = model;
        _playArea = playArea;
        _controlPanel = controlPanel;
        
        //XXX fill this in, it will be some variation of BasicController
    }
}
