/**
 * Created by IntelliJ IDEA.
 * User: General User
 * Date: 6/19/11
 * Time: 12:06 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes.util {
import flash.display.Graphics;
import flash.display.Sprite;

public class TwoHeadedArrow extends Sprite {
    private var colorFill:Number;

    public function TwoHeadedArrow() {
        this.colorFill = 0x00ff00;
        this.drawArrow();
    } //end constructor

    //draw double-headed horizontal arrow
    private function drawArrow():void{
        //trace("TwoHeadedArrow.drawArrow called.");
        var g:Graphics = this.graphics;
        g.clear();
        g.lineStyle( 2, 0x0000ff, 1, true );
        var hR:Number = 10; //radius of arrow Head
        var hW:Number = 15; //half-height of arrow Head
        var sR:Number = 5;  //radius of arrow shaft
        var sL:Number = 25; //length of arrow shart
        g.beginFill( this.colorFill, 1 );
        g.moveTo( 0, 0 );
        g.lineTo( hW,  -hR );
        g.lineTo( hW,  -sR );
        g.lineTo( hW + sL, -sR );
        g.lineTo( hW + sL, -hR );
        g.lineTo( hW + sL + hW,  0 );
        g.lineTo( hW + sL,  hR );
        g.lineTo( hW + sL,  sR );
        g.lineTo( hW,  sR );
        g.lineTo( hW, hR );
        g.lineTo( 0, 0 );
        g.endFill();
    } //end drawArrow()

}//end class
}//end package
