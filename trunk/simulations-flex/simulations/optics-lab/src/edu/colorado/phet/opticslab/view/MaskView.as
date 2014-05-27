/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created with IntelliJ IDEA.
 * User: Dubson
 * Date: 5/27/14
 * Time: 3:38 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.opticslab.view {
import edu.colorado.phet.opticslab.model.LightSource;
import edu.colorado.phet.opticslab.model.Mask;
import edu.colorado.phet.opticslab.model.OpticsModel;

import flash.display.Graphics;

import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.geom.Point;

public class MaskView extends Sprite {
    //private var myMainView: MainView;
    private var myOpticsModel: OpticsModel;
    private var container: LayoutView;      //parent of this sprite
    private var pixPerMeter: Number;
    private var _index:uint;      //index of this optical component, this mask = myOpticsModel.component_arr[index];
    private var _height: Number;  //height of mask in pixels
    private var maskModel: Mask;
    private var maskGraphic: Sprite;

    public function MaskView( opticsModel: OpticsModel, container: LayoutView, index: uint ) {
        myOpticsModel = opticsModel;
        maskModel = myOpticsModel.component_arr[index];
        this.container = container;
        this.pixPerMeter = container.pixPerMeter;
        this._index = index;
        this._height = maskModel.height*pixPerMeter;
        this.maskGraphic = new Sprite();
        myOpticsModel.registerView( this );
        init();
    }//end constructor

    private function init():void{
        drawMask();
        this.addChild( maskGraphic );
        this.container.addChild( this );
        this.makeMaskGrabbable();
    }//end init()

    private function drawMask():void{
        var gM:Graphics = maskGraphic.graphics;
        trace("MaskView.drawMask()  _height in Pix = " + _height );
        with( gM ){
            clear();
            lineStyle( 5, 0xffffff );
            moveTo( 0, 0 );
            lineTo( 0, -_height );
            //draw invisible hit area
            var w: uint = 40;
            var h: uint = _height;
            lineStyle( 1, 0xffffff, 0 );
            beginFill( 0xffffff, 0 );
            drawRect( -w/2, -h,  w,  h );
            endFill();
        }
    }//end drawMask

    private function makeMaskGrabbable(): void {
        //var pixPerMeter: Number = this.container.pixPerMeter;
        var thisObject: Object = this;
        this.maskGraphic.buttonMode = true;
        this.maskGraphic.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        //this.addEventListener( MouseEvent.ROLL_OVER, thisObject.showArrows );
        //this.addEventListener( MouseEvent.ROLL_OUT, thisObject.hideArrows );
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
            var xInPix: Number = thisObject.container.mouseX - clickOffset.x;    //screen coordinates, origin on left edge of stage
            var yInPix: Number = thisObject.container.mouseY - clickOffset.y;    //screen coordinates, origin on left edge of stage
            var xInMeters: Number = xInPix / pixPerMeter;
            var yInMeters: Number = yInPix / pixPerMeter;   //screen coords and cartesian coordinates in sign harmony here
            thisObject.myOpticsModel.component_arr[ _index ].setLocation( xInMeters, yInMeters );
            evt.updateAfterEvent();
        }//end of dragTarget()

    }//end makeSourceGrabbable()

    public function update():void{
        this.x = maskModel.x*pixPerMeter;
        this.y = maskModel.y*pixPerMeter;
    }//end update

} //end class
} //end package
