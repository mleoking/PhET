//General utilities.  Useful for all sims

package edu.colorado.phet.collisionlab.util {
import flash.display.Sprite;
import flash.events.*;
import flash.geom.*;

public class Util extends Sprite {

    //public static var ORIGINX:Number = 0.10*stage.stageWidth;
    public static var PINGRABBED: Boolean = false;
    public static var BACKGROUNDCOLOR: uint = 0xFFFF99;

    public function Util() {
        //this is a static class
    }//end of constructor

    public static function setXYPosition( mySprite: Sprite, xPos: Number, yPos: Number ): void {
        mySprite.x = xPos;
        mySprite.y = yPos;
    }//end of setXYPostion()

    public static function testFunction(): void {
        trace( "Util.testFunction() called." );
    }

    //Will not work with rotated Sprites
    public static function makeClipDraggable( target: Sprite ): void {
        target.buttonMode = true;
        target.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        target.stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
        target.stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        var theStage: Object = target.parent;
        var clickOffset: Point;

        function startTargetDrag( evt: MouseEvent ): void {
            //problem with localX, localY if sprite is rotated.
            clickOffset = new Point( evt.localX, evt.localY );
            //trace("evt.localX: "+evt.localX);
            //trace("evt.localY: "+evt.localY);
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            //trace("stop dragging");
            clickOffset = null;
        }

        function dragTarget( evt: MouseEvent ): void {
            if ( clickOffset != null ) {  //if dragging

                //trace("theStage.mouseX: "+theStage.mouseX);
                //trace("theStage.mouseY: "+theStage.mouseY);
                target.x = theStage.mouseX - clickOffset.x;
                target.y = theStage.mouseY - clickOffset.y;
                evt.updateAfterEvent();
            }
        }//end of dragTarget()
    }//end of makeClipDraggable

    public static function makePanelDraggableWithBorder2( panel: Sprite, border: Sprite ): void {
        border.buttonMode = true;
        border.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        border.stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
        //        var dmz: int = 50;
        //        var W: int = panel.stage.stageWidth;
        //        var H: int = panel.stage.stageHeight;
        //        var boundingArea: Rectangle = new Rectangle( dmz, dmz, W - 2 * dmz, H - 2 * dmz );

        function startTargetDrag( evt: MouseEvent ): void {
            border.startDrag( false );
            evt.updateAfterEvent();
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            border.stopDrag();
        }
    }

    public static function makePanelDraggableWithBorder( panel: Sprite, border: Sprite ): void {
        border.buttonMode = true;
        border.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        border.stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
        border.stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        var clickOffset: Point;
        //hardcoded because of unresolved issue with stage.stageWidth, stageHeight in various browsers
        var stageW: int = 950;  //panel.stage.stageWidth;
        var stageH: int = 700;  //panel.stage.stageHeight;

        //var tempSprite:Sprite = new Sprite();
        //panel.stage.addChild(tempSprite);

        //var g:Object = tempSprite.graphics;
        //g.clear();
        //g.lineStyle(1,0x000000);
        //g.drawRect(0,0,stageW, stageH);

        function startTargetDrag( evt: MouseEvent ): void {
            //problem with localX, localY if sprite is rotated.
            clickOffset = new Point( evt.stageX - panel.x, evt.stageY - panel.y );

            //trace("evt.stageX = " + evt.stageX + "   panel.x = " + panel.x);
            //trace("evt.target.width = "+evt.target.width);
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            //trace("stop dragging");
            clickOffset = null;
        }

        function dragTarget( evt: MouseEvent ): void {
            if ( clickOffset != null ) {  //if dragging
                //trace("theStage.mouseX: "+theStage.mouseX);
                //trace("theStage.mouseY: "+theStage.mouseY);
                //trace("evt.stageX = "+evt.stageX +"   stageW = "+stageW+"   stageH = "+stageH);
                //minD prevents user from dragging panel completely off Stage
                var minD: Number = 50;
                if ( evt.stageX > minD && evt.stageX < stageW - minD ) {
                    panel.x = evt.stageX - clickOffset.x;
                }
                if ( evt.stageY > minD && evt.stageY < stageH - minD ) {
                    panel.y = evt.stageY - clickOffset.y;
                }
                evt.updateAfterEvent();
            }
        }//end of dragTarget()
    }//end of makePanelDraggableWithBorder

    public static function makeRotatedClipDraggable( target: Sprite ): void {
        target.buttonMode = true;
        target.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        target.stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
        target.stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        var theStage: Object = target.parent;
        var clickOffset: Point;

        function startTargetDrag( evt: MouseEvent ): void {
            //problem with localX, localY if sprite is rotated.
            var rotation: Number = evt.target.rotation;
            var rads: Number = rotation * Math.PI / 180;
            //trace("Util.makeRotatedClipDraggable() rotation = "+rotation);
            var sinTheta: Number = Math.sin( rads );
            var cosTheta: Number = Math.cos( rads );
            var x: Number = evt.localX;
            var y: Number = evt.localY;
            var xP: Number = x * cosTheta - y * sinTheta;
            var yP: Number = x * sinTheta + y * cosTheta;
            clickOffset = new Point( xP, yP );
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            //trace("stop dragging");
            clickOffset = null;
        }

        function dragTarget( evt: MouseEvent ): void {
            if ( clickOffset != null ) {  //if dragging

                //trace("theStage.mouseX: "+theStage.mouseX);
                //trace("theStage.mouseY: "+theStage.mouseY);
                target.x = theStage.mouseX - clickOffset.x;
                target.y = theStage.mouseY - clickOffset.y;
                evt.updateAfterEvent();
            }
        }//end of dragTarget()
    }//end of makeRotatedClipDraggable

    public static function round( input: Number, nPlaces: int ): Number {
        var result: Number;
        var factor: Number = Math.pow( 10, nPlaces );
        result = Math.round( factor * input ) / factor;
        return result
    }

}//end of class


}//end of package