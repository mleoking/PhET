/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/6/12
 * Time: 8:40 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.radiatingcharge.view {
import edu.colorado.phet.radiatingcharge.model.FieldModel;

import flash.display.Graphics;
import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.events.TimerEvent;
import flash.events.TimerEvent;
import flash.utils.Timer;

public class ChargeView extends Sprite {

    private var myMainView: MainView;
    private var myFieldModel: FieldModel;
    private var chargeGraphic:Sprite;
    private var springGraphic:Sprite;

    private var stageW: int;
    private var stageH: int;
    private var delX:Number;        //x-component of displacement between charge and mouse (spring end)
    private var delY:Number;        //y-component of displacement between charge and mouse (spring end)
    private var originX:Number;     // location, in screen coordinates, of origin
    private var originY:Number;     //
    private var dt:Number;          //time step, in seconds
    private var springTimer:Timer;  //spring timer

    public function ChargeView( myMainView:MainView, myFieldModel:FieldModel) {
        this.myMainView = myMainView;
        this.myFieldModel = myFieldModel;
        this.myFieldModel.registerView( this );
        this.chargeGraphic = new Sprite();
        this.originX = stageW/2;
        this.originY = stageH/2;
        this.chargeGraphic.x = originX;
        this.chargeGraphic.y = originY;
        this.springGraphic = new Sprite();
        this.delX = 0;
        this.delY = 0;
        this.dt = this.myFieldModel.getDT();
        this.springTimer = new Timer( this.dt * 1000 );
        this.initialize();
    }

    private function initialize():void{
        this.stageW = this.myMainView.stageW;
        this.stageH = this.myMainView.stageH;
        this.addChild( this.chargeGraphic );
        this.addChild( this.springGraphic );
        this.drawChargeGraphic();
        this.makeChargeGrabbable();
        this.springTimer.addEventListener( TimerEvent.TIMER, updateSpring );
        this.update();

    } //end of initialize

    private function drawChargeGraphic():void{
        var g:Graphics = this.chargeGraphic.graphics;
        g.clear();
        g.lineStyle( 2, 0x000000, 1 );
        g.beginFill( 0x000000, 1 );
        g.drawCircle( 0, 0, 10 );
        g.endFill();
    }//end drawChargeGraphic

    private function setSpring( ):void{
        var g:Graphics = this.springGraphic.graphics;
        g.clear();
        g.lineStyle( 2, 0xff0000, 1 );
        g.moveTo( this.chargeGraphic.x,  this.chargeGraphic.y );
        g.lineTo( this.mouseX,  this.mouseY );
        this.delX = this.mouseX - this.chargeGraphic.x;
        this.delY = -( this.mouseY - this.chargeGraphic.y );
        this.myFieldModel.setForce( delX,  delY );
        //trace("ChargeView.drawSpringGraphic mouseX = "+this.mouseX+"    this.chargeGraphic.x = "+this.chargeGraphic.x) ;
        //g.lineTo( evt.stageX,  evt.stageY );     //does not work: evt.stageX is (1/2) value of mouseX ??
        //trace("ChargeView: evt.stageX = "+evt.stageX );
    }

    private function updateSpring( evt:TimerEvent ):void{
        this.setSpring();

    }

    private function killSpring():void{
        var g:Graphics = this.springGraphic.graphics;
        g.clear();
        this.delX = 0;
        this.delY = 0;
        this.myFieldModel.setForce( delX,  delY );
    }

    private function makeChargeGrabbable():void{
        var thisObject:Object = this;
        this.chargeGraphic.buttonMode = true;
        this.chargeGraphic.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        //var clickOffset: Point;

        function startTargetDrag( evt: MouseEvent ): void {
            //clickOffset = new Point( evt.localX, evt.localY );
            //trace("ChargeVeiw: mouseX = "+evt.stageX+"      mouseY = "+evt.stageY );
            thisObject.springTimer.start();
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


    }//end makeChargeGrabbable

    public function update():void{
        this.chargeGraphic.x = this.stageW/2 + this.myFieldModel.xC;
        this.chargeGraphic.y = this.stageH/2 - this.myFieldModel.yC;
        //trace( "ChargeView.update. chargeGraphic.y = "+ this.chargeGraphic.y) ;
        //trace("ChargeView.update called model.xC = "+ this.myFieldModel.xC)
    }

}  //end of class
}  //end of package
