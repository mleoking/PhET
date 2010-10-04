package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.model.NumericProperty;

public class LiquidDensityModel {
    private var _density: NumericProperty = new NumericProperty( "Liquid Density", "kg/L", Material.WATER.getDensity() );

    public function LiquidDensityModel() {
    }

    public function get density(): NumericProperty {
        return _density;
    }
}
}