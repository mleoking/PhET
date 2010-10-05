/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module.dielectric;

import java.awt.Frame;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.module.CLModule;

/**
 * The "Dielectric" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricModule extends CLModule {
    
    private final DielectricModel model;
    private final DielectricCanvas canvas;
    private final DielectricControlPanel controlPanel;
    
    public DielectricModule( Frame parentFrame, boolean dev ) {
        this( CLStrings.TAB_DIELECTRIC, parentFrame, true /* hasDielectricPropertiesControl */, dev );
    }

    protected DielectricModule( String title, Frame parentFrame, boolean hasDielectricPropertiesControl, boolean dev ) {
        super( title );
        
        model = new DielectricModel( getCLClock() );
        
        canvas = new DielectricCanvas( model, dev );
        setSimulationPanel( canvas );
        
        controlPanel = new DielectricControlPanel( parentFrame, this, model, canvas, hasDielectricPropertiesControl, dev );
        setControlPanel( controlPanel );
    }
    
    @Override
    public void reset() {
        super.reset();
        model.reset();
        canvas.reset();
    }
    
    protected DielectricModel getDielectricModel() {
        return model;
    }
    
    protected DielectricCanvas getDielectricCanvas() {
        return canvas;
    }
    
    protected DielectricControlPanel getDielectricControlPanel() {
        return controlPanel;
    }
}
