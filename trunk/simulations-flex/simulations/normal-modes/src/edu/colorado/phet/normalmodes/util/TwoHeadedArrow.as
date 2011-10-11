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

//A double-headed horizontal arrow, with the registration point on the left tip
//dimensions are length = 2*headWidth + shaftLength = 2*15+25 = 55 pix, height = 2*headRadius = 2*10 = 20 pix
public class TwoHeadedArrow extends Sprite {
    private var colorFill:Number;
    private var canvas:Sprite;

    public function TwoHeadedArrow() {
        this.colorFill = 0x00ff00;
        this.canvas = new Sprite();
        this.drawArrow( 10, 15, 5, 25 );
        this.addChild( this.canvas );
    } //end constructor

    //draw double-headed horizontal arrow
    private function drawArrow( headRadius:Number, headWidth:Number, shaftRadius:Number,  shaftLength:Number ):void{
        //trace("TwoHeadedArrow.drawArrow called.");
        var g:Graphics = this.canvas.graphics;
        g.clear();
        g.lineStyle( 2, 0x0000ff, 1, true );
        var hR:Number = headRadius;     //radius of arrow Head
        var hW:Number = headWidth;      //width of arrow Head (from tip to base of triangular head)
        var sR:Number = shaftRadius;    //radius of arrow shaft
        var sL:Number = shaftLength;    //length of arrow shaft
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

    public function setArrowDimensions( headRadius:Number, headWidth:Number, shaftRadius:Number, shaftLength:Number ):void{
        this.drawArrow( headRadius, headWidth, shaftRadius, shaftLength );
    }

    public function setRegistrationPointAtCenter( tOrF:Boolean ):void{
        if( tOrF ){
            this.canvas.x = - 0.5*this.canvas.width;
        }else{
            this.canvas.x = 0;
        }
    }

}//end class
}//end package
