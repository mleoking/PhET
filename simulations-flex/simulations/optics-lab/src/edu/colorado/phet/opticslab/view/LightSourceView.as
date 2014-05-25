/**
 * Created by Dubson on 5/24/2014.
 * View of LightSource
 */
package edu.colorado.phet.opticslab.view {
import edu.colorado.phet.opticslab.model.OpticsModel;
import edu.colorado.phet.opticslab.util.Util;

import flash.display.Graphics;

import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.geom.Point;

public class LightSourceView extends Sprite {
    //private var myMainView: MainView;
    private var myOpticsModel: OpticsModel;
    private var sourceHolder: Sprite;       //like a flashlight, holds a bunch of rays
    private var container: LayoutView;      //parent of this sprite
    private var sourceNbr:int;      //index of source, this source = myOpticsModel.source_arr[sourceNbr];

    public function LightSourceView( opticsModel: OpticsModel, container: LayoutView ) {
        //myMainView = mainView;
        myOpticsModel = opticsModel;
        this.container = container;
        this.sourceHolder = new Sprite();
        myOpticsModel.registerView( this );
        init();
    }//end constructor

    private function init():void{
        //this.sourceHolder = new Sprite();
        drawSourceHolder();
        this.addChild( sourceHolder );
        this.container.addChild( this );
        this.makeSourceGrabbable();


    }//end init()

    private function drawSourceHolder():void{
        var g: Graphics = sourceHolder.graphics;
        g.clear();
        g.lineStyle( 5, 0xffffff );
        g.beginFill( 0xff00ff );
        //g.moveTo( 0, 0 );
        //g.lineTo( 50, 50 )
        g.drawCircle( 0, 0, 20 );
        g.endFill();


    }//end drawSourceHolder;
    
    public function makeGrabbable( ):void{
        //Util.makeClipDraggable( this );   //fails!  Why?
        this.buttonMode = true;
        var thisThing = this;
        var clickOffset: Point;

        thisThing.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        thisThing.parent.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        thisThing.parent.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );

        function startTargetDrag( evt: MouseEvent ):void{
            clickOffset = new Point( evt.localX, evt.localY );
        }
        function dragTarget( evt: MouseEvent ):void{
            if( clickOffset != null ){
               var xNow: Number = thisThing.parent.mouseX - clickOffset.x;
               var yNow: Number = thisThing.parent.mouseY - clickOffset.y;
               thisThing.myOpticsModel.testSource.setLocation( xNow, yNow );
//                thisThing.x = thisThing.parent.mouseX - clickOffset.x;
//                thisThing.y = thisThing.parent.mouseY - clickOffset.y;
                evt.updateAfterEvent();
            }
        }
        function stopTargetDrag( evt: MouseEvent ):void{
            clickOffset = null;
        }
    } //end makeGrabbable();

    private function makeSourceGrabbable(): void {
        var pixPerMeter: Number = this.container.pixPerMeter;
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
            thisObject.myOpticsModel.testSource.setLocation( xInPix, yInPix );
            evt.updateAfterEvent();
        }//end of dragTarget()

    }//end makeSourceGrabbable()

    public function update():void{
        this.x = myOpticsModel.testSource.x;
        this.y = myOpticsModel.testSource.y;
    }//end update
}//end class
}//end package

//Error:[optics-lab]: command line: unable to open 'C:\Users\Duso\.IntelliJIdea13\system\compile-server\_temp_\IntelliJ_IDEA\idea-666F1D32-A52FB50C.xml'
