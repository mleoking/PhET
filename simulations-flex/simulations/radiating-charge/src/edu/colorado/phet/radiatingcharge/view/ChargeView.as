/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/6/12
 * Time: 8:40 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.radiatingcharge.view {
import edu.colorado.phet.radiatingcharge.model.FieldModel;

import flash.display.CapsStyle;

import flash.display.Graphics;
import flash.display.MovieClip;
import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.events.TimerEvent;
import flash.events.TimerEvent;
import flash.utils.Timer;

public class ChargeView extends Sprite {

    private var myMainView: MainView;
    private var myFieldModel: FieldModel;
    //private var chargeGraphic:Sprite;
    private var springGraphic:Sprite;

    //private var springGraphic2: Sprite;   //for use when mouse_over, before mouse_down
    private var grabArea: Sprite;
    private var grabbed: Boolean;   //true if charge is grabbed

    private var stageW: int;
    private var stageH: int;
    private var delX:Number;        //x-component of displacement between charge and mouse (spring end)
    private var delY:Number;        //y-component of displacement between charge and mouse (spring end)
    private var originX:Number;     // location, in screen coordinates, of origin
    private var originY:Number;     //
    private var dt:Number;          //time step, in seconds
    private var springTimer:Timer;  //spring timer
    private var chargeGraphic:Sprite;
    [Embed(source='radiatingChargeGraphics.swf',symbol='chargeBall')]
    public static var ChargeGraphic: Class;

    public function ChargeView( myMainView:MainView, myFieldModel:FieldModel) {
        this.myMainView = myMainView;
        this.myFieldModel = myFieldModel;
        this.myFieldModel.registerView( this );

        this.grabArea = new Sprite();
        this.originX = stageW/2;
        this.originY = stageH/2;

        this.springGraphic = new Sprite();
        this.delX = 0;
        this.delY = 0;
        this.grabbed = false;
        this.dt = this.myFieldModel.getDT();
        this.springTimer = new Timer( this.dt * 1000 );

        this.chargeGraphic = new ChargeGraphic();//new Sprite();
        this.chargeGraphic.x = originX;
        this.chargeGraphic.y = originY;
        //this.springGraphic2 = new Sprite();
        this.initialize();
    }

    private function initialize():void{
        this.stageW = this.myMainView.stageW;
        this.stageH = this.myMainView.stageH;
        //this.addChild( this.springGraphic2 );
        this.addChild( this.chargeGraphic );
        this.chargeGraphic.addChild( myMainView.myVelocityArrowView.velocityArrow );
        this.addChild( this.springGraphic );
        //this.chargeGraphic.addChild( this.springGraphic );
        this.chargeGraphic.addChild( this.grabArea );
        //this.drawChargeGraphic();
        this.makeChargeGrabbable();
        this.springTimer.addEventListener( TimerEvent.TIMER, updateSpring );
        this.update();

    } //end of initialize

    private function drawChargeGraphic():void{
        var g:Graphics = this.chargeGraphic.graphics;
        var radius:Number = 10
        g.clear();
        g.lineStyle( 2, 0xff0000, 0 );
        g.beginFill( 0xff0000, 1 );
        g.drawCircle( 0, 0, radius );
        g.endFill();
        //Draw specular highlight
//        g.beginFill( 0xff8888, 1 );
//        g.drawCircle(0.35*radius, -0.35*radius,  0.5*radius );
//        g.endFill();
//        g.beginFill( 0xffffff, 1 );
//        g.drawCircle(0.5*radius, -0.5*radius,  0.2*radius );
//        g.endFill();
        //Draw plus sign
        g.lineStyle( 2, 0xffffff, 1, false,"normal", CapsStyle.NONE );
        var r:Number = radius/2;
        g.moveTo( -r, 0 );
        g.lineTo( r,  0 );
        g.moveTo( 0, -r );
        g.lineTo( 0, r );
        //Draw grab area
        var gGrab: Graphics = this.grabArea.graphics;
        var radius: Number = 100;
        with( gGrab ){
            clear();
            lineStyle( 1, 0xffffff, 0 );
            beginFill( 0x00ff00, 0 );
            drawCircle( 0, 0, radius );
            endFill();

        }
    }//end drawChargeGraphic

    private function setSpring( ):void{
        var g:Graphics = this.springGraphic.graphics;
        g.clear();
        g.lineStyle( 4, 0xff5555, 1 );
        g.moveTo( this.chargeGraphic.x,  this.chargeGraphic.y );
        g.lineTo( this.mouseX,  this.mouseY );
        g.lineStyle( 4, 0xff0000, 1 );
        g.beginFill( 0xff0000, 1 );
        g.drawCircle( mouseX,  mouseY,  5 );
        g.endFill();
        this.delX = this.mouseX - this.chargeGraphic.x;
        this.delY = -( this.mouseY - this.chargeGraphic.y );
        this.myFieldModel.setForce( delX,  delY );
    }

//    private function setSpringGraphic2WithNoForce():void{
//        var g:Graphics = this.springGraphic2.graphics;
//        g.clear();
//        g.lineStyle( 2, 0xff0000, 1 );
//        //g.moveTo( this.chargeGraphic.x,  this.chargeGraphic.y );
//        var L: Number = 50;
//        var r: Number = 6;
//        g.moveTo( this.mouseX,  this.mouseY );
//        g.lineTo( this.mouseX, this.mouseY - L/4 )
//        g.lineTo( this.mouseX - r,  this.mouseY- L/4 - L/16 );
//        g.lineTo( this.mouseX + r,  this.mouseY- L/4 - 3*L/16 );
//        g.lineTo( this.mouseX - r,  this.mouseY- L/4 - 5*L/16 );
//        g.lineTo( this.mouseX + r,  this.mouseY- L/4 - 7*L/16 );
//        g.lineTo( this.mouseX, this.mouseY - 3*L/4 )
//        g.lineTo( this.mouseX,  this.mouseY - L );
//        g.beginFill( 0xff0000, 1 );
//        g.drawCircle( mouseX,  mouseY,  5 );
//        g.endFill();
//    }

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
        //this.chargeGraphic.buttonMode = true;
        //this.springGraphic2.buttonMode = true;
        this.chargeGraphic.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        this.chargeGraphic.addEventListener( MouseEvent.ROLL_OVER, startShowSpring );
        this.chargeGraphic.addEventListener( MouseEvent.ROLL_OUT, stopShowSpring );

        function startShowSpring( evt: MouseEvent ):void{
//            var motion:String = myFieldModel.motionType_str;
//            var manual:String = myFieldModel.manual_str;
//            var noFriction:String = myFieldModel.noFriction_str;
            var motion: int = myFieldModel.motionType;
            var manual: int = myFieldModel.MANUAL_WITH_FRICTION;
            var noFriction: int = myFieldModel.MANUAL_NO_FRICTION;
            if( motion == manual || motion == noFriction ){
                thisObject.chargeGraphic.buttonMode = true;
//                if( !thisObject.grabbed ){
//                    thisObject.setSpringGraphic2WithNoForce();
//                }
                stage.addEventListener( MouseEvent.MOUSE_MOVE, dragShowSpring );
                evt.updateAfterEvent();
            }else{
                thisObject.chargeGraphic.buttonMode = false;
            }
        }

        function dragShowSpring( evt: MouseEvent ):void{
            //trace("dragging")
//            if( !thisObject.grabbed ){
//                thisObject.setSpringGraphic2WithNoForce();
//            }
            evt.updateAfterEvent();
        }

        function  stopShowSpring( evt: MouseEvent ):void{
            //trace("stopShowSpring called");
            //thisObject.springGraphic2.graphics.clear();
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, dragShowSpring );
            evt.updateAfterEvent();
        }


        function startTargetDrag( evt: MouseEvent ): void {
            var motion: int = myFieldModel.motionType;
            var manual: int = myFieldModel.MANUAL_WITH_FRICTION;
            var noFriction: int = myFieldModel.MANUAL_NO_FRICTION;
            if( motion == manual || motion == noFriction ){
                thisObject.chargeGraphic.buttonMode = true;
                thisObject.grabbed = true;
                stopShowSpring( evt );
                thisObject.setSpring();
                thisObject.springTimer.start();
                stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            }else{
                thisObject.chargeGraphic.buttonMode = false;
            }
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            thisObject.grabbed = false;
            thisObject.springTimer.stop();
            thisObject.killSpring();
            evt.updateAfterEvent();
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
        }

        function dragTarget( evt: MouseEvent ): void {
            //do nothing.  Motion is handled by springTimer function
        }//end of dragTarget()


    }//end makeChargeGrabbable

    public function update():void{
        this.chargeGraphic.x = this.stageW/2 + this.myFieldModel.xC;
        this.chargeGraphic.y = this.stageH/2 - this.myFieldModel.yC;
        if( myFieldModel.outOfBounds ){
            myMainView.myControlPanel.resetCharge();
            myFieldModel.outOfBounds = false;
        }
    }

}  //end of class
}  //end of package
