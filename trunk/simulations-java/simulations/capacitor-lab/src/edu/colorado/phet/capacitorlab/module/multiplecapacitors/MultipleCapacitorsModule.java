/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module.multiplecapacitors;

import java.awt.Frame;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.capacitorlab.module.CLModule;

/**
 * The "Introduction" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MultipleCapacitorsModule extends CLModule {
    
    private final CLModel model;
    private final MultipleCapacitorsCanvas canvas;
    private final MultipleCapacitorsControlPanel controlPanel;

    public MultipleCapacitorsModule( Frame parentFrame, boolean dev ) {
        super( CLStrings.TAB_MULTIPLE_CAPACITORS );
        
        model = new CLModel();
        
        canvas = new MultipleCapacitorsCanvas( model, dev );
        setSimulationPanel( canvas );
        
        controlPanel = new MultipleCapacitorsControlPanel( this );
        setControlPanel( controlPanel );
    }
    
    @Override
    public void reset() {
        model.reset();
        canvas.reset();
    }
}
