// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.multiplecapacitors;

import edu.colorado.phet.capacitorlab.CLGlobalProperties;
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

    public MultipleCapacitorsModule( CLGlobalProperties globalProperties ) {
        super( CLStrings.MULTIPLE_CAPACITORS );

        CLModelViewTransform3D mvt = new CLModelViewTransform3D();

        model = new MultipleCapacitorsModel( getClock(), mvt );

        canvas = new MultipleCapacitorsCanvas( model, mvt, globalProperties );
        setSimulationPanel( canvas );

        controlPanel = new MultipleCapacitorsControlPanel( this, model, canvas, globalProperties );
        setControlPanel( controlPanel );
    }

    @Override
    public void reset() {
        super.reset();
        model.reset();
        canvas.reset();
    }
}
