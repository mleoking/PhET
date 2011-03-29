// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.dielectric;

import java.awt.Frame;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
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
        this( CLStrings.DIELECTRIC, parentFrame, dev );
    }

    protected DielectricModule( String title, Frame parentFrame, boolean dev ) {
        super( title );

        CLModelViewTransform3D mvt = new CLModelViewTransform3D();

        model = new DielectricModel( getCLClock(), mvt);

        canvas = new DielectricCanvas( model, mvt, dev );
        setSimulationPanel( canvas );

        controlPanel = new DielectricControlPanel( parentFrame, this, model, canvas, dev );
        setControlPanel( controlPanel );

        reset();
    }

    @Override
    public void reset() {
        super.reset();
        model.reset();
        canvas.reset();
    }

    public void setEFieldShapesDebugEnabled( boolean enabled ) {
        canvas.setEFieldShapesVisible( enabled );
    }

    public void setVoltageShapesDebugEnabled( boolean enabled ) {
        canvas.setVoltageShapesVisible( enabled );
    }

    protected void setDielectricVisible( boolean visible ) {
        canvas.setDielectricVisible( false ); // hide dielectric and offset drag handle
    }

    protected void setDielectricPropertiesControlPanelVisible( boolean visible ) {
        controlPanel.setDielectricPropertiesControlPanelVisible( false ); // hide dielectric controls
    }

    protected void setDielectricOffset( double offset ) {
        model.getCapacitor().setDielectricOffset( offset );
    }

    protected void setEFieldDetectorSimplified( boolean simplified ) {
        canvas.setEFieldDetectorSimplified( simplified );
    }

    protected void setEFieldDetectorDielectricVisible( boolean visible ) {
        model.getEFieldDetector().setDielectricVisible( visible );
    }

    protected void setEFieldDetectorSumVisible( boolean visible ) {
        model.getEFieldDetector().setSumVisible( visible );
    }
}
