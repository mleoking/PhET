package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.components.DensityVBox;
import edu.colorado.phet.densityandbuoyancy.components.MassBounds;
import edu.colorado.phet.densityandbuoyancy.components.PropertyEditor;
import edu.colorado.phet.densityandbuoyancy.components.Unbounded;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.view.units.Units;

import mx.containers.Grid;

public class FluidDensityControl extends DensityVBox {
    private var grid:Grid = new Grid();

    function noClamp(n:Number):Number {
        return n;
    }

    public function FluidDensityControl(liquidDensityModel:LiquidDensityModel, units:Units) {
        grid.addChild(new PropertyEditor(liquidDensityModel.density, DensityConstants.MIN_FLUID_DENSITY, DensityConstants.MAX_FLUID_DENSITY, units.densityUnit, noClamp, new Unbounded()));
        addChild(grid);
    }
}
}