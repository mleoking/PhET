/**
 * Created by IntelliJ IDEA.
 * User: General User
 * Date: 6/4/11
 * Time: 12:31 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes.view {
import edu.colorado.phet.normalmodes.*;
import edu.colorado.phet.normalmodes.model.Model1;
import edu.colorado.phet.normalmodes.util.TwoHeadedArrow;

import flash.display.Graphics;
import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.geom.Point;
import flash.sampler.Sample;

public class MassView1 extends Sprite{
    private var _index:int;   //integer labeling the mass
    private var myModel1:Model1;
    private var container:View1;
    private var mass:Sprite;
    private var borderZone:Sprite;    //when user mouses over borderZone, arrows appear around mass
    private var arrows:Sprite;        //arrows cue user that mass is grabbable



    public function MassView1( index:int, myModel1:Model1, container:View1 ) {
        this._index = index;
        this.myModel1 = myModel1
        this.container = container;
        this.mass = new Sprite();
        this.borderZone = new Sprite();
        this.arrows = new Sprite();
        this.addChild( borderZone );
        this.addChild( arrows );
        this.addChild( mass );
        this.drawBorderZone( 150, 200 );
        this.drawArrows();
        this.drawMass( "dim" );
        this.makeMassGrabbable();
    } //end constructor

    public function drawBorderZone( width:Number,  height:Number ):void{
        var w:Number = width;
        var h:Number = height;
        var g:Graphics = this.borderZone.graphics;
        g.clear();
        g.lineStyle(3, 0xffffff, 0);
        //var d:Number = 80;   //edge length of square mass in pixels
        g.beginFill(0xffffff, 0);
        g.drawRoundRect(-w/2, -h/2, w,  h,  w/4 );
        g.endFill();
    }

    public function drawArrows():void{
        var arrows1:TwoHeadedArrow = new TwoHeadedArrow();
        var arrows2:TwoHeadedArrow = new TwoHeadedArrow();
        arrows1.setRegistrationPointAtCenter( true );
        arrows2.setRegistrationPointAtCenter( true );
        arrows1.scaleX = 2;
        arrows2.scaleX = 2;
        arrows1.rotation = 45;
        arrows2.rotation = -45;
        this.arrows.addChild( arrows1 );
        this.arrows.addChild( arrows2 );
        this.arrows.visible = false;
    } //end drawArrows()

    private function drawMass( dimOrBright:String ):void{
        var g:Graphics = this.mass.graphics;
        g.clear();

        var d:Number = 20;   //edge length of square mass in pixels
        g.lineStyle(3, 0x0000ff, 1);
        g.beginFill(0x6666ff, 1);
        g.drawRoundRect(-d/2, -d/2, d,  d,  d/4 );
        g.endFill();
        this.mass.visible = true;      //start with mass invisible
        //this.mass_arr[i].y = this.leftEdgeY;

    }//end drawMass()

    public function get index():int{
        return this._index;
    }

    private function makeMassGrabbable(): void {
        //var target = mySprite;
        //var wasRunning: Boolean;
        var leftEdgeX:Number = this.container.leftEdgeX;
        var leftEdgeY:Number = this.container.leftEdgeY;
        var pixPerMeter:Number = this.container.pixPerMeter;
        var thisObject:Object = this;
        this.mass.buttonMode = true;
        this.mass.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        //this.borderZone.addEventListener( MouseEvent.ROLL_OVER, showArrows );
        //this.borderZone.addEventListener( MouseEvent.ROLL_OUT, removeArrows );
        this.addEventListener( MouseEvent.ROLL_OVER, thisObject.showArrows );
        this.addEventListener( MouseEvent.ROLL_OUT, thisObject.removeArrows );
        //this.mass.addEventListener( MouseEvent.MOUSE_OVER, showArrows );
        var clickOffset: Point;

        function startTargetDrag( evt: MouseEvent ): void {
            thisObject.arrows.visible = false;
            thisObject.myModel1.grabbedMass = thisObject._index;
            clickOffset = new Point( evt.localX, evt.localY );
            stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
            thisObject.killArrowListeners();
            //thisObject.removeEventListener( MouseEvent.ROLL_OVER, thisObject.showArrows );
            //thisObject.removeEventListener( MouseEvent.ROLL_OUT, thisObject.removeArrows );
            //thisObject.mass.removeEventListener( MouseEvent.MOUSE_OVER, showArrows );
            //trace("evt.target.y: "+evt.target.y);
        }


        function stopTargetDrag( evt: MouseEvent ): void {
            thisObject.myModel1.grabbedMass = 0;
            //thisObject.myModel1.computeModeAmplitudesAndPhases();
            //thisObject.myModel1.justReleased = true;
            thisObject.myModel1.computeModeAmplitudesAndPhases();
            //thisObject.myModel1.nbrStepsSinceRelease = 0;
            clickOffset = null;
            thisObject.container.clearBorderZones();
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        }

        function dragTarget( evt: MouseEvent ): void {
            var xInPix:Number = thisObject.container.mouseX - clickOffset.x;    //screen coordinates, origin on left edge of stage
            var yInPix:Number = thisObject.container.mouseY - clickOffset.y;    //screen coordinates, origin on left edge of stage
            if(thisObject.myModel1.xModes){
                var xInMeters:Number = (xInPix - leftEdgeX) / pixPerMeter;
                thisObject.myModel1.setX( thisObject.index,  xInMeters );
            }else{
                var yInMeters:Number = -(yInPix - leftEdgeY) / pixPerMeter;    //screen coords vs. cartesian coordinates, hence the minus sign
                thisObject.myModel1.setY( thisObject.index,  yInMeters );
            }

            //xInMeters is physical coordinate in meters, origin at left wall

            var limit: Number = 100;   //max range in pixels
//            if ( xInMeters > limit ) {  x
//                //trace("bar high");
//                xInMeters = limit;
//            } else if ( xInMeters < -limit ) {
//                xInMeters = -limit;
//                //trace("bar low");
//            }

            //trace("xInMeters = "+xInMeters);
            //trace("xInPix = "+xInPix);
            evt.updateAfterEvent();
        }//end of dragTarget()

    } //end makeMassGrabble

    private function showArrows( evt: MouseEvent ):void {
        this.arrows.visible = true;
        //stage.addEventListener ( MouseEvent.MOUSE_OUT, removeArrows );
    }

    private function removeArrows( evt: MouseEvent ):void{
        this.arrows.visible = false;
        //stage.removeEventListener( MouseEvent.MOUSE_OVER, showArrows );
        //stage.removeEventListener( MouseEvent.MOUSE_OUT, removeArrows );
    }

    public function killArrowListeners():void{
        this.removeEventListener( MouseEvent.ROLL_OVER, this.showArrows );
        this.removeEventListener( MouseEvent.ROLL_OUT, this.removeArrows );
    }
} //end class
} //end package
