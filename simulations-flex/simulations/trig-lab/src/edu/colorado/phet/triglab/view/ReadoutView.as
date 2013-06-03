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
    private var totalAngleReadout: NiceLabel;
    private var cosineReadout: NiceLabel;


    //internationalized strings
    private var angle_str:String;
    private var sine_str: String;
    private var cosine_str: String;
    private var tangent_str: String;
    private var radians_str: String;
    private var degrees_str: String;

    public function ReadoutView( myMainView: MainView,  myTrigModel: TrigModel ) {
        this.myMainView = myMainView;
        this.myTrigModel = myTrigModel;
        this.myTrigModel.registerView( this );
        this.init();
    }

    private function init():void{
        initializeStrings();
        this.background = new VBox();
        with ( this.background ) {
            setStyle( "backgroundColor", 0x66ff66 );
            setStyle( "borderStyle", "solid" )
            setStyle( "borderColor", 0x009900 );
            setStyle( "cornerRadius", 15 );
            setStyle( "borderThickness", 8 );
            setStyle( "paddingTop", 20 );
            setStyle( "paddingBottom", 20 );
            setStyle( "paddingRight", 10 );
            setStyle( "paddingLeft", 10 );
            setStyle( "verticalGap", 15 );
            setStyle( "horizontalAlign", "center" );
        }
        //this.angleBox = new HBox();
        this.addChild( background );
        //this.background.addChild( angleBox );
        this.smallAngleReadout = new NiceLabel( );
        //next two lines needed to set size of label
        var angleInDeg_str:String = "395";
        this.smallAngleReadout.setText(FlexSimStrings.get("angleEqualsXDegrees", "angle = {0} degrees", [angleInDeg_str]));
        this.background.addChild( new SpriteUIComponent ( this.smallAngleReadout ) );
        this.totalAngleReadout = new NiceLabel( );
        //next two lines needed to set size of label
        var totAngleInDeg_str:String = "395";
        this.totalAngleReadout.setText(FlexSimStrings.get("angleEqualsXDegrees", "angle = {0} degrees", [totAngleInDeg_str]));
        this.background.addChild( new SpriteUIComponent ( this.totalAngleReadout ) );

        this.cosineReadout = new NiceLabel();
        var cosine_str:String = "0.500";
        this.cosineReadout.setText(FlexSimStrings.get("cosineEqualsX", "cos = {0}", [cosine_str]));
        this.background.addChild( new SpriteUIComponent( this.cosineReadout ));

    }//end init()

    private function initializeStrings():void{
        angle_str = FlexSimStrings.get("angle", "angle:");
        cosine_str = FlexSimStrings.get("cosine", "cosine:");
        sine_str = FlexSimStrings.get("sine", "sine:");
        tangent_str = FlexSimStrings.get("tangent", "tangent:");
        radians_str = FlexSimStrings.get("radians", "radians");
        degrees_str = FlexSimStrings.get("degrees", "degrees");
    }

    private function setAngleReadout(): void {
        var smallAngleInDegrees: Number = this.myTrigModel.smallAngle*180/Math.PI;
        var smallAngleInDegrees_str: String = smallAngleInDegrees.toFixed( 0 );
        this.smallAngleReadout.setText(FlexSimStrings.get("angleEqualsXDegrees", "angle = {0} degrees", [smallAngleInDegrees_str]));

        var totalAngleInDegrees: Number = this.myTrigModel.totalAngle*180/Math.PI;
        var totalAngleInDegrees_str: String = totalAngleInDegrees.toFixed( 0 );
        this.totalAngleReadout.setText(FlexSimStrings.get("angleEqualsXDegrees", "angle = {0} degrees", [totalAngleInDegrees_str]));

        var cos: Number = this.myTrigModel.cos;
        var cosine_str: String = cos.toFixed( 3 );
        this.cosineReadout.setText(FlexSimStrings.get("cosineEqualsX", "cos = {0} ", [cosine_str]));
    }

    public function update():void{
         setAngleReadout();
    }
} //end of class
} //end of package
