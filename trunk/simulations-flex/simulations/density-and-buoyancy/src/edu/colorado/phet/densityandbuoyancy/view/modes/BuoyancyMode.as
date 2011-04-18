//  Copyright 2002-2011, University of Colorado

package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.model.BuoyancyScale;
import edu.colorado.phet.densityandbuoyancy.model.Scale;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDensityAndBuoyancyPlayAreaComponent;

/**
 * All modes in the Buoyancy simulation have a scale in the land and a scale underwater.
 */
public class BuoyancyMode extends Mode {
    private var landScale: BuoyancyScale;
    private var waterScale: BuoyancyScale;

    public function BuoyancyMode( canvas: AbstractDensityAndBuoyancyPlayAreaComponent ) {
        super( canvas );
        landScale = new BuoyancyScale( Scale.GROUND_SCALE_X_RIGHT, Scale.GROUND_SCALE_Y, canvas.model )
        waterScale = new BuoyancyScale( Scale.POOL_SCALE_X, Scale.POOL_SCALE_Y, canvas.model );
    }

    override public function init(): void {
        super.init();
        canvas.model.addDensityObject( landScale );
        canvas.model.addDensityObject( waterScale );
    }
}
}
