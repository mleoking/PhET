//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view {
import away3d.cameras.Camera3D;
import away3d.containers.View3D;

import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.model.DensityModel;
import edu.colorado.phet.densityandbuoyancy.view.away3d.GroundNode;

import flash.display.Sprite;

public class TickMarkSet extends Sprite {
    private const tickMarks: Array = new Array();

    public function TickMarkSet( model: DensityModel ) {
        addTickMark( new TickMark( model, DensityAndBuoyancyConstants.litersToMetersCubed( 0 ) ) );
        addTickMark( new TickMark( model, DensityAndBuoyancyConstants.litersToMetersCubed( 20 ) ) );
        addTickMark( new TickMark( model, DensityAndBuoyancyConstants.litersToMetersCubed( 40 ) ) );
        addTickMark( new TickMark( model, DensityAndBuoyancyConstants.litersToMetersCubed( 60 ) ) );
        addTickMark( new TickMark( model, DensityAndBuoyancyConstants.litersToMetersCubed( 80 ) ) );
        addTickMark( new TickMark( model, DensityAndBuoyancyConstants.litersToMetersCubed( 100 ) ) );
    }

    private function addTickMark( tm: TickMark ): void {
        addChild( tm );
        tickMarks.push( tm );
    }

    public function updateCoordinates( camera: Camera3D, groundNode: GroundNode, view: View3D ): void {
        for each ( var tickMark: TickMark in tickMarks ) {
            tickMark.updateCoordinates( camera, groundNode, view );
        }
    }
}
}