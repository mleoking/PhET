package edu.colorado.phet.chargesandfields {

public class PlusCharge extends Charge {
    [Embed(source='assets.swf', symbol='plusCharge_mc')]
    public static var plusMC: Class;

    public function PlusCharge( mosaic: VoltageMosaic ) {

        // create a child sprite containing the graphics
        chargeMC = new plusMC();
        addChild( chargeMC );

        q = 1;

        super( mosaic );
    }
}

}