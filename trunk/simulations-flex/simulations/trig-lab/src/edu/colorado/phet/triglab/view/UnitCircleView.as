/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/6/12
 * Time: 8:40 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.triglab.view {
import edu.colorado.phet.triglab.model.TrigModel;

import flash.display.CapsStyle;

import flash.display.Graphics;
import flash.display.MovieClip;
import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.events.TimerEvent;
import flash.events.TimerEvent;
import flash.utils.Timer;

public class UnitCircleView extends Sprite {

    private var myMainView: MainView;
    private var myTrigModel: TrigModel;

    private var unitCircleGraph = new Sprite(); //unit circle centered on xy axes
    private var gridLines = new Sprite();       //optional gridlines on unit circle
    private var angleHandle: Sprite;//grabbable handle for setting angle on unit Circle
    private var radius:Number;      //radius of unit circle in pixels
    private var grabbed: Boolean;   //true if angle selection ball is grabbed

    private var stageW: int;
    private var stageH: int;



    public function UnitCircleView( myMainView:MainView, myTrigModel:TrigModel) {
        this.myMainView = myMainView;
        this.myTrigModel = myTrigModel;
        this.myTrigModel.registerView( this );
        this.unitCircleGraph = new Sprite();
        this.gridLines = new Sprite();
        this.angleHandle = new Sprite();
        this.grabbed = false;
        this.initialize();
    }

    private function initialize():void{
        this.stageW = this.myMainView.stageW;
        this.stageH = this.myMainView.stageH;
        this.radius = 100;
        this.addChild( unitCircleGraph );
        this.unitCircleGraph.addChild( gridLines );
        this.unitCircleGraph.addChild( this.angleHandle );

        this.drawAngleHandle();

        this.makeAngleHandleGrabbable();
        this.update();

    } //end of initialize


    private function drawUnitCircle():void{
        var g:Graphics = this.unitCircleGraph.graphics;
        var f: Number = 1.3;     //extent of each axis is -f*radius to +f*radius
        with ( g ){
            clear();
            lineStyle( 2, 0x000000, 1 );  //black
            //draw xy axes
            moveTo( -f*radius,  0 );
            lineTo( +f*radius,  0 );
            moveTo( 0, -f*radius );
            lineTo( 0, f*radius );
            //draw unit circle
            lineStyle( 2, 0x0000ff, 1 );  //blue
            drawCircle( 0, 0, radius );
        }
    }  //end drawUnitCircle()

    private function drawAngleHandle():void{
        var gBall: Graphics = this.angleHandle.graphics;
        var ballRadius: Number = 10;
        with( gBall ){
            clear();
            lineStyle( 1, 0x0000ff, 1.0 )
            beginFill( 0xff0000, 1.0 )    //ball is red
            drawCircle( 0, 0, ballRadius );
            endFill();

        }
        var grabArea = new Sprite();
        this.angleHandle.addChild( grabArea );
        //draw invisible grab area
        var gGrab: Graphics = grabArea.graphics;
        var radius: Number = 100;
        with( gGrab ){
            clear();
            lineStyle( 1, 0xffffff, 0 );
            beginFill( 0x00ff00,.5 );
            drawCircle( 0, 0, radius );
            endFill();
        }
    }//end drawAngleHandle();



    private function makeAngleHandleGrabbable():void{
        var thisObject:Object = this;
        this.angleHandle.buttonMode = true;
        this.angleHandle.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        //this.angleHandle.addEventListener( MouseEvent.ROLL_OVER, startShowSpring );
        //this.angleHandle.addEventListener( MouseEvent.ROLL_OUT, stopShowSpring );

        function startTargetDrag( evt: MouseEvent ): void {
            thisObject.grabbed = true;
            stage.addEventListener( MouseEvent.MOUSE_MOVE, moveTarget );
            stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
        }

        function moveTarget( evt: MouseEvent ):void{

        }

        function stopTargetDrag( evt: MouseEvent ): void {
            thisObject.grabbed = false;
            evt.updateAfterEvent();
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
        }

        function dragTarget( evt: MouseEvent ): void {
            //do nothing.  Motion is handled by springTimer function
        }//end of dragTarget()


    }//end makeAngleSelectorGrabbable

    private function positionAngleHandle( angleInRads: Number ):void{
        this.angleHandle.x = this.radius*Math.cos( angleInRads );
        this.angleHandle.y = this.radius*Math.sin( angleInRads );
    }

    public function update():void{
        this.positionAngleHandle( myTrigModel.theta );
    }

}  //end of class
}  //end of package
