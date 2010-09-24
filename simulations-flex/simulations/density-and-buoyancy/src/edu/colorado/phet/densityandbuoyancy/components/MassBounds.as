package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;

public class MassBounds implements Bounds {
    private var densityObject: DensityObject;
    public function MassBounds(densityObject:DensityObject) {
        this.densityObject=densityObject;
    }

    public function clamp( newValue: Number ): Number {
        //TODO: factor out this duplicated code
        if ( newValue > DensityConstants.STYROFOAM_MAX_MASS && densityObject.material.equals( Material.STYROFOAM ) ) {//TODO: See related workaround in CustomObjectPropertiesPanel
            newValue = DensityConstants.STYROFOAM_MAX_MASS;
            return DensityConstants.STYROFOAM_MAX_MASS;
        }
        else {
            if ( newValue > DensityConstants.WOOD_MAX_MASS && densityObject.material.equals( Material.WOOD ) ) {//TODO: See related workaround in CustomObjectPropertiesPanel
                newValue = DensityConstants.WOOD_MAX_MASS;
                return DensityConstants.WOOD_MAX_MASS;
            }
            else {
                return newValue;
            }
        }
    }
}
}