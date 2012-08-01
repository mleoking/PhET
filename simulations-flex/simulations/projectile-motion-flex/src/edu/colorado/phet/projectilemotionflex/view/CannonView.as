/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/17/12
 * Time: 9:09 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.view {
import edu.colorado.phet.projectilemotionflex.model.MainModel;

import flash.display.Graphics;
import flash.display.MovieClip;

import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.geom.Point;

public class CannonView extends Sprite {
    private var mainView: MainView;
    private var trajectoryModel: MainModel;
    private var background: BackgroundView;
    //public var trajectoryView: TrajectoryView;
    private var stageW: Number;
    private var stageH: Number;
    private var barrel: Sprite;
    private var carriage: Sprite;
    private var platform: Sprite;
    //private var oldCannon: Sprite;
    private var flames: Sprite;
    private var pixPerMeter: Number;
    private var originXinPix: Number;   //location of model's origin in stage coordinates
    private var originYinPix: Number;

    [Embed(source='projectile-motion-flex-graphics.swf',symbol='barrel')]
    public static var Barrel: Class;

    [Embed(source='projectile-motion-flex-graphics.swf',symbol='cannonCarriage')]
    public static var Carriage: Class;


    public function CannonView( mainView:MainView,  trajectoryModel: MainModel, background:BackgroundView ) {
        this.mainView = mainView;
        this.trajectoryModel = trajectoryModel;
        this.background = background;
        this.stageW = mainView.stageW;
        this.stageH = mainView.stageH;
        this.barrel = new Barrel(); //new Sprite();
        this.carriage = new Carriage(); //new Sprite();
        this.platform = new Sprite();
        this.flames = new Sprite();
        //this.oldCannon = new Cannon2();
//        this.oldCannon.gotoAndStop(1);
        this.initialize();
    }//end constructor

    private function initialize():void{
        trajectoryModel.registerView( this );
        this.pixPerMeter = mainView.pixPerMeter;
        this.originXinPix = mainView.originXInPix;    //must set originInPix before instantiating trajectoryView
        this.originYinPix = mainView.originYInPix;
        //this.trajectoryView = new TrajectoryView( mainView,  trajectoryModel );
        //this.drawCannon();
        //this.drawCarriage();
        this.makeBarrelTiltable();
        this.makeCarriageMovable();

        this.addChild( barrel );
        this.addChild( carriage );
        this.addChild( platform );
        //this.addChild( oldCannon );
        //this.addChild( trajectoryView );

        this.update();
    }//end initialize()

    private function drawCannon():void{
        //var pixPerM = mainView.pixPerMeter
        var L:Number = 2*pixPerMeter;
        var W:Number = 0.4*pixPerMeter;
        var gB: Graphics = barrel.graphics;
        with( gB ){
            clear();
            lineStyle( 1, 0x000000, 1 );
            beginFill( 0xbbbbbb, 1 );
            moveTo( 0 , -W/2 );
            lineTo( 0,  W/2 );
            lineTo( L,  W/2 );
            lineTo( L, -W/2 );
            lineTo( 0, -W/2 );
            endFill();
        }
    }//end drawCannon()

    private function drawCarriage():void{
        //var pixPerM = mainView.pixPerMeter
        var B:Number = 2*pixPerMeter;   //base width
        var H:Number = 1*pixPerMeter;   //height
        var W:Number = 0.4*pixPerMeter; //width of support
        var gC: Graphics = this.carriage.graphics;
        with( gC ){
            clear();
            lineStyle( 1, 0x000000, 1 );
            beginFill( 0x996633 );
            moveTo( 0, 0 );
            lineTo( -W/2, 0 );
            lineTo( -W/2, H );
            lineTo( - B/2, H );
            lineTo( - B/2, H + W );
            lineTo( B/2, H + W );
            lineTo( B/2, H );
            lineTo( W/2, H );
            lineTo( W/2, 0 );
            lineTo( 0, 0 );
            endFill();
        }
    }

    private function makeBarrelTiltable():void{
        var thisObject:Object = this;
        var thisBarrel:Object = this.barrel;
        this.barrel.buttonMode = true;
        this.barrel.addEventListener( MouseEvent.MOUSE_DOWN, startTargetTilt );

        function startTargetTilt( evt: MouseEvent ): void {
            //clickOffset = new Point( evt.localX, evt.localY );
            //trace("ChargeVeiw: mouseX = "+evt.stageX+"      mouseY = "+evt.stageY );
            stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetTilt );
            stage.addEventListener( MouseEvent.MOUSE_MOVE, tiltTarget );
        }

        function stopTargetTilt( evt: MouseEvent ): void {
            var xInPix:Number = thisObject.mouseX - thisBarrel.x;   //screen coordinates, origin on left edge of stage
            var yInPix:Number = thisObject.mouseY - thisBarrel.y;   //screen coordinates, origin on top edge of stage
            var angleInDeg:Number = -( 180/Math.PI )*Math.atan2( yInPix,  xInPix );
            thisObject.trajectoryModel.angleInDeg = angleInDeg;
            evt.updateAfterEvent();
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetTilt );
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, tiltTarget );
        }

        function tiltTarget( evt: MouseEvent ): void {
            var xInPix:Number = thisObject.mouseX - thisBarrel.x;   //screen coordinates, origin on left edge of stage
            var yInPix:Number = thisObject.mouseY - thisBarrel.y;   //screen coordinates, origin on top edge of stage
            var angleInDeg:Number = -( 180/Math.PI )*Math.atan2( yInPix,  xInPix );
            thisObject.trajectoryModel.angleInDeg = angleInDeg;
            evt.updateAfterEvent();
        }//end of dragTarget()
    }

    private function makeCarriageMovable():void{
        var thisObject: Object = this;
        var thisCarriage: Sprite = this.carriage;
        this.carriage.buttonMode = true;
        this.carriage.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        var clickOffset: Point;


        function startTargetDrag( evt: MouseEvent ): void {
            var scaleFactor: Number = thisObject.background.container.scaleX;
            clickOffset = new Point( evt.localX*scaleFactor, evt.localY*scaleFactor );

            stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            var xInPix:Number = thisObject.background.mouseX - clickOffset.x;    //screen coordinates
            var yInPix:Number = thisObject.background.mouseY - clickOffset.y;    //screen coordinates

            thisObject.trajectoryModel.xP0 = ( xInPix - thisObject.originXinPix )/(thisObject.mainView.pixPerMeter);
            thisObject.trajectoryModel.yP0 = ( thisObject.originYinPix - yInPix ) /(thisObject.mainView.pixPerMeter);   //minus sign is tricky!  Remember that stageY and modelY are in opposite directions
            evt.updateAfterEvent();
            clickOffset = null;
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        }

        function dragTarget( evt: MouseEvent ): void {
            var xInPix:Number = thisObject.background.mouseX - clickOffset.x;    //screen coordinates
            //trace("CannonView.dragTarget  xInPix = " + xInPix );
            var yInPix:Number = thisObject.background.mouseY - clickOffset.y;
            //trace("CannonView.dragTarget  yInPix = " + yInPix );
            thisObject.trajectoryModel.xP0 = ( xInPix - thisObject.originXinPix )/(thisObject.mainView.pixPerMeter);
            //trace("CannonView.thisObject.originXinPix = " + thisObject.originXinPix );
            thisObject.trajectoryModel.yP0 = ( thisObject.originYinPix - yInPix )/(thisObject.mainView.pixPerMeter);
            evt.updateAfterEvent();
        }//end of dragTarget()


    } //end makeCarriageMovable

    public function update():void{
        this.barrel.rotation = -trajectoryModel.angleInDeg;
        //trace("cannonView.update called, angle = " + trajectoryModel.angleInDeg );
        this.x = (this.originXinPix + this.trajectoryModel.xP0*mainView.pixPerMeter /this.background.container.scaleX );
        this.y = (this.originYinPix - this.trajectoryModel.yP0*mainView.pixPerMeter /this.background.container.scaleY );
        //trace("cannonView.update called, y in pix = " + this.y  );
        //trace("cannonView.update called, y in meters = " + this.trajectoryModel.yP0  );
    }


} //end class
} //end package
