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
import edu.colorado.phet.normalmodes.util.TwoHeadedArrow;

import flash.display.Graphics;
import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.geom.Point;
import flash.text.TextField;
import flash.text.TextFormat;

/*
 * MassView2 is the view for an individual mass in View2, which is view for Model2, a 2D array of coupled masses
 * When mouse is inside border zone surrounding mass, vertical or horizontal arrows appear,
 * cuing user that mass is grabbable.  All arrows disappear after user grabs any mass.
 * */
public class MassView2 extends Sprite{
    private var _iJIndices:Array = new Array(2);       // i, j integers labeling the mass: i = row = 1, 2, .. N, j = column = 1, 2, .. N
    private var myModel2:Model2;
    private var container:View2;
    private var mass:Sprite;          //graphic for mass
    private var borderZone:Sprite;    //when user mouses over borderZone, arrows appear around mass
    private var arrows:Sprite;        //arrows cue user that mass is grabbable
    private var label_txt:TextField;
    private var tFormat:TextFormat;

    public function MassView2( i:int, j:int,  myModel2:Model2, container:View2 ) {
        this._iJIndices[0] = i;
        this._iJIndices[1] = j;
        this.myModel2 = myModel2;
        this.container = container;
        this.mass = new Sprite();
        this.borderZone = new Sprite();
        this.arrows = new Sprite();
        this.addChild( borderZone );
        this.addChild( arrows );
        this.addChild( mass );
        this.drawMass();
        this.drawArrows();
        this.drawBorderZone( 200, 200 );
        this.label_txt = new TextField();
        this.tFormat = new TextFormat();
        this.makeMassGrabbable();
    } //end constructor


 /*
 * Invisible border zone. When zone is moused over, arrows appear, cueing user that mass is grabbable
 * */
    public function drawBorderZone( width:Number,  height:Number ):void{
        var w:Number = width;
        var h:Number = height;
        var g:Graphics = this.borderZone.graphics;
        g.clear();
        g.lineStyle(3, 0xffffff, 0);
        g.beginFill(0xffffff, 0);             //border zone is invisible, alpha = 0
        g.drawRoundRect(-w/2, -h/2, w,  h,  w/4 );
        g.endFill();
    }

    public function drawArrows():void{
        var arrows1:TwoHeadedArrow = new TwoHeadedArrow();
        var arrows2:TwoHeadedArrow = new TwoHeadedArrow();
        arrows1.setArrowDimensions( 10, 20, 4, 50 );
        arrows2.setArrowDimensions( 10, 20, 4, 50 );
        arrows1.setRegistrationPointAtCenter( true );
        arrows2.setRegistrationPointAtCenter( true );
        arrows1.rotation = 45;
        arrows2.rotation = -45;
        this.arrows.addChild( arrows1 );
        this.arrows.addChild( arrows2 );
        this.arrows.visible = false;
    } //end drawArrows()

    private function drawMass():void{
        var g:Graphics = this.mass.graphics;
        g.clear();
        g.lineStyle(3, 0x0000cc, 1);
        var d:Number = 20;   //edge length of square mass in pixels
        g.beginFill(0x2222ff, 1);
        g.drawCircle(0, 0, d/2);
        g.endFill();
        this.mass.visible = true;      //mass always visible
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

    private function makeMassGrabbable(): void {
        var leftEdgeX:Number = this.container.topLeftCornerX;
        var topEdgeY:Number = this.container.topLeftCornerY;
        var pixPerMeter:Number = this.container.pixPerMeter;
        var thisObject:Object = this;
        this.mass.buttonMode = true;
        this.mass.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        this.addEventListener( MouseEvent.ROLL_OVER, thisObject.showArrows );
        this.addEventListener( MouseEvent.ROLL_OUT, thisObject.hideArrows );
        var clickOffset: Point;

        function startTargetDrag( evt: MouseEvent ): void {
            thisObject.arrows.visible = false;
            thisObject.myModel2.grabbedMassIndices = thisObject._iJIndices;
            thisObject.myModel2.verletOn = true;
            clickOffset = new Point( evt.localX, evt.localY );
            stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
            thisObject.container.clearBorderZones();// killArrowListeners();
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            thisObject.myModel2.grabbedMassIndices = [0, 0];     //convention for when masses in not grabbed
            thisObject.myModel2.verletOn = false;
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
            evt.updateAfterEvent();
        }//end of dragTarget()

    }//end makeMassGrabbable()

    private function showArrows( evt: MouseEvent ):void {
        this.arrows.visible = true;
    }

    private function hideArrows( evt: MouseEvent ):void{
        this.arrows.visible = false;
    }

    //Called by View2 when any mass is grabbed
    public function killArrowListeners():void{
        this.removeEventListener( MouseEvent.ROLL_OVER, this.showArrows );
        this.removeEventListener( MouseEvent.ROLL_OUT, this.hideArrows );
        this.drawBorderZone(0,0);  //eliminate border zone
    }

} //end class
} //end package
