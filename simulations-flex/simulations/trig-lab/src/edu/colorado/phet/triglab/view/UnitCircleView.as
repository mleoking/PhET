/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/6/12
 * Time: 8:40 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.triglab.view {
import edu.colorado.phet.triglab.model.TrigModel;
import edu.colorado.phet.triglab.util.Util;

import flash.display.CapsStyle;

import flash.display.Graphics;
import flash.display.MovieClip;
import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.events.TimerEvent;
import flash.events.TimerEvent;
import flash.geom.Point;
import flash.utils.Timer;

public class UnitCircleView extends Sprite {

    private var myMainView: MainView;
    private var myTrigModel: TrigModel;

    private var unitCircleGraph: Sprite ; //unit circle centered on xy axes
    private var triangleDiagram: Sprite;  //triangle drawn on unit circle, the ratio of the sides are the trig functions
    private var gridLines: Sprite ;       //optional gridlines on unit circle
    private var angleHandle: Sprite;//grabbable handle for setting angle on unit Circle
    private var radius:Number;      //radius of unit circle in pixels
    private var previousAngle:Number;
    private var smallAngle:Number
    private var totalAngle:Number;
    private var nbrHalfTurns:Number;

    private var grabbed: Boolean;   //true if angle selection ball is grabbed

    private var stageW: int;
    private var stageH: int;



    public function UnitCircleView( myMainView:MainView, myTrigModel:TrigModel) {
        this.myMainView = myMainView;
        this.myTrigModel = myTrigModel;
        this.myTrigModel.registerView( this );
        this.unitCircleGraph = new Sprite();
        this.triangleDiagram = new Sprite();
        this.gridLines = new Sprite();
        this.angleHandle = new Sprite();
        this.grabbed = false;
        this.initialize();
    }

    private function initialize():void{
        this.stageW = this.myMainView.stageW;
        this.stageH = this.myMainView.stageH;
        this.radius = 200;
        this.addChild( gridLines );
        this.addChild( unitCircleGraph );
        this.unitCircleGraph.addChild( triangleDiagram );
        this.unitCircleGraph.addChild( this.angleHandle );
        this.drawUnitCircle();
//        this.unitCircleGraph.x = 0.3*stageW;
//        this.unitCircleGraph.y = 0.3*stageH;
//        this.gridLines.x = this.unitCircleGraph.x;
//        this.gridLines.y = this.unitCircleGraph.y;
        this.drawGridLines();
        this.drawAngleHandle();
        this.makeAngleHandleGrabbable();
        this.smallAngle = 0;
        this.previousAngle = 0;
        this.totalAngle = 0;
        this.nbrHalfTurns = 0;
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
            //draw arrow heads on axes
            var length:Number = 10;
            var halfWidth:Number = 6
            //x-axis arrow
            moveTo( f*radius, 0 );
            lineTo( f*radius - length,  halfWidth );
            moveTo( f*radius, 0 );
            lineTo( f*radius - length,  -halfWidth );
            //y-axis arrow
            moveTo( 0, -f*radius);
            lineTo( -halfWidth, -f*radius + length );
            moveTo( 0, -f*radius );
            lineTo( +halfWidth, -f*radius + length );
            //draw unit circle
            lineStyle( 2, 0x0000ff, 1 );  //blue
            drawCircle( 0, 0, radius );
        }
    }  //end drawUnitCircle()

    private function drawGridLines():void{
        //grid spacing = 0.5
        var spacing: Number = 0.5*this.radius;
        var gGrid: Graphics = this.gridLines.graphics;
        gGrid.lineStyle( 2, 0xaaaaaa, 1 ); //light gray color
        for( var i:int = -2; i <= 2; i++) {
            with(gGrid){
                //draw horizontal lines
                moveTo(-2*spacing,  i*spacing );
                lineTo(+2*spacing,  i*spacing );
                //draw vertical lines
                moveTo( i*spacing, -2*spacing );
                lineTo( i*spacing, +2*spacing );
            }//end with
        }//end for
    }//end drawGridLines

    private function drawTriangle():void{
        var gTriangle: Graphics = this.triangleDiagram.graphics;
        var xLeg: Number = this.radius*myTrigModel.cos;
        var yLeg: Number = -this.radius*myTrigModel.sin;
        with( gTriangle ){
            clear();
            //draw hypotenuse
            lineStyle( 1, 0xff0000, 1 );
            moveTo( 0, 0 );
            lineTo( xLeg, yLeg );
            //draw x-leg
            lineStyle( 3, Util.COSCOLOR,  1) ;
            moveTo( 0, 0 );
            lineTo( xLeg,  0 );
            //draw y-leg
            lineStyle( 3, Util.SINCOLOR,  1) ;
            moveTo( xLeg, 0 );
            lineTo( xLeg,  yLeg );
        }
    }//end drawTriangle

    private function drawAngleHandle():void{
        var gBall: Graphics = this.angleHandle.graphics;
        var ballRadius: Number = 5;
        with( gBall ){
            clear();
            lineStyle( 1, 0x0000ff, 1.0 )
            beginFill( 0xff0000, 1.0 )    //ball is red
            drawCircle( 0, 0, ballRadius );
            endFill();

        }
        var grabArea: Sprite = new Sprite();
        this.angleHandle.addChild( grabArea );
        //draw invisible grab area
        var gGrab: Graphics = grabArea.graphics;
        var grabRadius: Number = 50;
        with( gGrab ){
            clear();
            lineStyle( 1, 0xffffff, 0 );
            beginFill( 0x00ff00,0 );
            drawCircle( 0, 0, grabRadius );
            endFill();
        }
    }//end drawAngleHandle();



    private function makeAngleHandleGrabbable():void{
        var thisObject:Object = this;
        var clickOffset: Point;
        this.angleHandle.buttonMode = true;
        this.angleHandle.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );

        function startTargetDrag( evt: MouseEvent ): void {
            thisObject.grabbed = true;
            clickOffset = new Point( evt.localX, evt.localY );
            stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
            stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
        }


        function stopTargetDrag( evt: MouseEvent ): void {
            thisObject.grabbed = false;
            clickOffset = null;
            evt.updateAfterEvent();
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
        }

        function dragTarget( evt: MouseEvent ): void {
            var xInPix: Number = thisObject.unitCircleGraph.mouseX - clickOffset.x;
            var yInPix: Number = thisObject.unitCircleGraph.mouseY - clickOffset.y;
            var angleInRads: Number = Math.atan2( yInPix,  xInPix );
            thisObject.smallAngle = -angleInRads;   //minus-sign to be consistent with convention: CCW = +angle, CW = -angle
            thisObject.myTrigModel.smallAngle = -angleInRads;
            evt.updateAfterEvent();
        }//end of dragTarget()


    }//end makeAngleSelectorGrabbable



    private function positionAngleHandle( angleInRads: Number ):void{
        this.angleHandle.x = this.radius*Math.cos( angleInRads );
        this.angleHandle.y = -this.radius*Math.sin( angleInRads );
    }

    public function update():void{
        this.positionAngleHandle( myTrigModel.smallAngle );
        //this.updateTotalAngle();
        this.drawTriangle();
    }

}  //end of class
}  //end of package
