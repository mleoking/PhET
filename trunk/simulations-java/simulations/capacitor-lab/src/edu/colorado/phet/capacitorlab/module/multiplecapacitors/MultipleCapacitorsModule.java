// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.multiplecapacitors;

import java.awt.*;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.module.CLModule;

/**
 * The "Multiple Capacitors" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MultipleCapacitorsModule extends CLModule {

    private final MultipleCapacitorsModel model;
    private final MultipleCapacitorsCanvas canvas;
    private final MultipleCapacitorsControlPanel controlPanel;

    public MultipleCapacitorsModule( Frame parentFrame, boolean dev ) {
        super( CLStrings.MULTIPLE_CAPACITORS );

        CLModelViewTransform3D mvt = new CLModelViewTransform3D();

        model = new MultipleCapacitorsModel( getCLClock(), mvt );

        canvas = new MultipleCapacitorsCanvas( model, mvt, dev );
        setSimulationPanel( canvas );

        controlPanel = new MultipleCapacitorsControlPanel( parentFrame, this, model, canvas, dev );
        setControlPanel( controlPanel );
    }

    @Override
    public void reset() {
        super.reset();
        model.reset();
        canvas.reset();
    }

    public void setEFieldShapesDebugEnabled( boolean enabled ) {
        //XXX
    }

    public void setVoltageShapesDebugEnabled( boolean enabled ) {
        //XXX
    }
}
