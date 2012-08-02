
/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 7/2/12
 * Time: 7:02 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.tools {
import edu.colorado.phet.flashcommon.controls.NiceTextField;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.projectilemotionflex.view.MainView;

import flash.display.DisplayObjectContainer;

import flash.display.Graphics;

import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.geom.Point;

public class TapeMeasure extends Sprite {
    private var mainView: MainView;
    private var value: Number;                  //length of tape in meters
    private var outputField: NiceTextField;     //readout of length of tape
    private var lengthInPix: Number;            //length of tape in screen pixels
    private var _magF:Number;                    //magnification factor, read in from ControlPanel
    private var angleInDeg: Number;             //rotated angle of tape measure
    private var tapeMeasureHolder: Sprite;      //container for parts of tapeMeasure
    private var tapeMeasureBody: Sprite;        //graphic of tape measure body
    private var tapeEnd: Sprite;                //end of tape
    private var tape: Sprite;                   //tape stretching from body to end of tape

    public function TapeMeasure( mainView:MainView ) {
        this.mainView = mainView;
        this.outputField = new NiceTextField( null,"", 0, 100000, "left", 50, false );
        this.outputField.setTextFieldToAutoSize();
        this.outputField.units_str = FlexSimStrings.get( "m", " m" );

        this.value = 10;
        this.lengthInPix = value*mainView.pixPerMeter;
        this._magF = 1;
        this.angleInDeg = 0;
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

        setTapeConfigurationAndReadout( );
        //makeClipDraggable( this.tapeMeasureBody );
        //makeTapeGrabbableAndRotatable();
    }//end constructor

    private function drawTapeMeasureParts():void{
        var gMB: Graphics = this.tapeMeasureBody.graphics;
        var gTE: Graphics = this.tapeEnd.graphics;
        //var gGTE: Graphics = this.grabbableTapeEnd.graphics;
        var gT:Graphics = this.tape.graphics;
        var w: Number = 30;  //width of body in pixels
        var r: Number = 8;  //radius of registration plus in pixels
        with(gMB){
            clear();
            lineStyle( 2, 0xffffff, 0);   //large invisible grabbable area
            beginFill( 0xffffff, 0 );
            drawRect( -2*w,  -2*w, 3*w, 3*w  );
            endFill();
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
            lineStyle( 1, 0xffffff, 0 );    //large invisible grab area
            beginFill( 0x00ff00, 0 );
            var r:Number = 40;
            moveTo( -r,  -2*r );
            lineTo( r,  -2*r );
            lineTo( r,  2*r );
            lineTo( -r,  2*r );
            lineTo( -r,  -2*r );
            r = 8;    //radius of registration plus in pixels
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
        var clickOffset: Point;
        var thisStage: DisplayObjectContainer;
        var delXInPix: Number;
        var delYInPix: Number;
        //var lengthInPix: Number;
        //var angleInDeg: Number;
        this.tapeEnd.buttonMode = true;
        this.tapeEnd.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        function startTargetDrag( evt: MouseEvent ): void {
            thisStage = thisObject.parent;
            //thisEnd.startDrag();
            //Must rotate offset point according to angle so that user's finger always remains below target point
            var cos:Number = Math.cos( angleInDeg* Math.PI/180);
            var sin:Number = Math.sin( angleInDeg* Math.PI/180);
            clickOffset = new Point( cos*evt.localX - sin*evt.localY, cos*evt.localY + sin*evt.localX );
            //trace( "TapeMeasure.makeEndGrabbable  x = " + clickOffset.x + "   y = " + clickOffset.y );
            stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            var cos:Number = Math.cos( angleInDeg* Math.PI/180);
            delXInPix = thisStage.mouseX - clickOffset.x - thisObject.x;    //screen coordinates
            delYInPix = thisStage.mouseY - clickOffset.y - thisObject.y;    //screen coordinates
            lengthInPix = Math.sqrt( delXInPix*delXInPix + delYInPix*delYInPix );
            angleInDeg = Math.atan2( delYInPix, delXInPix )*180/Math.PI;
            thisObject.setTapeConfigurationAndReadout();
            //thisEnd.stopDrag();
            evt.updateAfterEvent();
            clickOffset = null;
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        }

        function dragTarget( evt: MouseEvent ): void {
            delXInPix = thisStage.mouseX - clickOffset.x - thisObject.x;    //screen coordinates
            delYInPix = thisStage.mouseY - clickOffset.y  - thisObject.y    //screen coordinates
            lengthInPix = Math.sqrt( delXInPix*delXInPix + delYInPix*delYInPix );
            angleInDeg = Math.atan2( delYInPix, delXInPix )*180/Math.PI;

            //trace("TapeMeasure.makeTapeEndGrabbable  delX = " + delXInPix + "   delY = " + delYInPix );
            //trace("TapeMeasure.makeTapeEndGrabbable  angleInDeg = " + angleInDeg );
            thisObject.setTapeConfigurationAndReadout();
            evt.updateAfterEvent();
        }//end of dragTarget()

    }

    private function setTapeConfigurationAndReadout():void{
        if( lengthInPix > 0) {
            tapeMeasureHolder.visible = false;
            tapeMeasureHolder.rotation = 0;
            tapeEnd.x = lengthInPix;
            tape.width = lengthInPix;
            tapeMeasureHolder.rotation = angleInDeg;
            tapeMeasureHolder.visible = true;
            var lengthInMeters:Number = lengthInPix/mainView.pixPerMeter;//lengthInPix*magF/mainView.pixPerMeter;
            //lengthInMeters = Math.round( 10*lengthInMeters )/10;
            this.outputField.setVal( lengthInMeters );
        }
    }

    public function resetReadout():void{
        magF = mainView.backgroundView.totMagF;
        //trace("TapeMeasure.resetReadout called.  magF = "+ magF) ;
        setTapeConfigurationAndReadout();
    }


    private function makeBodyGrabbable():void{
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


    public function get magF():Number {
        return _magF;
    }

    //called from ControlPanel when user uses zoom controls
    public function set magF(value:Number):void {
        _magF = value;
    }
} //end class
} //end package
