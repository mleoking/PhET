// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.dielectric;

import edu.colorado.phet.capacitorlab.CLGlobalProperties;
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

    public DielectricModule( CLGlobalProperties globalProperties ) {
        this( CLStrings.DIELECTRIC, globalProperties );
    }

    protected DielectricModule( String title, CLGlobalProperties globalProperties ) {
        super( title );

        CLModelViewTransform3D mvt = new CLModelViewTransform3D();

        model = new DielectricModel( getClock(), mvt );

        canvas = new DielectricCanvas( model, mvt, globalProperties );
        setSimulationPanel( canvas );

        controlPanel = new DielectricControlPanel( this, model, globalProperties );
        setControlPanel( controlPanel );

        reset();
    }

    @Override
    public void reset() {
        super.reset();
        model.reset();
        canvas.reset();
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
}
