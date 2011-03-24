//  Copyright 2002-2011, University of Colorado

/**
 * Created by ${PRODUCT_NAME}.
 * User: Sam
 * Date: 12/6/10
 * Time: 11:58 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.model.BuoyancyScale;
import edu.colorado.phet.densityandbuoyancy.model.Scale;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDBCanvas;

public class BuoyancyMode extends Mode {
    private var landScale: BuoyancyScale;
    private var waterScale: BuoyancyScale;

    public function BuoyancyMode( canvas: AbstractDBCanvas ) {
        super( canvas );
        landScale = new BuoyancyScale( Scale.GROUND_SCALE_X, Scale.GROUND_SCALE_Y, canvas.model )
        waterScale = new BuoyancyScale( Scale.POOL_SCALE_X, Scale.POOL_SCALE_Y, canvas.model );
    }

    override public function init(): void {
        super.init();
        canvas.model.addDensityObject( landScale );
        canvas.model.addDensityObject( waterScale );
    }
}
}
