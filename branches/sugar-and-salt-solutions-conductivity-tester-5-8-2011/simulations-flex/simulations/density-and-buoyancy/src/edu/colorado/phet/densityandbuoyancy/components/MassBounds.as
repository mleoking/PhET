//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.model.DensityAndBuoyancyObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;

/**
 * This implementation of Bounds ensures that a the mass for an object can't exceed the maximum (depends on the material type).
 */
public class MassBounds implements NumericClamp {
    private var densityObject: DensityAndBuoyancyObject;

    public function MassBounds( densityObject: DensityAndBuoyancyObject ) {
        this.densityObject = densityObject;
    }

    public function clamp( value: Number ): Number {
        //TODO: factor out this duplicated code, probably by introducing a method material.getMaximum()
        //REVIEW These constants feel kludgy here, adding another similar object in the future will be difficult because of this coupling.
        if ( value > DensityAndBuoyancyConstants.STYROFOAM_MAX_MASS && densityObject.material.equals( Material.STYROFOAM ) ) {
            return DensityAndBuoyancyConstants.STYROFOAM_MAX_MASS;
        }
        else if ( value > DensityAndBuoyancyConstants.WOOD_MAX_MASS && densityObject.material.equals( Material.WOOD ) ) {
            return DensityAndBuoyancyConstants.WOOD_MAX_MASS;
        }
        else {
            return value;
        }
    }
}
}