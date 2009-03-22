package edu.colorado.phet.chargesandfields {

import flash.display.Sprite;

public class MinusCharge extends Charge {
    [Embed(source='assets.swf', symbol='minusCharge_mc')]
    public static var minusMC : Class;

    public function MinusCharge( mosaic : VoltageMosaic ) {

        // create a child sprite containing the graphics
        var mc : Sprite = new minusMC();
        addChild(mc);

        q = -1;

        super(mosaic);
    }
}

}