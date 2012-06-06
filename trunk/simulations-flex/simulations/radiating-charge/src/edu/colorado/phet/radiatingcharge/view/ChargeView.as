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
import flash.geom.Point;

public class ChargeView extends Sprite {

    private var myMainView: MainView;
    private var myFieldModel: FieldModel;
    private var chargeGraphic:Sprite;

    private var stageW: int;
    private var stageH: int;

    public function ChargeView( myMainView:MainView, myFieldModel:FieldModel) {
        this.myMainView = myMainView;
        this.myFieldModel = myFieldModel;
        this.myFieldModel.registerView( this );
        this.chargeGraphic = new Sprite();
        this.initialize();
    }

    private function initialize():void{
        this.stageW = this.myMainView.stageW;
        this.stageH = this.myMainView.stageH;
        this.addChild( this.chargeGraphic );
        this.drawChargeGraphic();
        this.makeChargeGrabbable();

    } //end of initialize

    private function drawChargeGraphic():void{
        var g:Graphics = this.chargeGraphic.graphics;
        g.clear();
        g.lineStyle( 2, 0x000000, 1 );
        g.beginFill( 0x000000, 1 );
        g.drawCircle( stageW/2, stageH/2, 10 );
        g.endFill();
    }//end drawChargeGraphic

    private function makeChargeGrabbable():void{
        var thisObject:Object = this;
        this.chargeGraphic.buttonMode = true;
        this.chargeGraphic.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        var clickOffset: Point;

        function startTargetDrag( evt: MouseEvent ): void {
            clickOffset = new Point( evt.localX, evt.localY );
            stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            clickOffset = null;
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        }

        function dragTarget( evt: MouseEvent ): void {
            var xInPix:Number = thisObject.mouseX - clickOffset.x;    //screen coordinates, origin on left edge of stage
            var yInPix:Number = thisObject.mouseY - clickOffset.y;    //screen coordinates, origin on left edge of stage
            //trace("x: "+xInPix+"    y: "+yInPix);
            thisObject.myFieldModel.setXY( xInPix,  yInPix );
            evt.updateAfterEvent();
        }//end of dragTarget()


    }//end makeChargeGrabbable

    public function update():void{
        this.chargeGraphic.x = this.myFieldModel.xC;
        this.chargeGraphic.y = this.myFieldModel.yC;
    }

}  //end of class
}  //end of package
