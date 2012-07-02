/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 7/2/12
 * Time: 7:02 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.tools {
import edu.colorado.phet.flashcommon.controls.NiceTextField;

import flash.display.Sprite;

public class TapeMeasure extends Sprite {
    var value: Number;  //length of tape in meters
    var outputField: NiceTextField;     //readout of length of tape
    var angleInRad: Number;     //rotated angle of tape measure
    var tapeMeasureBody: Sprite;      //graphic of tape measure body
    var tape: Sprite;           //graphic of tape

    public function TapeMeasure() {
        this.outputField = new NiceTextField( null,"", 0, 100000 );
        this.tapeMeasureBody = new Sprite();
        this.tape = new Sprite();
        this.addChild( tapeMeasureBody );
        this.tapeMeasureBody.addChild( this.tape );
        drawTapeMeasure();
        makeBodyGrabbable();
        makeTapeGrabbableAndRotatable();
    }//end constructor

    private function drawTapeMeasure():void{

    }

    private function makeBodyGrabbable():void{

    }

    private function makeTapeGrabbableAndRotatable():void{

}
} //end class
} //end package
