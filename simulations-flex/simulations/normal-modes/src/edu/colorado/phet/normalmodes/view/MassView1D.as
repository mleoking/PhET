/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created by IntelliJ IDEA.
 * User: General User
 * Date: 6/4/11
 * Time: 12:31 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes.view {
import edu.colorado.phet.normalmodes.model.Model1D;
import edu.colorado.phet.normalmodes.util.TwoHeadedArrow;

import flash.display.Graphics;
import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.geom.Point;

/*
* MassView1D is the view for an individual mass in View1D, which is view for Model1D, a 1D array of coupled masses
* When mouse is inside border zone surrounding mass, vertical or horizontal arrows appear,
* cuing user that mass is grabbable.  All arrows disappear after user grabs any mass.
* */
public class MassView1D extends Sprite {
    private var _index: int;           //integer labeling the mass
    private var myModel1D: Model1D;
    private var container: View1D;
    private var mass: Sprite;          //graphic for the mass
    private var borderZone: Sprite;    //when user mouses over borderZone, arrows appear around mass
    private var arrowH: Sprite;        //horizontal arrow, cues user that mass is movable
    private var arrowV: Sprite;        //vertical arrow

    public function MassView1D( index: int, myModel1D: Model1D, container: View1D ) {
        this._index = index;
        this.myModel1D = myModel1D;
        this.container = container;
        this.mass = new Sprite();
        this.borderZone = new Sprite();
        this.arrowH = new Sprite();
        this.arrowV = new Sprite();
        this.addChild( borderZone );
        this.addChild( arrowH );
        this.addChild( arrowV );
        this.addChild( mass );
        this.drawBorderZone( 150, 300 );
        this.drawHorizontalArrow();
        this.drawVerticalArrow();
        this.drawMass();
        this.makeMassGrabbable();
    } //end constructor


    public function drawBorderZone( width: Number, height: Number ): void {
        var w: Number = width;
        var h: Number = height;
        var g: Graphics = this.borderZone.graphics;
        g.clear();
        g.lineStyle( 3, 0xffffff, 0 );
        g.beginFill( 0xffffff, 0 );
        g.drawRoundRect( -w / 2, -h / 2, w, h, w / 4 );
        g.endFill();
    }

    public function drawVerticalArrow(): void {
        var arrow2HV: TwoHeadedArrow = new TwoHeadedArrow();
        arrow2HV.setArrowDimensions( 10, 20, 4, 50 );  //headRadius, headWidth, shaftRadius,  shaftLength
        arrow2HV.setRegistrationPointAtCenter( true );
        arrow2HV.rotation = 90;
        this.arrowV.addChild( arrow2HV );
        this.arrowV.visible = false;
    }

    public function drawHorizontalArrow(): void {
        var arrow2HH: TwoHeadedArrow = new TwoHeadedArrow();
        arrow2HH.setArrowDimensions( 10, 20, 4, 50 );  //headRadius, headWidth, shaftRadius,  shaftLength
        arrow2HH.setRegistrationPointAtCenter( true );
        this.arrowH.addChild( arrow2HH );
        this.arrowH.visible = false;
    }

    private function drawMass(): void {
        var g: Graphics = this.mass.graphics;
        g.clear();
        var d: Number = 20;   //edge length of square mass in pixels
        g.lineStyle( 3, 0x0000ff, 1 );
        g.beginFill( 0x6666ff, 1 );
        g.drawRoundRect( -d / 2, -d / 2, d, d, d / 4 );
        g.endFill();
        this.mass.visible = true;      //mass always visible
    }//end drawMass()

    public function get index(): int {
        return this._index;
    }

    private function makeMassGrabbable(): void {
        //var target = mySprite;
        //var wasRunning: Boolean;
        var leftEdgeX: Number = this.container.leftEdgeX;
        var leftEdgeY: Number = this.container.leftEdgeY;
        var pixPerMeter: Number = this.container.pixPerMeter;
        var thisObject: Object = this;
        this.mass.buttonMode = true;
        this.mass.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        this.addEventListener( MouseEvent.ROLL_OVER, thisObject.showArrows );
        this.addEventListener( MouseEvent.ROLL_OUT, thisObject.hideArrows );
        var clickOffset: Point;

        function startTargetDrag( evt: MouseEvent ): void {
            thisObject.arrowH.visible = false;
            thisObject.arrowV.visible = false;
            thisObject.myModel1D.grabbedMass = thisObject._index;
            clickOffset = new Point( evt.localX, evt.localY );
            stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
            thisObject.container.clearBorderZones();
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            thisObject.myModel1D.grabbedMass = 0;
            thisObject.myModel1D.computeModeAmplitudesAndPhases();
            clickOffset = null;
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        }

        function dragTarget( evt: MouseEvent ): void {
            var xInPix: Number = thisObject.container.mouseX - clickOffset.x;    //screen coordinates, origin on left edge of stage
            var yInPix: Number = thisObject.container.mouseY - clickOffset.y;    //screen coordinates, origin on left edge of stage
            if ( thisObject.myModel1D.xModes ) {
                var xInMeters: Number = (xInPix - leftEdgeX) / pixPerMeter;
                thisObject.myModel1D.setX( thisObject.index, xInMeters );
            }
            else {
                var yInMeters: Number = -(yInPix - leftEdgeY) / pixPerMeter;    //screen coords vs. cartesian coordinates, hence the minus sign
                thisObject.myModel1D.setY( thisObject.index, yInMeters );
            }
            evt.updateAfterEvent();
        }//end of dragTarget()
    } //end makeMassGrabble

    private function showArrows( evt: MouseEvent ): void {
        if ( this.myModel1D.xModes ) {
            this.arrowH.visible = true;
            this.arrowV.visible = false;
        }
        else {
            this.arrowV.visible = true;
            this.arrowH.visible = false;
        }
    }

    private function hideArrows( evt: MouseEvent ): void {
        this.arrowH.visible = false;
        this.arrowV.visible = false;
    }

    //Called by View1D when any mass is moved.
    public function killArrowListeners(): void {
        this.removeEventListener( MouseEvent.ROLL_OVER, this.showArrows );
        this.removeEventListener( MouseEvent.ROLL_OUT, this.hideArrows );
        this.drawBorderZone( 0, 0 );
    }
} //end class
} //end package
