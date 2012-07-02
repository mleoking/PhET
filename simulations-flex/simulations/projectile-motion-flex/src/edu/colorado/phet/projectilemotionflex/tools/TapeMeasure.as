/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 7/2/12
 * Time: 7:02 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.tools {
import edu.colorado.phet.flashcommon.controls.NiceTextField;

import flash.display.DisplayObjectContainer;

import flash.display.Graphics;

import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.geom.Point;

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
        //makeClipDraggable( this.tapeMeasureBody );
        //makeTapeGrabbableAndRotatable();
    }//end constructor

    private function drawTapeMeasure():void{
        var gB: Graphics = this.tapeMeasureBody.graphics;
        var gT: Graphics = this.tape.graphics;
        var w: Number = 30;  //width of body in pixels
        var r: Number = 8;  //radius of registration plus in pixels
        with(gB){
            clear();
            lineStyle( 2, 0x000000 );
            beginFill( 0xbbbbbb );      //gray body
            drawRoundRect( -w,  -w,  w,  w,  w/4, w/4 );
            endFill();
            lineStyle( 1, 0x000000 );
            beginFill( 0xffff00 );
            drawCircle( -w/2, -w/2, 0.35*w );
            endFill();
            lineStyle( 4, 0xffffff );   //large white cross
            moveTo( -r,  0 );
            lineTo( r,  0 );
            moveTo( 0, -r );
            lineTo( 0, r )
            lineStyle( 2, 0x000000 );   //smaller black cross
            r -= 1;

            moveTo( -r,  0 );
            lineTo( r,  0 );
            moveTo( 0, -r );
            lineTo( 0, r )
        }
        with( gT ){
            clear();

        }

    }//end drawTapeMeasure

    public function makeBodyGrabbable():void{
        var thisObject: Object = this;
        this.buttonMode = true;
        this.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        var clickOffset: Point;

        function startTargetDrag( evt: MouseEvent ): void {
            var thisStage: DisplayObjectContainer = thisObject.parent;
            clickOffset = new Point( evt.localX, evt.localY );
            stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            var thisStage: DisplayObjectContainer = thisObject.parent;
            var xInPix:Number = thisStage.mouseX - clickOffset.x;    //screen coordinates
            var yInPix:Number = thisStage.mouseY - clickOffset.y;    //screen coordinates
            thisObject.x = xInPix;
            thisObject.y = yInPix;
            evt.updateAfterEvent();
            clickOffset = null;
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        }

        function dragTarget( evt: MouseEvent ): void {
            var thisStage: DisplayObjectContainer = thisObject.parent;
            var xInPix:Number = thisStage.mouseX - clickOffset.x;    //screen coordinates
            var yInPix:Number = thisStage.mouseY - clickOffset.y;
            thisObject.x = xInPix;
            thisObject.y = yInPix;
            evt.updateAfterEvent();
        }//end of dragTarget()
    }



    private function makeTapeGrabbableAndRotatable():void{

}
} //end class
} //end package
