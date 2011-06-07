/**
 * Created by IntelliJ IDEA.
 * User: General User
 * Date: 6/4/11
 * Time: 12:31 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes {
import flash.display.Graphics;
import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.geom.Point;

public class MassView extends Sprite{
    private var _index:int;   //integer labeling the mass
    private var myModel:Model;
    private var container:View;

    public function MassView( index:int, myModel:Model, container:View ) {
        this._index = index;
        this.myModel = myModel
        this.container = container;
        this.drawMass();
        this.makeMassGrabbable();
    } //end constructor

    private function drawMass():void{
        var nMax:int = this.myModel.nMax;   //maximum number of mobile masses
        for(var i:int = 0; i < nMax; i++){
            var g:Graphics = this.graphics;
            g.clear();
            g.lineStyle(3, 0x0000ff, 1);
            var d:Number = 20;   //edge length of square mass in pixels
            g.beginFill(0x3333ff, 1);
            g.drawRoundRect(-d/2, -d/2, d,  d,  d/4 );
            g.endFill();
            //this.mass_arr[i].y = this.leftEdgeY;
            this.visible = false;      //start with mass invisible
        }
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
            thisObject.myModel.grabbedMass = thisObject._index;
            clickOffset = new Point( evt.localX, evt.localY );
                //wasRunning = thisObject.model.getRunning();
                //thisObject.model.stopShaker();
             stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
             stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
            //problem with localX, localY if sprite is rotated.
            //trace("evt.target.y: "+evt.target.y);
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            thisObject.myModel.grabbedMass = 0;
            clickOffset = null;
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
            //if ( wasRunning ) {thisObject.model.startShaker();}
        }

        function dragTarget( evt: MouseEvent ): void {
            var xInPix:Number = thisObject.container.mouseX - clickOffset.x;    //screen coordinates, origin on left edge of stage
            var yInPix:Number = thisObject.container.mouseY - clickOffset.y;    //screen coordinates, origin on left edge of stage
            if(thisObject.myModel.longitudinalMode){
                var xInMeters:Number = (xInPix - leftEdgeX) / pixPerMeter;
                thisObject.myModel.setX( thisObject.index,  xInMeters );
            }else{
                var yInMeters:Number = -(yInPix - leftEdgeY) / pixPerMeter;   //screen coords vs. cartesian coordinates, hence the minus sign
                thisObject.myModel.setY( thisObject.index,  yInMeters );
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
