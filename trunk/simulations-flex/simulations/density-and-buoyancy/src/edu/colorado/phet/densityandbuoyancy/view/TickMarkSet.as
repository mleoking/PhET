package edu.colorado.phet.densityandbuoyancy.view {
import away3d.cameras.HoverCamera3D;
import away3d.containers.View3D;

import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.DensityModel;

import flash.display.Sprite;

public class TickMarkSet extends Sprite {
    private const tickMarks:Array = new Array();

    public function TickMarkSet(model:DensityModel) {
        addTickMark(new TickMark(model, DensityConstants.litersToMetersCubed(0)));
        addTickMark(new TickMark(model, DensityConstants.litersToMetersCubed(25)));
        addTickMark(new TickMark(model, DensityConstants.litersToMetersCubed(50)));
        addTickMark(new TickMark(model, DensityConstants.litersToMetersCubed(75)));
        addTickMark(new TickMark(model, DensityConstants.litersToMetersCubed(100)));
        addTickMark(new TickMark(model, DensityConstants.litersToMetersCubed(125)));
    }

    private function addTickMark(tm:TickMark):void {
        addChild(tm);
        tickMarks.push(tm);
    }

    public function updateCoordinates(camera:HoverCamera3D, groundNode:GroundNode, view:View3D):void {
        for each (var tickMark:TickMark in tickMarks) {
            tickMark.updateCoordinates(camera, groundNode, view);
        }
    }
}
}