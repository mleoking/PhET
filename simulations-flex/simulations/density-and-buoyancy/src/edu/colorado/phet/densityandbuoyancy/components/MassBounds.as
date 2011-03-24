//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;

/**
 * This implementation of Bounds ensures that a the mass for an object can't exceed the maximum (depends on the material type).
 */
public class MassBounds implements Bounds {
    private var densityObject: DensityObject;

    public function MassBounds( densityObject: DensityObject ) {
        this.densityObject = densityObject;
    }

    public function clamp( value: Number ): Number {
        //TODO: factor out this duplicated code, probably by introducing a method material.getMaximum()
        if ( value > DensityConstants.STYROFOAM_MAX_MASS && densityObject.material.equals( Material.STYROFOAM ) ) {//TODO: See related workaround in CustomObjectPropertiesPanel
            return DensityConstants.STYROFOAM_MAX_MASS;
        }
        else if ( value > DensityConstants.WOOD_MAX_MASS && densityObject.material.equals( Material.WOOD ) ) {//TODO: See related workaround in CustomObjectPropertiesPanel
            return DensityConstants.WOOD_MAX_MASS;
        }
        else {
            return value;
        }
    }
}
}