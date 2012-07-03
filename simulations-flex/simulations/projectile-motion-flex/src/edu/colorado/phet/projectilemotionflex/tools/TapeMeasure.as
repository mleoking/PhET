/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 7/2/12
 * Time: 7:02 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.tools {
import edu.colorado.phet.flashcommon.controls.NiceTextField;
import edu.colorado.phet.projectilemotionflex.view.MainView;

import flash.display.DisplayObjectContainer;

import flash.display.Graphics;

import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.geom.Point;

public class TapeMeasure extends Sprite {
    var mainView: MainView;
    var value: Number;                  //length of tape in meters
    var outputField: NiceTextField;     //readout of length of tape
    var angleInRad: Number;             //rotated angle of tape measure
    var tapeMeasureHolder: Sprite;      //container for parts of tapeMeasure
    var tapeMeasureBody: Sprite;        //graphic of tape measure body
    var tapeEnd: Sprite;                //graphic of grabbable end of tape
    var tape: Sprite;                   //tape stretching from body to end of tape

    public function TapeMeasure( mainView:MainView ) {
        this.mainView = mainView;
        this.value = 10;
        this.outputField = new NiceTextField( null,"", 0, 100000 );
        this.outputField.units_str = " m";
        this.tapeMeasureHolder = new Sprite();
        this.tapeMeasureBody = new Sprite();
        this.tapeEnd = new Sprite();
        this.tape = new Sprite();
        this.addChild( tapeMeasureHolder );
        this.addChild( outputField );
        outputField.y = 25;
        outputField.x = -25;
        tapeMeasureHolder.addChild( this.tape );
        tapeMeasureHolder.addChild( tapeMeasureBody );
        tapeMeasureHolder.addChild( this.tapeEnd );
        drawTapeMeasureParts();
        makeBodyGrabbable();
        makeEndGrabbable();
        setTape( value*mainView.pixPerMeter, 0 );
        //makeClipDraggable( this.tapeMeasureBody );
        //makeTapeGrabbableAndRotatable();
    }//end constructor

    private function drawTapeMeasureParts():void{
        var gMB: Graphics = this.tapeMeasureBody.graphics;
        var gTE: Graphics = this.tapeEnd.graphics;
        var gT:Graphics = this.tape.graphics;
        var w: Number = 30;  //width of body in pixels
        var r: Number = 8;  //radius of registration plus in pixels
        with(gMB){
            clear();
            lineStyle( 2, 0x000000 );
            beginFill( 0xbbbbbb );      //gray body
            drawRoundRect( -w,  -w,  w,  w,  w/4, w/4 );
            endFill();
            lineStyle( 1, 0x000000, 1, false, "normal", "square" );
            beginFill( 0xffff00 );      //yellow circle
            drawCircle( -w/2, -w/2, 0.35*w );
            endFill();
            lineStyle( 4, 0xffffff, 1, false, "normal", "square" );   //large white cross
            moveTo( -r,  0 );
            lineTo( r,  0 );
            moveTo( 0, -r );
            lineTo( 0, r )
            lineStyle( 2, 0x000000, 1, false, "normal", "square" );   //smaller black cross
            r -= 1;
            moveTo( -r,  0 );
            lineTo( r,  0 );
            moveTo( 0, -r );
            lineTo( 0, r )
        }
        with( gTE ){
            clear();
            var r: Number = 8;  //radius of registration plus in pixels
            lineStyle( 1, 0x000000, 1, false, "normal", "square" );
            beginFill( 0x000000 );
            drawCircle( 0, 0, r-3 );
            endFill();
            lineStyle( 4, 0xffffff, 1, false, "normal", "square" );   //large white cross
            moveTo( -r,  0 );
            lineTo( r,  0 );
            moveTo( 0, -r );
            lineTo( 0, r )
            lineStyle( 2, 0x000000, 1, false, "normal", "square" );   //smaller black cross
            r -= 1;
            moveTo( -r,  0 );
            lineTo( r,  0 );
            moveTo( 0, -r );
            lineTo( 0, r )
        }
        with(gT){
            var lengthInPix:Number = value*mainView.pixPerMeter;
            clear();
            lineStyle( 2, 0xdddd00);
            moveTo( 0, -1);
            lineTo( lengthInPix,  -1 );
            lineStyle( 2, 0x00bb00 );
            moveTo( 0, 1 );
            lineTo( lengthInPix, 1 ); 
        }
    }//end drawTapeMeasure

    private function makeEndGrabbable():void{
        var thisObject: TapeMeasure = this;
        var thisStage: DisplayObjectContainer;
        var delXInPix: Number;
        var delYInPix: Number;
        var lengthInPix: Number;
        var angleInDeg: Number;
        this.tapeEnd.buttonMode = true;
        this.tapeEnd.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        function startTargetDrag( evt: MouseEvent ): void {
            thisStage = thisObject.parent;
            //clickOffset = new Point( evt.localX, evt.localY );
            stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            delXInPix = thisStage.mouseX - thisObject.x;    //screen coordinates
            delYInPix = thisStage.mouseY - thisObject.y;    //screen coordinates
            lengthInPix = Math.sqrt( delXInPix*delXInPix + delYInPix*delYInPix );
            angleInDeg = Math.atan2( delYInPix, delXInPix )*180/Math.PI;
            thisObject.setTape( lengthInPix,  angleInDeg );
            evt.updateAfterEvent();
            //clickOffset = null;
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        }

        function dragTarget( evt: MouseEvent ): void {
            delXInPix = thisStage.mouseX - thisObject.x;    //screen coordinates
            delYInPix = thisStage.mouseY - thisObject.y;    //screen coordinates
            lengthInPix = Math.sqrt( delXInPix*delXInPix + delYInPix*delYInPix );
            angleInDeg = Math.atan2( delYInPix, delXInPix )*180/Math.PI;

            //trace("TapeMeasure.makeTapeEndGrabbable  delX = " + delXInPix + "   delY = " + delYInPix );
            //trace("TapeMeasure.makeTapeEndGrabbable  angleInDeg = " + angleInDeg );
            thisObject.setTape( lengthInPix,  angleInDeg );
            evt.updateAfterEvent();
        }//end of dragTarget()

    }

    private function setTape( lengthInPix:Number,  angleInDeg: Number ):void{
        if( lengthInPix > 0) {
            tapeMeasureHolder.visible = false;
            tapeMeasureHolder.rotation = 0;
            tapeEnd.x = lengthInPix;
            tape.width = lengthInPix;
            tapeMeasureHolder.rotation = angleInDeg;
            tapeMeasureHolder.visible = true;
            var lengthInMeters = lengthInPix/mainView.pixPerMeter;
            //lengthInMeters = Math.round( 10*lengthInMeters )/10;
            this.outputField.setVal( lengthInMeters );
        }
    }

    public function makeBodyGrabbable():void{
        var thisObject: TapeMeasure = this;
        var thisStage: DisplayObjectContainer;
        //var body: Object = this.tapeMeasureBody;
        this.tapeMeasureBody.buttonMode = true;
        //body.mouseChildren = false;
        this.tapeMeasureBody.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        var clickOffset: Point;

        function startTargetDrag( evt: MouseEvent ): void {
            thisObject.startDrag();
//            thisStage = thisObject.parent;
//            clickOffset = new Point( evt.localX, evt.localY );
            stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            thisObject.stopDrag();
            //var thisStage: DisplayObjectContainer = thisObject.parent;
//            var xInPix:Number = thisStage.mouseX - clickOffset.x;    //screen coordinates
//            var yInPix:Number = thisStage.mouseY - clickOffset.y;    //screen coordinates
//            thisObject.x = xInPix;
//            thisObject.y = yInPix;
//            evt.updateAfterEvent();
//            clickOffset = null;
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        }

        function dragTarget( evt: MouseEvent ): void {
            //var thisStage: DisplayObjectContainer = thisObject.parent;
//            var xInPix:Number = thisStage.mouseX - clickOffset.x;    //screen coordinates
//            var yInPix:Number = thisStage.mouseY - clickOffset.y;
//            thisObject.x = xInPix;
//            thisObject.y = yInPix;
            evt.updateAfterEvent();
        }//end of dragTarget()
    }




} //end class
} //end package
