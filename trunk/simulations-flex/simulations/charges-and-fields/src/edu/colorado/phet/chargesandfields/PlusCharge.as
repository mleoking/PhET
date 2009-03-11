package edu.colorado.phet.chargesandfields {

import flash.display.Sprite;

public class PlusCharge extends Charge {
    [Embed(source='assets.swf', symbol='plusCharge_mc')]
    public static var plusMC : Class;

    public function PlusCharge(mosaic : VoltageMosaic) {

        // create a child sprite containing the graphics
        var mc : Sprite = new plusMC();
        addChild(mc);

        q = 1;

        super(mosaic);
    }
}

}