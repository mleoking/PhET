//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.view.units.Unit;
import edu.colorado.phet.flexcommon.FlexSimStrings;

import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

/**
 * Textual readout of the numeric value and units of the density, which appears underneath the density slider thumb.
 */
public class DensitySliderDataTip extends Sprite {
    private var textField: TextField;
    private var waterHeight: Number;

    public function DensitySliderDataTip() {
        super();
        textField = new TextField();
        textField.autoSize = TextFieldAutoSize.RIGHT;
        textField.text = "";
        addChild( textField );

        textField.selectable = false;
        update();
    }

    protected function update(): void {
        graphics.clear();
        var indicatedVolume: Number = waterHeight;

        var readout: Number = DensityAndBuoyancyConstants.metersCubedToLiters( indicatedVolume );//Convert SI to sim units

        var textFormat: TextFormat = new TextFormat();
        textFormat.size = 16;
        textFormat.bold = true;
        textField.setTextFormat( textFormat );

        textField.x = + 10;
        textField.y = -textField.height / 2;

        const horizontalPadding: Number = 5;

        graphics.lineStyle( 1, 0x000000 );
        graphics.beginFill( 0xFFFFFF );
        graphics.drawRoundRect( textField.x - horizontalPadding, textField.y, textField.width + 2 * horizontalPadding, textField.height, 6, 6 );
        graphics.endFill();
    }

    public function setDensity( density: Number, units: Unit ): void {
        textField.text = FlexSimStrings.get( "properties.densityValue", "{0} kg/L", [String( DensityAndBuoyancyConstants.format( units.fromSI( density ) ) )] );
        update();
    }
}
}