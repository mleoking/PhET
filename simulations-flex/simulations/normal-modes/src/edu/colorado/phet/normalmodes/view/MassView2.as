/**
 * Created by IntelliJ IDEA.
 * User: General User
 * Date: 6/12/11
 * Time: 9:08 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes.view {
import edu.colorado.phet.normalmodes.*;
import edu.colorado.phet.normalmodes.model.Model2;

import flash.display.Graphics;
import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.geom.Point;
import flash.text.TextField;
import flash.text.TextFormat;

//view for mass in 2D array of masses and springs
public class MassView2 extends Sprite{
    private var _iJIndices:Array = new Array(2);       // i, j integers labeling the mass :i = row = 1, 2, .. N, j = column = 1, 2, .. N
    private var myModel2:Model2;
    private var container:View2;
    private var label_txt:TextField;
    private var tFormat:TextFormat;

    public function MassView2( i:int, j:int,  myModel2:Model2, container:View2 ) {
        this._iJIndices[0] = i;
        this._iJIndices[1] = j;
        this.myModel2 = myModel2
        this.container = container;
        this.label_txt = new TextField();
        this.tFormat = new TextFormat();
        this.drawMass();
        //this.makeLabel();   //for testing only
        this.makeMassGrabbable();
    } //end constructor

    private function drawMass():void{
        var g:Graphics = this.graphics;
        g.clear();
        g.lineStyle(3, 0x0000cc, 1);
        var d:Number = 20;   //edge length of square mass in pixels
        g.beginFill(0x2222ff, 1);
        //g.drawRoundRect(-d/2, -d/2, d,  d,  d/4 );
        //g.drawCircle(-d/2, -d/2, d/2)
        g.drawCircle(0, 0, d/2)
        g.endFill();
        this.visible = false;      //start with mass invisible
    }//end drawMass()

    //For testing only. Label shows indices on mass graphic
    private function makeLabel():void{
        var txt1_str:String = this._iJIndices[0].toString();
        var txt2_str:String = this._iJIndices[1].toString();
        this.label_txt.text = txt1_str + "," + txt2_str;
        this.tFormat.font = "Arial";
        this.tFormat.size = 14;
        this.label_txt.setTextFormat(this.tFormat);
        this.label_txt.y +=10;
        this.addChild(this.label_txt);
    }

    public function get iJIndices():Array{
        return this._iJIndices;
    }

    private function makeMassGrabbable(): void {
        var leftEdgeX:Number = this.container.topLeftCornerX;
        var topEdgeY:Number = this.container.topLeftCornerY;
        var pixPerMeter:Number = this.container.pixPerMeter;
        var thisObject:Object = this;
        this.buttonMode = true;
        this.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        var clickOffset: Point;

        function startTargetDrag( evt: MouseEvent ): void {
            thisObject.myModel2.grabbedMassIndices = thisObject._iJIndices;
            thisObject.myModel2.verletOn = true;
            clickOffset = new Point( evt.localX, evt.localY );
            stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
            //trace("MassView2.startTargetDrag.  i = " + thisObject._iJIndices[0] + "    j = "+thisObject._iJIndices[1]);
            //trace("evt.target.y: "+evt.target.y);
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            thisObject.myModel2.grabbedMassIndices = [0, 0];     //convention for when masses in not grabbed
            //thisObject.myModel2.computeModeAmplitudesAndPhases();
            //thisObject.myModel2.justReleased = true;
            //thisObject.myModel2.nbrStepsSinceRelease = 0;
            thisObject.myModel2.verletOn = false;
            //thisObject.myModel2.t = 0;
            thisObject.myModel2.computeModeAmplitudesAndPhases();
            clickOffset = null;
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        }

        function dragTarget( evt: MouseEvent ): void {
            var xInPix:Number = thisObject.container.mouseX - clickOffset.x;    //screen coordinates, origin on left edge of stage
            var yInPix:Number = thisObject.container.mouseY - clickOffset.y;    //screen coordinates, origin on left edge of stage
            var xInMeters:Number = (xInPix - leftEdgeX) / pixPerMeter;
            var yInMeters:Number = (yInPix - topEdgeY) / pixPerMeter;   //screen coords and cartesian coordinates in sign harmony here
            var i:int = thisObject._iJIndices[0];
            var j:int = thisObject._iJIndices[1];
            thisObject.myModel2.setXY( i,  j, xInMeters, yInMeters );
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
}
