/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module.dielectric;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.capacitorlab.module.CLModule;

/**
 * The "Dielectric" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricModule extends CLModule {
    
    private final CLModel model;
    private final DielectricCanvas canvas;
    private final DielectricControlPanel controlPanel;

    public DielectricModule( boolean dev ) {
        super( CLStrings.TAB_DIELECTRIC );
        
        model = new CLModel();
        
        canvas = new DielectricCanvas( model, dev );
        setSimulationPanel( canvas );
        
        controlPanel = new DielectricControlPanel( this, model, canvas, dev );
        setControlPanel( controlPanel );
    }
    
    @Override
    public void reset() {
        model.reset();
        canvas.reset();
    }
}
