/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/6/12
 * Time: 8:25 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.triglab.view {
import edu.colorado.phet.flashcommon.controls.NiceLabel;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.triglab.model.TrigModel;
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;
import edu.colorado.phet.triglab.util.Util;

import flash.display.Graphics;

import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.geom.Point;

//View of xy graph of trig functions
public class GraphView extends Sprite{

    private var myMainView: MainView;
    private var myTrigModel: TrigModel;
    private var wavelengthInPix: Number; //number of pixels in one wavelength
    private var amplitudeInPix: Number;  //number of pixels in one amplitude of sine or cos = nbr pixels in one unit on y-axis
    private var nbrWavelengths: int;     //number of wavelengths in full graph; must be even number; only a portion of graph is shown to user
    private var leftEndInQtrWavelengths: int;      //number of 1/4 wavelengths on x-axis between left end of graph and origin
    private var rightEndInQtrWavelengths: int;     //number of 1/4 wavelengths on x-axis between origin and right end of graph
    private var axesGraph: Sprite;
    private var cosGraph: Sprite;
    private var sinGraph: Sprite;
    private var tanGraph: Sprite;
    private var valueIndicator: Sprite;          //red ball which shows current value on graph
    private var verticalLineToCurrentValue: Sprite
    private var arrowHead: Sprite;
    private var gVertLine: Graphics;
    //private var whichValueIndicator: String;     //the string is "cos", "sin", or "tan" depending on which graph is selected.
    private var whichGraphToShow: int;           //0 = cos graph, 1 = sin graph, 2 = tan graph
    private var _showCos: Boolean;
    private var _showSin: Boolean;
    private var _showTan: Boolean;
    private var cos_str: String;
    private var sin_str: String;
    private var tan_str: String;
    private var theta_str: String;
    private var cos_lbl: NiceLabel;
    private var sin_lbl: NiceLabel;
    private var tan_lbl: NiceLabel;
    private var theta_lbl: NiceLabel;


    public function GraphView( myMainView: MainView,  myTrigModel: TrigModel ) {
        this.myMainView = myMainView;
        this.myTrigModel = myTrigModel;
        this.myTrigModel.registerView( this );
        this.initializeStrings();
        this.init();
        this.myTrigModel.updateViews();
    }

    private function initializeStrings():void{
        this.theta_str = FlexSimStrings.get( "theta", "\u03b8")
        this.cos_str = FlexSimStrings.get( "cos", "cos") + theta_str;
        this.sin_str = FlexSimStrings.get( "sin", "sin") + theta_str;
        this.tan_str = FlexSimStrings.get( "tan", "tan") + theta_str;

    }

    private function init():void{
        this.wavelengthInPix = 300;
        this.nbrWavelengths = 2*2;  //must be even number, so equal nbr of wavelengths are shown on right and left of origin.
        this.leftEndInQtrWavelengths = 8;
        this.rightEndInQtrWavelengths = 6;
        this.amplitudeInPix = 70;
        this.axesGraph = new Sprite();
        this.cosGraph = new Sprite();
        this.sinGraph = new Sprite();
        this.tanGraph = new Sprite();
        this.valueIndicator = new Sprite();
        this.verticalLineToCurrentValue = new Sprite();
        this.arrowHead = new Sprite();
        this.cos_lbl = new NiceLabel( 20, cos_str );
        this.sin_lbl = new NiceLabel( 20, sin_str );
        this.tan_lbl = new NiceLabel( 20, tan_str );
        this.theta_lbl = new NiceLabel( 20, theta_str );
        this.cos_lbl.setFontColor( Util.XYAXESCOLOR );
        this.sin_lbl.setFontColor( Util.XYAXESCOLOR );
        this.tan_lbl.setFontColor( Util.XYAXESCOLOR );
        this.theta_lbl.setFontColor( Util.XYAXESCOLOR );
        this.gVertLine = verticalLineToCurrentValue.graphics;
        this.drawAxesGraph();
        this.drawTrigFunctions();
        this.drawArrowHead();
        this.drawValueIndicator();
        this.makeValueIndicatorGrabbable();
        this.addChild( axesGraph );
        this.addChild( cosGraph );
        this.addChild( sinGraph );
        this.addChild( tanGraph );
        this.addChild( verticalLineToCurrentValue );
        this.verticalLineToCurrentValue.addChild( arrowHead );
        this.addChild( valueIndicator );
        this.addChild( theta_lbl );
        this.addChild( cos_lbl );
        this.addChild( sin_lbl );
        this.addChild( tan_lbl );
    }

    //xy axes, origin is at (0, 0) in the sprite
    private function drawAxesGraph():void{
        var gAxes: Graphics = axesGraph.graphics;
        with( gAxes ){
            clear();
            lineStyle( Util.THICKNESS3, Util.XYAXESCOLOR, 1 );
            moveTo( -leftEndInQtrWavelengths*wavelengthInPix/4, 0 );
            lineTo( rightEndInQtrWavelengths*wavelengthInPix/4, 0)
//            moveTo( -wavelengthInPix*nbrWavelengths/2, 0 );   //x-axis
//            lineTo( wavelengthInPix*nbrWavelengths/2, 0 );
            moveTo( 0, -1.4*amplitudeInPix );                 //y-axis
            lineTo( 0, 1.4*amplitudeInPix );
            var ticLength: int = 5;
            for( var n:int = -leftEndInQtrWavelengths + 1 ; n < rightEndInQtrWavelengths; n++ ){
                moveTo( n*wavelengthInPix/4, -ticLength );
                lineTo( n*wavelengthInPix/4, +ticLength );
            }
            for ( var s: int = -1; s <= 1; s += 2 ){
                moveTo( -ticLength, s*amplitudeInPix );
                lineTo( +ticLength, s*amplitudeInPix );
            }
            //position labels
            var offset: Number = 15
            this.cos_lbl.x = -cos_lbl.width/2;// - offset;
            this.cos_lbl.y = -1.9*amplitudeInPix;
            this.sin_lbl.x = -sin_lbl.width/2;// - offset;
            this.sin_lbl.y = -1.9*amplitudeInPix;
            this.tan_lbl.x = -tan_lbl.width/2;// - offset;
            this.tan_lbl.y = -1.9*amplitudeInPix;
            this.theta_lbl.x = rightEndInQtrWavelengths*wavelengthInPix/4 - 2*theta_lbl.width;
            this.theta_lbl.y = 0;
            //draw arrows on ends of axes
            //x-axis arrow
            var length: Number = 10;
            var halfWidth: Number = 4;
            var xEnd: Number = rightEndInQtrWavelengths*wavelengthInPix/4;
            var yEnd: Number = 1.5*amplitudeInPix;
            beginFill( Util.XYAXESCOLOR, 1 );
            moveTo( xEnd - length,  halfWidth );
            lineTo( xEnd, 0 );
            lineTo( xEnd - length,  -halfWidth );
            lineTo( xEnd - length,  halfWidth );
            endFill();
            //y-axis arrow
            beginFill( Util.XYAXESCOLOR, 1)
            moveTo( -halfWidth, -yEnd + length);
            lineTo( 0, -yEnd );
            lineTo( +halfWidth, -yEnd + length );
            lineTo( -halfWidth, -yEnd + length);
            endFill();
        }
    }//end drawAxesGraph()

    private function drawArrowHead():void{
        var gAH: Graphics = arrowHead.graphics;
        var color: uint
        var length: Number = 15;
        var halfWidth: Number = 6;
        if( whichGraphToShow == 0 ){
            color = Util.COSCOLOR;
        }else if( whichGraphToShow == 1 ){
            color = Util.SINCOLOR;
        }else if( whichGraphToShow == 2 ){
            color = Util.TANCOLOR;
        }
        with( gAH ){
            clear();
            lineStyle( 1, color );
            beginFill( color );
            moveTo( 0, 0 );
            lineTo( -halfWidth, length );
            lineTo( halfWidth, length );
            lineTo( 0, 0 )
            endFill()
        }
    }//end drawArrowHead

    private function drawTrigFunctions():void{
        var N:int = wavelengthInPix;
        var gCos: Graphics = cosGraph.graphics;
        var leftEndInPix: int = - leftEndInQtrWavelengths*wavelengthInPix/4;
        var rightEndInPix: int = rightEndInQtrWavelengths*wavelengthInPix/4;
        var xInit:Number = -2*Math.PI*leftEndInPix/wavelengthInPix;
        with( gCos ){
            clear();
            lineStyle( 3, Util.COSCOLOR );
            moveTo( leftEndInPix, -amplitudeInPix*Math.cos( xInit ) );
            for ( var i: int = leftEndInPix; i <= rightEndInPix; i++ ){
               var x:Number = 2*Math.PI*i/wavelengthInPix;
               lineTo( i, -amplitudeInPix*Math.cos( x ));
            }
        } //end with
        var gSin: Graphics = sinGraph.graphics;
        with( gSin ){
            clear();
            lineStyle( 3, Util.SINCOLOR );
            moveTo( leftEndInPix, -amplitudeInPix*Math.sin( xInit ) );
            for ( i = leftEndInPix; i <= rightEndInPix; i++ ){
                x = 2*Math.PI*i/wavelengthInPix;
                lineTo( i, -amplitudeInPix*Math.sin( x ));
            }//end for
        } //end with
        var gTan: Graphics = tanGraph.graphics;
        var yMax: Number = 2;
        with( gTan ){
            clear();
            lineStyle( 3, Util.TANCOLOR );
            moveTo( leftEndInPix, -amplitudeInPix*Math.sin( xInit ) );
            for ( i = leftEndInPix; i <= rightEndInPix; i++ ){
                x = 2*Math.PI*i/wavelengthInPix;
                var yNbr: Number = Math.tan( x )
                if( Math.abs( yNbr ) < yMax ){
                        lineTo( i, -amplitudeInPix*Math.tan( x ) );
                    }else{
                         moveTo( i, -amplitudeInPix*Math.tan( x ) );
                    }
                lineTo( i, -amplitudeInPix*Math.tan( x ));
            }//end for
//            for( j = -nbrWavelengths/2; j < nbrWavelengths/2; j++ ){
//                moveTo(j*wavelengthInPix, -amplitudeInPix*Math.tan( 0 ));
//                for( i = 1; i <= N; i++ ){
//                    var x:Number = 2*Math.PI*i/wavelengthInPix;
//                    var yNbr: Number = Math.tan( x )
//                    var yInPix: Number =  amplitudeInPix*yNbr;
//                    if( Math.abs( yNbr ) < yMax ){
//                        lineTo( i + j*wavelengthInPix, -yInPix );
//                    }else{
//                         moveTo(  i + j*wavelengthInPix, -yInPix );
//                    }
//
//                }//end for i
//            }//end for j
        } //end with
    } //end drawTrigFunctions()

    private function drawValueIndicator():void{
        var g:Graphics = valueIndicator.graphics;
        with( g ){
            clear();
            //draw red ball handle
            lineStyle( 1, 0x0000ff, 1 );
            beginFill( 0xff0000, 1 );
            drawCircle( 0, 0, 5 );
            endFill();
            //draw rectangular invisible grab area, offset below red ball indicator to avoid collision with UnitCircleView
            lineStyle( 1, Util.XYAXESCOLOR, 0 );
            beginFill( Util.XYAXESCOLOR, 0 );
            drawRect( -30, -20, 60, 200 );
            endFill();
        }
    } //end drawValueIndicator()

    public function set showCos( tOrF:Boolean ):void{
        this._showCos = tOrF;
        this.setVisibilityOfGraphs();
    }
    public function set showSin( tOrF:Boolean ):void{
        this._showSin = tOrF;
        this.setVisibilityOfGraphs();
    }
    public function set showTan( tOrF:Boolean ):void{
        this._showTan = tOrF;
        this.setVisibilityOfGraphs();
    }

    private function setVisibilityOfGraphs():void{
        cosGraph.visible = _showCos;
        sinGraph.visible = _showSin;
        tanGraph.visible = _showTan;
    }

    public function selectWhichGraphToShow( graphNumber: int ):void{
        this.whichGraphToShow = graphNumber;
        cosGraph.visible = false;
        sinGraph.visible = false;
        tanGraph.visible = false;
        cos_lbl.visible = false;
        sin_lbl.visible = false;
        tan_lbl.visible = false;
        if( graphNumber == 0 ){
            cosGraph.visible = true;
            cos_lbl.visible = true;
        }else if ( graphNumber == 1){
            sinGraph.visible = true;
            sin_lbl.visible = true;
        }else if ( graphNumber == 2 ){
            tanGraph.visible = true;
            tan_lbl.visible = true;
        }
        this.resetVerticalLine();
        this.drawArrowHead();
        this.update();
    }//end selectWhichGraphToShow

    private function resetVerticalLine():void{
        gVertLine.clear();
        if( whichGraphToShow == 0 ){
            gVertLine.lineStyle( 6, Util.COSCOLOR, 1, false, "normal", "none" );
        }else if ( whichGraphToShow == 1 ){
            gVertLine.lineStyle( 6, Util.SINCOLOR, 1, false, "normal", "none" );
        }else if ( whichGraphToShow == 2 ){
            gVertLine.lineStyle( 6, Util.TANCOLOR, 1, false, "normal", "none" );
        }
    }

    private function makeValueIndicatorGrabbable():void{
        var thisObject:Object = this;
        var clickOffset: Point;
        this.valueIndicator.buttonMode = true;
        this.valueIndicator.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );

        function startTargetDrag( evt: MouseEvent ): void {
            //thisObject.myMainView.myReadoutView.diagnosticReadout.setText( "startDrag" );
            clickOffset = new Point( evt.localX, evt.localY );
            stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
            stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
        }


        function stopTargetDrag( evt: MouseEvent ): void {
            //thisObject.myMainView.myReadoutView.diagnosticReadout.setText( "stopDrag" );
            clickOffset = null;
            evt.updateAfterEvent();
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
        }

        function dragTarget( evt: MouseEvent ): void {
            var xInPix: Number = thisObject.mouseX - clickOffset.x;
            //thisObject.myMainView.myReadoutView.diagnosticReadout.setText( String( xInPix ) );
            var angleInRads: Number = 2*Math.PI* ( xInPix / wavelengthInPix );
            thisObject.myTrigModel.totalAngle = angleInRads;
            evt.updateAfterEvent();
        }//end of dragTarget()
    }//end makeValueIndicatorGrabbable


    public function update():void{
        var angleInRads:Number = myTrigModel.totalAngle;
        var xPos: Number = (wavelengthInPix*angleInRads/(2*Math.PI)) ;
        var yPos: Number;
        valueIndicator.x = xPos;
        //gVertLine.clear();
        resetVerticalLine();
        gVertLine.moveTo( xPos, 0 ) ;
        if( whichGraphToShow == 0){
            yPos = -amplitudeInPix*Math.cos( angleInRads );
        }else if( whichGraphToShow == 1 ){
            yPos = -amplitudeInPix*Math.sin( angleInRads );
        }else if( whichGraphToShow == 2 ){
            yPos = -amplitudeInPix*Math.tan( angleInRads );
        }
        valueIndicator.y = yPos;
        // Following code is needed to prevent lineTo drawing bug which occurs when
        // drawing coordinates are infinite, caused by infinite value of tangent function
        // at theta = +/-90 degrees
        if( Math.abs( yPos ) < 5*wavelengthInPix  ){    //arbitrary offscreen cutoff
            gVertLine.lineTo( xPos,  yPos ) ;
            if( Math.abs( yPos ) < 0.3*amplitudeInPix ){
                arrowHead.scaleY = -3*yPos/amplitudeInPix;
            }else{
                arrowHead.scaleY = -Math.abs( yPos )/yPos;
            }

            arrowHead.x = xPos;
            arrowHead.y = yPos;
        }else{
            var sign: Number = yPos/Math.abs( yPos );
            gVertLine.lineTo( xPos, sign*5*wavelengthInPix ) ;
        }

    }//end of update()

}//end of class
}//end of package
