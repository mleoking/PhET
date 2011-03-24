//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.components.DensityVBox;
import edu.colorado.phet.densityandbuoyancy.components.FluidDensityEditor;
import edu.colorado.phet.densityandbuoyancy.components.Unbounded;
import edu.colorado.phet.densityandbuoyancy.view.units.Units;
import edu.colorado.phet.flexcommon.model.NumericProperty;

import mx.containers.Grid;

public class FluidDensityControl extends DensityVBox {
    private var grid: Grid = new Grid();

    private function noClamp( n: Number ): Number {
        return n;
    }

    public function FluidDensityControl( fluidDensity: NumericProperty, units: Units ) {
        grid.addChild( new FluidDensityEditor( fluidDensity, DensityConstants.MIN_FLUID_DENSITY, DensityConstants.MAX_FLUID_DENSITY, units.densityUnit, noClamp, new Unbounded() ) );
        addChild( grid );

        setStyle( "paddingTop", 10 );
    }
}
}