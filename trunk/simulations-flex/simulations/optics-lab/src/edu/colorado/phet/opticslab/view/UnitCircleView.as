/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/6/12
 * Time: 8:40 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.opticslab.view {
import flash.display.Graphics;
import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.geom.Point;
import flash.text.TextFormat;

public class UnitCircleView extends Sprite {

    private var myMainView: MainView;
    private var myTrigModel: TrigModel;

    private var unitCircleGraph: Sprite ; //unit circle centered on xy axes
    private var triangleDiagram: Sprite;  //triangle drawn on unit circle, the ratio of the sides are the trig functions
    private var specialAngleMarks: Sprite; //fudicial marks on unit circle showing where angle = 0, 30, 45, 60, 90, etc
    private var horizArrowHead: Sprite;   //arrow head on xLeg of triangle
    private var vertArrowHead: Sprite;    //arrow head on yLeg of triangle
    private var gridLines: Sprite ;       //optional gridlines on unit circle
    private var labelsLayer: Sprite;      //optional labels x, y, 1, theta
    private var angleArc: Sprite;         //arc showing the angle theta
    private var angleArcArrowHead: Sprite;//arrow head on end of arc showing angle theta
    private var angleHandle: Sprite;//grabbable handle for setting angle on unit Circle
    private var radius:Number;      //radius of unit circle in pixels
    private var previousAngle: Number;
    private var smallAngle: Number;   //angle between -pi and +pi in radians
    private var totalAngle: Number;
    private var _trigMode: int;      //0 when displaying cos graph, 1 for sin, 2 for tan
    //private var _specialAnglesMode: Boolean;  //true if restricted to angles 0, 30, 45, 60, 90, etc
    //private var nbrHalfTurns:Number;

    //Labels
    private var x_lbl: NiceLabel;       //x-label on x-axis
    private var y_lbl: NiceLabel;       //y-label on y-axis
    private var x2_lbl: NiceLabel;      //x-label on xyr triangle
    private var y2_lbl: NiceLabel;      //y-lable on xyr triangle
    private var one_lbl: NiceLabel;     //1 label on radius
    private var theta_lbl: NiceLabel;   //angle label

    //internationalized strings
    private var x_str: String;
    private var y_str: String;
    private var plusOne_str;
    private var minusOne_str;
    private var one_str: String;
    private var theta_str: String;

    private var grabbed: Boolean;   //true if angle selection ball is grabbed

    private var stageW: int;
    private var stageH: int;



    public function UnitCircleView( myMainView:MainView, myTrigModel:TrigModel) {
        this.myMainView = myMainView;
        this.myTrigModel = myTrigModel;
        this.myTrigModel.registerView( this );
        this.unitCircleGraph = new Sprite();
        this.specialAngleMarks = new Sprite();
        this.triangleDiagram = new Sprite();
        this.horizArrowHead = new Sprite();
        this.vertArrowHead = new Sprite();
        this.gridLines = new Sprite();
        this.labelsLayer = new Sprite();
        this.angleArc = new Sprite();
        this.angleArcArrowHead = new Sprite();
        this.angleHandle = new Sprite();
        this.grabbed = false;
        this.internationalizeStrings();
        this.x_lbl = new NiceLabel( 25, x_str );
        this.y_lbl = new NiceLabel( 25, y_str );
        this.x2_lbl = new NiceLabel( 30, x_str );
        this.y2_lbl = new NiceLabel( 30, y_str );
        this.one_lbl = new NiceLabel( 30, one_str );
        this.theta_lbl = new NiceLabel( 30, theta_str );
        var tFormat: TextFormat = new TextFormat( "Times New Roman")
        this.one_lbl.setTextFormat( tFormat );
        this.theta_lbl.setTextFormat( tFormat );
        this.one_lbl.setBold( true );
        this.theta_lbl.setBold( true );
        this.x_lbl.setFontColor( Util.XYAXESCOLOR );
        this.y_lbl.setFontColor( Util.XYAXESCOLOR );
        this.x2_lbl.setFontColor( Util.XYAXESCOLOR );
        this.y2_lbl.setFontColor( Util.XYAXESCOLOR );
        this.one_lbl.setFontColor( Util.XYAXESCOLOR );
        this.theta_lbl.setFontColor( Util.XYAXESCOLOR );
        this.initialize();
    }

    private function internationalizeStrings():void{
        this.x_str = FlexSimStrings.get( "x", "x");
        this.y_str = FlexSimStrings.get( "y", "y");
        this.plusOne_str = FlexSimStrings.get( "plusOne", "+1" );
        this.minusOne_str = FlexSimStrings.get( "minusOne", "-1" );
        this.one_str = FlexSimStrings.get( "one", "1");
        this.theta_str = FlexSimStrings.get( "theta", "\u03b8" );   //greek letter theta
    }

    private function initialize():void{
        this.stageW = this.myMainView.stageW;
        this.stageH = this.myMainView.stageH;
        this.radius = 200;
        this.addChild( gridLines );
        this.addChild( unitCircleGraph );
        this.unitCircleGraph.addChild( triangleDiagram );
        this.triangleDiagram.addChild( horizArrowHead );
        this.triangleDiagram.addChild( vertArrowHead );
        this.unitCircleGraph.addChild( labelsLayer );
        this.unitCircleGraph.addChild( angleArc );
        this.unitCircleGraph.addChild( specialAngleMarks );
        this.angleArc.addChild( angleArcArrowHead );
        this.unitCircleGraph.addChild( this.angleHandle );
        this.unitCircleGraph.addChild( x_lbl );
        this.unitCircleGraph.addChild( y_lbl );
        this.labelsLayer.addChild( one_lbl );
        this.labelsLayer.addChild( x2_lbl );
        this.labelsLayer.addChild( y2_lbl );
        this.labelsLayer.addChild( theta_lbl );
        this.drawUnitCircle();
        this.drawSpecialAngleMarks();
        this.drawGridLines();
        this.drawAngleHandle();
        this.drawArrowHeads();
        this.makeAngleHandleGrabbable();
        this.smallAngle = 0;
        this.previousAngle = 0;
        this.totalAngle = 0;
        this._trigMode = 0;      //start with trigMode = cos
        //this._specialAnglesMode = false;  //start with allowing arbritrary angle
        //this.nbrHalfTurns = 0;
    } //end of initialize


    private function drawUnitCircle():void{
        var g:Graphics = this.unitCircleGraph.graphics;
        var f: Number = 1.3;     //extent of each axis is -f*radius to +f*radius
        with ( g ){
            clear();
            lineStyle( Util.THICKNESS3, Util.XYAXESCOLOR, 1 );  //black
            //draw xy axes
            moveTo( -f*radius,  0 );
            lineTo( +f*radius,  0 );
            moveTo( 0, -f*radius );
            lineTo( 0, 0.9*f*radius );
            //draw arrow heads on axes
            var length:Number = 10;
            var halfWidth:Number = 6
            //x-axis arrow
            beginFill( Util.XYAXESCOLOR, 1 );
            moveTo( f*radius - length,  halfWidth );
            lineTo( f*radius, 0 );
            lineTo( f*radius - length,  -halfWidth );
            lineTo( f*radius - length,  halfWidth );
            endFill();
            //y-axis arrow
            beginFill( Util.XYAXESCOLOR, 1)
            moveTo( -halfWidth, -f*radius + length);
            lineTo( 0, -f*radius );
            lineTo( +halfWidth, -f*radius + length );
            lineTo( -halfWidth, -f*radius + length);
            endFill();
            //draw unit circle
            lineStyle( Util.THICKNESS3, Util.XYAXESCOLOR, 1 );  //black
            drawCircle( 0, 0, radius );
        }
        x_lbl.x = f*radius - x_lbl.width - 10;
        x_lbl.y = 5;
        y_lbl.x = -y_lbl.width - 10;
        y_lbl.y = -f*radius + 5;
    }  //end drawUnitCircle()

    /*
    * Draw sprite with small circular marks on unit circle showing locations of "special" angles:
    * 0, 30, 45, 60, 90, 120, etc.  That is, angles for which the trig functions are simple ratios.
    * */
    private function drawSpecialAngleMarks():void{
        var g: Graphics = specialAngleMarks.graphics;
        g.clear();
        g.lineStyle( 1, 0x000000 );
        var angles:Array = [0, 30, 45, 60, 90, 120, 135, 150, 180, -30, -45, -60, -90, -120, -135, -150 ] ;
        for( var i: int = 0; i < angles.length; i++ ){
            var angleInRads = angles[i] * Math.PI/180;
            var xComp: Number = radius * Math.cos( angleInRads );
            var yComp: Number = radius * Math.sin( angleInRads );
            g.drawCircle( xComp,  yComp,  5 );
        } //end for

    } //end drawSpecialAngleMarks

    private function drawGridLines():void{
        //grid spacing = 0.5
        var spacing: Number = 0.5*this.radius;
        var gGrid: Graphics = this.gridLines.graphics;
        gGrid.lineStyle( 2, 0x888888, 1 ); //light gray color
        for( var i:int = -2; i <= 2; i++) {
            with(gGrid){
                //draw horizontal lines
                moveTo(-2*spacing,  i*spacing );
                lineTo(+2*spacing,  i*spacing );
                //draw vertical lines
                moveTo( i*spacing, -2*spacing );
                lineTo( i*spacing, +2*spacing );
            }//end with
        }//end for
        var plusOne_lbl = new NiceLabel( 20, plusOne_str );
        var minusOne_lbl = new NiceLabel( 20, minusOne_str );
        var plusOne2_lbl = new NiceLabel( 20, plusOne_str );
        var minusOne2_lbl = new NiceLabel( 20, minusOne_str );
        this.gridLines.addChild( plusOne_lbl );
        this.gridLines.addChild( plusOne2_lbl );
        this.gridLines.addChild( minusOne_lbl );
        this.gridLines.addChild( minusOne2_lbl );
        plusOne_lbl.x = 2*spacing + 10;
        plusOne_lbl.y = - plusOne_lbl.height;
        minusOne_lbl.x = -2*spacing - minusOne_lbl.width - 5;
        plusOne2_lbl.y = -2*spacing - plusOne2_lbl.height;
        minusOne2_lbl.y = 2*spacing;
        minusOne2_lbl.x = -minusOne2_lbl.width - 5;
    }//end drawGridLines

    private function drawArrowHeads():void{
        var length:Number = 25;
        var halfWidth:Number = 8
        var gH: Graphics = horizArrowHead.graphics;
        with( gH ){
            clear();
            lineStyle( 1, Util.COSCOLOR );
            beginFill( Util.COSCOLOR );
            moveTo( 0, 0 );
            lineTo( -length,  -halfWidth );
            lineTo( -length,  halfWidth );
            lineTo( 0, 0 );
        }
        var gV: Graphics = vertArrowHead.graphics;
        with( gV ){
            clear();
            lineStyle( 1, Util.SINCOLOR );
            beginFill( Util.SINCOLOR );
            moveTo( 0, 0 );
            lineTo( -halfWidth, length );
            lineTo( halfWidth, length );
            lineTo( 0, 0 );
        }
        var gAAA: Graphics = angleArcArrowHead.graphics;
        with( gAAA ){
            length = 10;
            halfWidth = 3;
            clear();
            lineStyle( 1, Util.XYAXESCOLOR );
            beginFill( Util.XYAXESCOLOR );
            moveTo( 0, 0 );
            lineTo( -halfWidth, length );
            lineTo( halfWidth, length );
            lineTo( 0, 0 );
        }
    }//end drawArrowHeads()

    private function drawTriangle():void{
        var gTriangle: Graphics = this.triangleDiagram.graphics;
        var angle: Number = myTrigModel.smallAngle;
        var xLeg: Number = this.radius*myTrigModel.cos;
        var yLeg: Number = -this.radius*myTrigModel.sin;
        var xColor: uint = Util.XYAXESCOLOR;
        var yColor: uint = Util.XYAXESCOLOR;
        var hypColor: uint = Util.XYAXESCOLOR;
        var xStroke: int = 8;
        var yStroke: int = 2;
        if( _trigMode == 0 ){
            xColor = Util.COSCOLOR;
            xStroke = 6;
            horizArrowHead.visible = true;
            vertArrowHead.visible = false;
            var fH: Number = 0.9;
            var fV: Number = 1;
        }else if ( _trigMode == 1 ){
            yColor = Util.SINCOLOR;
            yStroke = 6;
            horizArrowHead.visible = false;
            vertArrowHead.visible = true;
            fH = 1;
            fV = 0.9;
        }else if ( _trigMode == 2 ){
            xColor = Util.COSCOLOR;
            yColor = Util.SINCOLOR;
            xStroke = 6;
            yStroke = 6;
            horizArrowHead.visible = true;
            vertArrowHead.visible = true;
            fH = 0.9;
            fV = 0.9;
        }
        with( gTriangle ){
            clear();
            //draw hypotenuse
            lineStyle( Util.THICKNESS3, hypColor, 1, false, "normal", "none" );
            moveTo( 0, 0 );
            lineTo( xLeg, yLeg );
            //draw x-leg
            lineStyle( xStroke, xColor,  1, false, "normal", "none"  ) ;
            moveTo( 0, 0 );
            lineTo( fH*xLeg, 0 );
            horizArrowHead.x = xLeg;
            horizArrowHead.y = 0;
            if( Math.abs(xLeg) < 0.3*radius ){
                horizArrowHead.scaleX = 3*xLeg/radius;
            }else{
                horizArrowHead.scaleX = xLeg/Math.abs( xLeg );
            }
            //draw y-leg
            lineStyle( yStroke, yColor,  1, false, "normal", "none") ;
            moveTo( xLeg, 0 );
            lineTo( xLeg,  fV*yLeg );
            vertArrowHead.y = yLeg;
            vertArrowHead.x = xLeg;
            if( Math.abs(yLeg) < 0.3*radius ){
                vertArrowHead.scaleY = -3*yLeg/radius;
            }else{
                vertArrowHead.scaleY = -yLeg/Math.abs( yLeg );
            }
        }
    }//end drawTriangle

    private function drawAngleArc():void{
        var gArc: Graphics = this.angleArc.graphics;
        var r: Number = 0.3*radius;
        var sign: Number;
        totalAngle = myTrigModel.totalAngle;
        smallAngle = myTrigModel.smallAngle;
        if( totalAngle != 0 ){
            sign = Math.abs( totalAngle )/totalAngle;
        }else{
            sign = 0;
        }
        //trace( "UnitCircleView.drawAngleArc  sign of angle = " + sign + "totalAngle = " + totalAngle );
        with( gArc ){
            clear();
            lineStyle( Util.THICKNESS2, Util.XYAXESCOLOR, 1 );
            moveTo( r, 0 );
            if( sign > 0 ){
                for( var ang: Number = 0; ang <= totalAngle; ang += 0.02 ){
                    r -= 0.02;
                    lineTo( r*Math.cos( ang ), -r*Math.sin( ang ) )
                }
            }else if( sign < 0 ){
                for( var ang: Number = 0; ang >= totalAngle; ang -= 0.02 ){
                    r -= 0.02;
                    lineTo( r*Math.cos( ang ), -r*Math.sin( ang ) )
                }
            }
        } //end with
        //locate arrowHead on angle arc, if arc is big enough
        if( Math.abs( totalAngle ) < 20*Math.PI/180 ){
            angleArcArrowHead.visible = false;
        }else{
            angleArcArrowHead.visible = true;
        }
        angleArcArrowHead.x = r*Math.cos( totalAngle );
        angleArcArrowHead.y = -r*Math.sin( totalAngle );
        if( totalAngle < 0 ){
            angleArcArrowHead.rotation = 180 - myTrigModel.smallAngle*180/Math.PI;
        }else{
            angleArcArrowHead.rotation = -myTrigModel.smallAngle*180/Math.PI;
        }

    }//end drawAngleArc()

    private function drawAngleHandle():void{
        var gBall: Graphics = this.angleHandle.graphics;
        var ballRadius: Number = 5;
        with( gBall ){
            clear();
            lineStyle( 1, 0x0000ff, 1.0 )
            beginFill( 0xff0000, 1.0 )    //ball is red
            drawCircle( 0, 0, ballRadius );
            endFill();
        }
        var grabArea: Sprite = new Sprite();
        this.angleHandle.addChild( grabArea );
        //draw invisible grab area
        var gGrab: Graphics = grabArea.graphics;
        var grabRadius: Number = 50;
        with( gGrab ){
            clear();
            lineStyle( 1, 0xffffff, 0 );
            beginFill( 0x00ff00,0 );
            drawCircle( 0, 0, grabRadius );
            endFill();
        }
    }//end drawAngleHandle();



    private function makeAngleHandleGrabbable():void{
        var thisObject:Object = this;
        var clickOffset: Point;
        this.angleHandle.buttonMode = true;
        this.angleHandle.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );

        function startTargetDrag( evt: MouseEvent ): void {
            thisObject.grabbed = true;
            clickOffset = new Point( evt.localX, evt.localY );
            stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
            stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
        }


        function stopTargetDrag( evt: MouseEvent ): void {
            thisObject.grabbed = false;
            clickOffset = null;
            evt.updateAfterEvent();
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
        }

        function dragTarget( evt: MouseEvent ): void {
            var xInPix: Number = thisObject.unitCircleGraph.mouseX - clickOffset.x;
            var yInPix: Number = thisObject.unitCircleGraph.mouseY - clickOffset.y;
            var angleInRads: Number = -Math.atan2( yInPix,  xInPix );
//            if( thisObject._specialAnglesMode ){
//                var inputAngle:Number = angleInRads;
//                angleInRads = thisObject.roundToSpecialAngle( angleInRads );
//                //trace( "UnitCircleView.dragTarget  input angle = "+ inputAngle +  "    angleInRads = "+ angleInRads ) ;
//            }
            thisObject.smallAngle = angleInRads;   //minus-sign to be consistent with convention: CCW = +angle, CW = -angle
            thisObject.myTrigModel.smallAngle = angleInRads;
            evt.updateAfterEvent();
        }//end of dragTarget()
    }//end makeAngleHandleGrabbable


//    /*Take input small angle in rads (between -pi and +pi) and convert to nearest "special" angle in rads.
//     *The special angles (in degrees) are 0, 30, 45, 60, 90, etc.
//     */
//    private function roundToSpecialAngle( anyAngleInRads: Number ): Number{
//        var angleInDegs: Number = anyAngleInRads*180/Math.PI;
//        var nearestSpecialAngleInRads: Number = 0;
//        var angles: Array = [-150, -135, -120, -90, -60, -45, -30, 0, 30, 45, 60, 90, 120, 135, 150, 180 ];
//        var border: Array = [-165, -142.5, -127.5, -105, -75, -52.5, -37.5, -15, 15, 37.5, 52.5, 75, 105, 127.5, 142.5, 165 ] ;
//        for ( var i:int = 0; i < angles.length; i++ ){
//            if( angleInDegs > border[i] && angleInDegs < border[i + 1] ){
//                nearestSpecialAngleInRads = angles[i]*Math.PI/180;
//            }
//            //Must deal with 180 deg angle as a special case.
//            if( angleInDegs > 165 || angleInDegs < -165 ){
//                nearestSpecialAngleInRads = 180*Math.PI/180;
//            }
//        }
//        return nearestSpecialAngleInRads;
//    }//end roundToSpecialAngle()

    private function positionAngleHandle( angleInRads: Number ):void{
        var xInPix: Number = this.radius*Math.cos( angleInRads );
        var yInPix: Number = this.radius*Math.sin( angleInRads );
        this.angleHandle.x = xInPix;
        this.angleHandle.y = -yInPix;
        this.positionLabels();
    }

    private function positionLabels():void{
        smallAngle = myTrigModel.smallAngle;
        totalAngle = myTrigModel.totalAngle;
        var pi: Number = Math.PI;
        //set visibility of labels
        if( Math.abs( totalAngle ) < 20*pi/180 ){
            theta_lbl.visible = false;
        }else{
            theta_lbl.visible = true;
        }
        var sAngle: Number = Math.abs( smallAngle*180/pi );
        if( sAngle < 10 || (180 - sAngle) < 10 ){
            y2_lbl.visible = false;
        } else{
            y2_lbl.visible = true;
        }
        if( Math.abs(90 - sAngle) < 5 ){
            x2_lbl.visible = false;
        }else{
            x2_lbl.visible = true;
        }
        //position one-label
        var angleOffset: Number = 7*Math.PI/180;
        var sign: int = 1;

        if( ( smallAngle > pi/2 && smallAngle < pi ) ||( smallAngle > -pi/2 && smallAngle < 0 )){
            sign = -1;
        }
        var xInPix: Number = this.radius*Math.cos( smallAngle + sign*angleOffset );
        var yInPix: Number = this.radius*Math.sin( smallAngle + sign*angleOffset );
        this.one_lbl.x = 0.6*xInPix - 0.5*one_lbl.width;
        this.one_lbl.y = - 0.6*yInPix -0.5*one_lbl.height;
        //position x-label
        var xPos: Number = 0.5*radius*Math.cos( smallAngle ) - 0.5*x2_lbl.width;
        var yPos: Number = -0.1*x2_lbl.height;
        if( smallAngle < 0 ){ yPos = -x2_lbl.height }
        x2_lbl.x = xPos;
        x2_lbl.y = yPos;
        //position y-label
        sign = 1;
        if( ( smallAngle > pi/2 && smallAngle < pi ) ||( smallAngle > -pi && smallAngle < -pi/2 )){
            sign = -1;
        }
        xPos = radius*Math.cos(smallAngle) - 0.5*x2_lbl.width + sign*0.8*x2_lbl.width;
        yPos = -0.5*radius*Math.sin( smallAngle ) - 0.5*y2_lbl.height;
        y2_lbl.x = xPos;
        y2_lbl.y = yPos;
        //show and position theta-label on angle arc if arc is greater than 20 degs


        xPos = 0.37*radius*Math.cos( totalAngle/2 ) - 0.5*theta_lbl.width;
        yPos = -0.37*radius*Math.sin( totalAngle/2 ) - 0.5*theta_lbl.height;
        theta_lbl.x = xPos;
        theta_lbl.y = yPos;
    }//end positionLabels()

    public function set trigMode( mode: int):void {
        this._trigMode = mode;  //0 for cos, 1 for sin, 2 for tan
        this.drawTriangle();
    }

    public function setGridLinesVisibility( tOrF: Boolean ):void{
        this.gridLines.visible = tOrF;
    }

    public function setLabelsVisibility( tOrF: Boolean ):void{
        this.labelsLayer.visible = tOrF;
    }

//    public function set specialAnglesMode( tOrF: Boolean ):void{
//        this._specialAnglesMode = tOrF;
//        if( this._specialAnglesMode ){
//            this.specialAngleMarks.visible = true;
//            myTrigModel.smallAngle = this.roundToSpecialAngle( myTrigModel.smallAngle );
//        } else{
//            this.specialAngleMarks.visible = false;
//        }
//    }

    public function update():void{
        this.positionAngleHandle( myTrigModel.smallAngle );
        this.drawTriangle();
        this.drawAngleArc();
        this.specialAngleMarks.visible = myTrigModel.specialAnglesMode;
    }

}  //end of class
}  //end of package
