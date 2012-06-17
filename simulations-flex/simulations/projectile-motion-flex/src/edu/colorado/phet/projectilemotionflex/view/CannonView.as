/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/17/12
 * Time: 9:09 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.view {
import edu.colorado.phet.projectilemotionflex.model.TrajectoryModel;

import flash.display.Graphics;

import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.geom.Point;

public class CannonView extends Sprite {
    private var mainView: MainView;
    private var trajectoryModel: TrajectoryModel;
    private var stageW: Number;
    private var stageH: Number;
    private var barrel: Sprite;
    private var carriage: Sprite;
    private var platform: Sprite;
    private var flames: Sprite;
    private var pixPerMeter: Number;



    public function CannonView( mainView:MainView,  trajectoryModel: TrajectoryModel ) {
        this.mainView = mainView;
        this.trajectoryModel = trajectoryModel;
        this.stageW = mainView.stageW;
        this.stageH = mainView.stageH;
        this.barrel = new Sprite();
        this.carriage = new Sprite();
        this.platform = new Sprite();
        this.flames = new Sprite();
        this.initialize();
    }//end constructor

    private function initialize():void{
        this.pixPerMeter = mainView.pixPerMeter;
        this.drawCannon();
        this.drawCarriage();
        this.makeBarrelTiltable();
        this.makeCarriageMovable();
        this.addChild( barrel );
        this.addChild( carriage );
        this.addChild( platform );
    }//end initialize()

    private function drawCannon():void{
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
        //var clickOffset: Point;

        function startTargetTilt( evt: MouseEvent ): void {
            //clickOffset = new Point( evt.localX, evt.localY );
            //trace("ChargeVeiw: mouseX = "+evt.stageX+"      mouseY = "+evt.stageY );
            stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetTilt );
            stage.addEventListener( MouseEvent.MOUSE_MOVE, tiltTarget );
        }

        function stopTargetTilt( evt: MouseEvent ): void {

            //var xInPix:Number = thisObject.mouseX - clickOffset.x;    //screen coordinates, origin on left edge of stage
            //var yInPix:Number = thisObject.mouseY - clickOffset.y;    //screen coordinates, origin on left edge of stage
            //thisObject.myFieldModel.setXY( xInPix,  yInPix );
            evt.updateAfterEvent();
            //clickOffset = null;
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetTilt );
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, tiltTarget );
        }

        function tiltTarget( evt: MouseEvent ): void {
            var xInPix:Number = thisObject.mouseX - thisBarrel.x; // - clickOffset.x;    //screen coordinates, origin on left edge of stage
            var yInPix:Number = thisObject.mouseY - thisBarrel.y; // - clickOffset.y;    //screen coordinates, origin on top edge of stage
            //trace("x: "+xInPix+"    y: "+yInPix);
            var angleInDeg:Number = ( 180/Math.PI )*Math.atan2( yInPix,  xInPix );
            thisBarrel.rotation = angleInDeg;
            //trace("ChargeVeiw: mouseX = "+evt.stageX+"      mouseY = "+evt.stageY );
            evt.updateAfterEvent();
        }//end of dragTarget()
    }

    private function makeCarriageMovable():void{
        var thisObject: Object = this;
        var thisCarriage: Object = this.carriage;
        this.carriage.buttonMode = true;
        this.carriage.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        var clickOffset: Point;

        function startTargetDrag( evt: MouseEvent ): void {
            clickOffset = new Point( evt.localX, evt.localY );
            //trace("ChargeVeiw: mouseX = "+evt.stageX+"      mouseY = "+evt.stageY );
            stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        }

        function stopTargetDrag( evt: MouseEvent ): void {

            //var xInPix:Number = thisObject.mouseX - clickOffset.x;    //screen coordinates, origin on left edge of stage
            //var yInPix:Number = thisObject.mouseY - clickOffset.y;    //screen coordinates, origin on left edge of stage
            //thisObject.myFieldModel.setXY( xInPix,  yInPix );
            thisObject.springTimer.stop();
            thisObject.killSpring();
            evt.updateAfterEvent();
            //clickOffset = null;
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        }

        function dragTarget( evt: MouseEvent ): void {
            //var xInPix:Number = thisObject.mouseX - clickOffset.x;    //screen coordinates, origin on left edge of stage
            //var yInPix:Number = thisObject.mouseY - clickOffset.y;    //screen coordinates, origin on top edge of stage
            //trace("x: "+xInPix+"    y: "+yInPix);
            //thisObject.myFieldModel.setXY( xInPix,  yInPix );
            //trace("ChargeVeiw: mouseX = "+evt.stageX+"      mouseY = "+evt.stageY );
            thisObject.setSpring();
            evt.updateAfterEvent();
        }//end of dragTarget()


    }

} //end class
} //end package
