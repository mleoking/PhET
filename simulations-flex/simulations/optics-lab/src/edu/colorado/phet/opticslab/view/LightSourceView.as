/**
 * Created by Dubson on 5/24/2014.
 * View of LightSource
 */
package edu.colorado.phet.opticslab.view {
import edu.colorado.phet.opticslab.model.LightSource;
import edu.colorado.phet.opticslab.model.OpticsModel;
import edu.colorado.phet.opticslab.util.Util;

import flash.display.Graphics;

import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.geom.Point;

public class LightSourceView extends Sprite {
    //private var myMainView: MainView;
    private var myOpticsModel: OpticsModel;
    //private var mySourceModel: LightSource;
    private var sourceHolder: Sprite;       //like a flashlight, holds a bunch of rays
    private var raysGraphic: Sprite;        //graphic of rays
    private var container: LayoutView;      //parent of this sprite
    private var pixPerMeter: Number;
    private var isOn: Boolean;      //True if light source is emitting rays. Source not on until dropped onto play area
    private var sourceNbr:uint;      //index of source, this source = myOpticsModel.source_arr[sourceNbr];
    private var sourceModel: LightSource;

    public function LightSourceView( opticsModel: OpticsModel, container: LayoutView, index: uint ) {
        //myMainView = mainView;
        myOpticsModel = opticsModel;
        sourceModel = myOpticsModel.source_arr[index];
        this.container = container;
        this.pixPerMeter = container.pixPerMeter;
        this.sourceNbr = index;
        this.sourceHolder = new Sprite();
        this.raysGraphic = new Sprite();
        myOpticsModel.registerView( this );
        init();
    }//end constructor

    private function init():void{
        //this.sourceHolder = new Sprite();
        drawSourceHolder();
        this.addChild( sourceHolder );
        drawRays();
        this.addChild( raysGraphic );
        this.container.addChild( this );
        this.makeSourceGrabbable();
        this.isOn = false;


    }//end init()

    private function drawSourceHolder():void{
        var g: Graphics = sourceHolder.graphics;
        g.clear();
        g.lineStyle( 5, 0xffffff );
        g.beginFill( 0xff00ff,0 );
        //g.moveTo( 0, 0 );
        //g.lineTo( 50, 50 )
        g.drawCircle( 0, 0, 20 );
        g.endFill();
    }//end drawSourceHolder;

    private function drawRays():void{
        var gR: Graphics = raysGraphic.graphics;
        var N:uint = sourceModel.nbrRays;
        var ray_arr:Array = sourceModel.ray_arr;
        with ( gR ){
            clear();
            lineStyle( 1, 0xffffff );
            for( var j:uint = 0; j < N; j++ ){
                var length: Number = ray_arr[j].length*pixPerMeter;
                var angle: Number = ray_arr[j].angle;
                moveTo( 0, 0 );
                lineTo( length*Math.cos(angle), length*Math.sin(angle) )
            }
        }
    }//end drawRays()
    


    private function makeSourceGrabbable(): void {
        //var pixPerMeter: Number = this.container.pixPerMeter;
        var thisObject: Object = this;
        this.sourceHolder.buttonMode = true;
        this.sourceHolder.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
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
            thisObject.myOpticsModel.source_arr[ sourceNbr ].setLocation( xInMeters, yInMeters );
            evt.updateAfterEvent();
        }//end of dragTarget()

    }//end makeSourceGrabbable()

    public function update():void{
        //this.x = myOpticsModel.getXYOfSource( sourceNbr ).x*pixPerMeter;
        //this.y = myOpticsModel.getXYOfSource( sourceNbr ).y*pixPerMeter;
        //this.x = myOpticsModel.source_arr[ sourceNbr ].x*pixPerMeter;
        //this.y = myOpticsModel.source_arr[ sourceNbr ].y*pixPerMeter;
        this.x = sourceModel.x*pixPerMeter;
        this.y = sourceModel.y*pixPerMeter;
    }//end update
}//end class
}//end package

//Error:[optics-lab]: command line: unable to open 'C:\Users\Duso\.IntelliJIdea13\system\compile-server\_temp_\IntelliJ_IDEA\idea-666F1D32-A52FB50C.xml'
