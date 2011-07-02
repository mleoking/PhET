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

import flash.display.Graphics;
import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.geom.Point;

public class MassView1 extends Sprite{
    private var _index:int;   //integer labeling the mass
    private var myModel1:Model1;
    private var container:View1;

    public function MassView1( index:int, myModel1:Model1, container:View1 ) {
        this._index = index;
        this.myModel1 = myModel1
        this.container = container;
        this.drawMass();
        this.makeMassGrabbable();
    } //end constructor

    private function drawMass():void{
        var g:Graphics = this.graphics;
        g.clear();
        g.lineStyle(3, 0x0000ff, 1);
        var d:Number = 20;   //edge length of square mass in pixels
        g.beginFill(0x3333ff, 1);
        g.drawRoundRect(-d/2, -d/2, d,  d,  d/4 );
        g.endFill();
        //this.mass_arr[i].y = this.leftEdgeY;
        this.visible = false;      //start with mass invisible
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
        this.buttonMode = true;
        this.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        var clickOffset: Point;

        function startTargetDrag( evt: MouseEvent ): void {
            thisObject.myModel1.grabbedMass = thisObject._index;
            clickOffset = new Point( evt.localX, evt.localY );
             stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
             stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
            //trace("evt.target.y: "+evt.target.y);
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            thisObject.myModel1.grabbedMass = 0;
            //thisObject.myModel1.computeModeAmplitudesAndPhases();
            //thisObject.myModel1.justReleased = true;
            thisObject.myModel1.computeModeAmplitudesAndPhases();
            thisObject.myModel1.nbrStepsSinceRelease = 0;
            clickOffset = null;
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        }

        function dragTarget( evt: MouseEvent ): void {
            var xInPix:Number = thisObject.container.mouseX - clickOffset.x;    //screen coordinates, origin on left edge of stage
            var yInPix:Number = thisObject.container.mouseY - clickOffset.y;    //screen coordinates, origin on left edge of stage
            if(thisObject.myModel1.longitudinalMode){
                var xInMeters:Number = (xInPix - leftEdgeX) / pixPerMeter;
                thisObject.myModel1.setX( thisObject.index,  xInMeters );
            }else{
                var yInMeters:Number = -(yInPix - leftEdgeY) / pixPerMeter;   //screen coords vs. cartesian coordinates, hence the minus sign
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

    }
} //end class
} //end package
