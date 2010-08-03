package edu.colorado.phet.densityflex.view {
import edu.colorado.phet.densityflex.components.IDensityObject;
import edu.colorado.phet.densityflex.components.NumericProperty;

public class DefaultDensityObject implements IDensityObject {
    private const volume:NumericProperty = new NumericProperty("Volume", "m^3", 1.0);
    private const mass:NumericProperty = new NumericProperty("Mass", "kg", 1.0);
    private const density:NumericProperty = new NumericProperty("Density", "kg/m^3", 1.0);

    public function DefaultDensityObject() {
    }

    public function getVolume():NumericProperty {
        return volume;
    }

    public function getMass():NumericProperty {
        return mass;
    }

    public function getDensity():NumericProperty {
        return density;
    }
}
}