/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/2/13
 * Time: 5:30 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.triglab.view {
import edu.colorado.phet.flashcommon.controls.NiceLabel;
import edu.colorado.phet.flashcommon.controls.NiceTextField;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;
import edu.colorado.phet.triglab.model.TrigModel;
import edu.colorado.phet.triglab.util.Util;

import flash.display.Sprite;

import mx.containers.Canvas;
import mx.containers.HBox;
import mx.containers.VBox;
import mx.controls.Label;

//view of readout panel which displays current angle in rads or degrees, values of sine, cos, tangent
public class ReadoutView extends Canvas {
    private var myMainView: MainView;
    private var myTrigModel: TrigModel;
    //private var readoutPanel: Sprite;
    private var background: VBox;
    private var angleBox: HBox;
    private var smallAngleReadout: NiceLabel;
    private var xyReadout: NiceLabel;
    private var totalAngleReadout: NiceLabel;
    private var trigReadoutContainer: Canvas;
    private var cosineReadout: NiceLabel;
    private var sineReadout: NiceLabel;
    private var tangentReadout: NiceLabel;
    public var diagnosticReadout: NiceLabel = new NiceLabel();


    //internationalized strings
    private var xy_str: String;
    private var angle_str:String;
    private var degrees_str: String;
    private var sine_str: String;
    private var cosine_str: String;
    private var tangent_str: String;
    private var radians_str: String;

    public function ReadoutView( myMainView: MainView,  myTrigModel: TrigModel ) {
        this.myMainView = myMainView;
        this.myTrigModel = myTrigModel;
        this.myTrigModel.registerView( this );
        this.init();
    }

    private function init():void{
        initializeStrings();
        this.background = new VBox();
//        with ( this.background ) {
//            setStyle( "backgroundColor", 0x66ff66 );
//            setStyle( "borderStyle", "solid" )
//            setStyle( "borderColor", 0x009900 );
//            setStyle( "cornerRadius", 15 );
//            setStyle( "borderThickness", 8 );
//            setStyle( "paddingTop", 20 );
//            setStyle( "paddingBottom", 20 );
//            setStyle( "paddingRight", 10 );
//            setStyle( "paddingLeft", 10 );
//            setStyle( "verticalGap", 15 );
//            setStyle( "horizontalAlign", "center" );
//        }
        //this.angleBox = new HBox();

        this.xyReadout = new NiceLabel( 25 );
        var x_str:String = "0.455";
        var y_str:String = "0.350";
        this.xyReadout.setText( FlexSimStrings.get( "(x,y)Equals(X,Y)", "(x, y) = ( {0}, {1} )" , [x_str,  y_str]));


        this.smallAngleReadout = new NiceLabel( );
        //next two lines needed to set size of label
        var angleInDeg_str:String = "395";
        this.smallAngleReadout.setText(FlexSimStrings.get("angleEqualsXDegrees", "angle = {0} degrees", [angleInDeg_str]));
        //this.background.addChild( new SpriteUIComponent ( this.smallAngleReadout ) );
        this.totalAngleReadout = new NiceLabel( 25 );
        //next two lines needed to set size of label
        var totAngleInDeg_str:String = "395";
        this.totalAngleReadout.setText(FlexSimStrings.get("angleEqualsXDegrees", "angle = {0} degrees", [totAngleInDeg_str]));

        this.trigReadoutContainer = new Canvas();

        this.cosineReadout = new NiceLabel( 25 );
        var cosine_str:String = "0.500";
        this.cosineReadout.setText(FlexSimStrings.get("cosineEqualsX", "cos = {0}", [cosine_str]));


        this.sineReadout = new NiceLabel( 25 );
        var sine_str:String = "0.500";
        this.sineReadout.setText(FlexSimStrings.get("sineEqualsX", "sin = {0}", [sine_str]));


        this.tangentReadout = new NiceLabel( 25 );
        var tangent_str:String = "0.500";
        this.tangentReadout.setText(FlexSimStrings.get("tangentEqualsX", "tan = {0}", [tangent_str]));



        this.diagnosticReadout.setFontSize( 20 );
        this.diagnosticReadout.setText( " test ")

        //set layers:
        this.addChild( background );
        this.background.addChild( new SpriteUIComponent( this.xyReadout ) );
        this.background.addChild( new SpriteUIComponent ( this.totalAngleReadout ) );
        this.background.addChild( trigReadoutContainer );
        this.trigReadoutContainer.addChild( new SpriteUIComponent( this.cosineReadout ));
        this.trigReadoutContainer.addChild( new SpriteUIComponent( this.sineReadout ));
        this.trigReadoutContainer.addChild( new SpriteUIComponent( this.tangentReadout ));
        this.background.addChild( new SpriteUIComponent( this.diagnosticReadout ));
    }//end init()

    private function initializeStrings():void{
        angle_str = FlexSimStrings.get("angle", "angle:");
        degrees_str = FlexSimStrings.get( "degrees", "degrees")
        cosine_str = FlexSimStrings.get("cosine", "cosine:");
        sine_str = FlexSimStrings.get("sine", "sine:");
        tangent_str = FlexSimStrings.get("tangent", "tangent:");
        radians_str = FlexSimStrings.get("radians", "radians");
        degrees_str = FlexSimStrings.get("degrees", "degrees");
    }

    private function setReadouts(): void {
        var xValue: Number = this.myTrigModel.x;
        var xValue_str: String = xValue.toFixed( 3 );
        var yValue: Number = this.myTrigModel.y;
        var yValue_str: String = yValue.toFixed( 3 );
        this.xyReadout.setText( FlexSimStrings.get( "(x,y)Equals(X,Y)", "( x, y ) = ( {0}, {1} )" , [xValue_str,  yValue_str]));

        var smallAngleInDegrees: Number = this.myTrigModel.smallAngle*180/Math.PI;
        var smallAngleInDegrees_str: String = smallAngleInDegrees.toFixed( 0 );
        this.smallAngleReadout.setText(FlexSimStrings.get("angleEqualsXDegrees", "angle = {0} degrees", [smallAngleInDegrees_str]));

        var totalAngleInDegrees: Number = this.myTrigModel.totalAngle*180/Math.PI;
        var totalAngleInDegrees_str: String = totalAngleInDegrees.toFixed( 0 );
        this.totalAngleReadout.setText(FlexSimStrings.get("angleEqualsXDegrees", "angle = {0} degrees", [totalAngleInDegrees_str]));

        var cos: Number = this.myTrigModel.cos;
        var cosine_str: String = cos.toFixed( 3 );
        this.cosineReadout.setText(FlexSimStrings.get("cosineEqualsX", "cos = {0} ", [cosine_str]));

        var sin: Number = this.myTrigModel.sin;
        sine_str = sin.toFixed( 3 );
        this.sineReadout.setText(FlexSimStrings.get("sineEqualsX", "sin = {0} ", [sine_str]));

        var tan: Number = this.myTrigModel.tan;
        tangent_str = tan.toFixed( 3 );
        this.tangentReadout.setText(FlexSimStrings.get("tangentEqualsX", "tan = {0} ", [tangent_str]));

        setTextColor( xyReadout );
        setTextColor( totalAngleReadout );
        setTextColor( cosineReadout );
        setTextColor( sineReadout );
        setTextColor( tangentReadout );
        setTextColor( diagnosticReadout );
    }

    private function setTextColor( nLabel: NiceLabel ):void{
        nLabel.setFontColor( Util.XYAXESCOLOR );
    }

    public function setVisibilityOfTrigReadout( choice: int ):void{
        cosineReadout.visible = false;
        sineReadout.visible = false;
        tangentReadout.visible = false;
        if( choice == 0 ){
            cosineReadout.visible = true;
        }else if ( choice == 1 ){
            sineReadout.visible = true;
        }else if ( choice == 2 ){
            tangentReadout.visible = true;
        }
    }

    public function update():void{
         setReadouts();
    }
} //end of class
} //end of package
