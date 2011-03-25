//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view {
import away3d.cameras.Camera3D;
import away3d.containers.View3D;
import away3d.core.base.Vertex;
import away3d.core.draw.ScreenVertex;

import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.model.DensityModel;
import edu.colorado.phet.densityandbuoyancy.view.away3d.GroundNode;

import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

public class TickMark extends Sprite {
    private var textField: TextField;
    private var waterHeight: Number;
    private var model: DensityModel;
    private var value: Number;

    public function TickMark( model: DensityModel, value: Number ) {
        super();
        this.value = value;
        this.model = model;
        textField = new TextField();
        textField.autoSize = TextFieldAutoSize.RIGHT;
        textField.text = "hello";
        addChild( textField );

        textField.selectable = false;
        this.visible = false;//show after locaiton is correct
        update();
    }

    protected function update(): void {
        graphics.clear();
        var indicatedVolume: Number = value;

        var readout: Number = DensityAndBuoyancyConstants.metersToLitersCubed( indicatedVolume );//Convert SI to display units

        textField.text = String( DensityAndBuoyancyConstants.format( readout ) );
        var textFormat: TextFormat = new TextFormat();
        textFormat.size = 14;
        textFormat.bold = true;
        textField.setTextFormat( textFormat );
        graphics.beginFill( 0x000000 );
        var x: Number = 0;
        graphics.moveTo( 0 + x, 0 );
        graphics.lineTo( +10 + x, -2 );
        graphics.lineTo( +10 + x, 2 );
        graphics.lineTo( 0 + x, 0 );
        graphics.endFill();

        textField.x = + 10;
        textField.y = -textField.height / 2;
    }


    public function updateCoordinates( camera: Camera3D, groundNode: GroundNode, view: View3D ): void {
        var height: Number = value / model.getPoolDepth() / model.getPoolWidth();
        var screenVertex: ScreenVertex = camera.screen( groundNode, new Vertex( model.getPoolWidth() * DensityModel.DISPLAY_SCALE / 2, (-model.getPoolHeight() + height) * DensityModel.DISPLAY_SCALE, 0 ) );
        this.x = screenVertex.x + view.x;
        this.y = screenVertex.y + view.y;
        this.visible = true;//Now can show the water volume indicator after it is at the right location
    }
}
}