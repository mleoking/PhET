package edu.colorado.phet.chargesandfields {

public class MinusCharge extends Charge {
    [Embed(source='assets.swf', symbol='minusCharge_mc')]
    public static var minusMC: Class;

    public function MinusCharge( mosaic: VoltageMosaic ) {

        // create a child sprite containing the graphics
        chargeMC = new minusMC();
        addChild( chargeMC );

        q = -1;

        super( mosaic );
    }
}

}