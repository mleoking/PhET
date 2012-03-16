//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.view.units.Units;
import edu.colorado.phet.flexcommon.model.NumericProperty;

import mx.containers.Grid;

/**
 * In the Buoyancy sim, shows a slider where the user can change the fluid density.
 */
public class FluidDensityControl extends DensityVBox {
    private var grid: Grid = new Grid();

    public function FluidDensityControl( fluidDensity: NumericProperty, units: Units ) {
        grid.addChild( new FluidDensityEditor( fluidDensity, DensityAndBuoyancyConstants.MIN_FLUID_DENSITY, DensityAndBuoyancyConstants.MAX_FLUID_DENSITY, units.densityUnit, new Unbounded().clamp, new Unbounded() ) );
        addChild( grid );

        setStyle( "paddingTop", 10 );
    }
}
}