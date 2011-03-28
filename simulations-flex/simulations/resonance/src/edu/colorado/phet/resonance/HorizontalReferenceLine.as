/**
 * Created by ${PRODUCT_NAME}.
 * User: General User
 * Date: 3/26/11
 * Time: 4:55 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.resonance {
import flash.display.Graphics;
import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.geom.Point;

public class HorizontalReferenceLine extends Sprite{

    private var backGround:Sprite;        //invisible grabbable background
    private var line:Sprite;              //narrow horizontal line
    private var bGHeight:Number;                  //height of grabbale background
    private var lineLength:Number;                //length of line in pixels

    public function HorizontalReferenceLine() {
        this.backGround = new Sprite();
        this.line = new Sprite();
        this.bGHeight = 20;
        this.lineLength = 700;
        this.drawBackGround();
        this.drawLine();
        this.makeSpriteGrabbable( this.backGround );
        this.addChild(this.backGround);
        this.backGround.addChild(this.line);
    }

    private function drawBackGround():void{
        var g: Graphics = this.backGround.graphics;
        g.lineStyle(1, 0x000000, 0 );
        g.beginFill(0xffffff, 0);
        g.drawRect( 0, 0, this.lineLength, this.bGHeight);
        g.endFill();
    }

    private function drawLine():void{
        var g: Graphics = this.line.graphics;
        var gap:Number = 3;    //pixel gap between segments of dashed line
        var dash: Number = 10; //length of dashed segments in pixels;
        var N:int = Math.floor( this.lineLength/(gap+dash));

        g.lineStyle( 1, 0x000000, 1 );
        g.moveTo( 0, 0.5*this.bGHeight );
        for(var i:int = 0; i <= N; i++ ){
          g.moveTo( i*(gap + dash), 0.5*this.bGHeight );
          g.lineTo( i*(gap + dash) + dash, 0.5*this.bGHeight )
        }

    }

    private function makeSpriteGrabbable( mySprite:Sprite ):void{
        var target:Sprite = mySprite;
        var thisObject: Object = this;
        target.buttonMode = true;
        target.mouseChildren = false;
        target.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        var clickOffset: Point;

        function startTargetDrag( evt: MouseEvent ): void {
            clickOffset = new Point( evt.localX, evt.localY );
            stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            //trace("stop dragging");
            clickOffset = null;
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        }

        function dragTarget( evt: MouseEvent ): void {
            target.x = mouseX - clickOffset.x;
            target.y = mouseY - clickOffset.y;
            evt.updateAfterEvent();
        }//end of dragTarget()
    }//end makeSpriteGrabbable();

    public function initializePosition():void{
        this.backGround.x = 0;
        this.backGround.y = 0;
    }
}//end of class
}//end of package
