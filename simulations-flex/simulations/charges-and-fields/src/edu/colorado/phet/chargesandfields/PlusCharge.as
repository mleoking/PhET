package edu.colorado.phet.chargesandfields {

import flash.display.Sprite;

public class PlusCharge extends Charge {
    [Embed(source='assets.swf', symbol='plusCharge_mc')]
    public static var plusMC : Class;

    public function PlusCharge() {

        // create a child sprite containing the graphics
        var mc : Sprite = new plusMC();
        addChild(mc);

        super();
    }
}

}