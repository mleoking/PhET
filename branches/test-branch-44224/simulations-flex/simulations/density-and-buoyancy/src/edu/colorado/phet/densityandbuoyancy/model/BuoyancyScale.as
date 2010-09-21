package edu.colorado.phet.densityandbuoyancy.model {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.flexcommon.FlexSimStrings;

public class BuoyancyScale extends Scale {
    public function BuoyancyScale( x:Number, y:Number, model:DensityModel ) {
        super( x, y, model );
    }

    override public function getScaleReadout():String {
        // scaled by DT-frame because we are measuring the 'normal impulses'
        //impulse I=Fdt
        //F=I/dt
        var force:Number = totalImpulse / DensityModel.DT_PER_FRAME;
        return FlexSimStrings.get("properties.weightNewtonValue", "{0} N", [DensityConstants.format(force)]);
    }
}
}