/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created by IntelliJ IDEA.
 * User: Dubson
 * Date: 7/11/11
 * Time: 7:29 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes.view {
import edu.colorado.phet.flexcommon.FlexSimStrings;

import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

import mx.core.UIComponent;

/*
* "Paused sign" appears in background when user pauses sim.
* Different pausedSigns for the two different model/views (view1D and view2D)
* */
public class PausedSign extends UIComponent {
    private var paused_txt: TextField;
    private var tFormat: TextFormat;
    private var paused_str: String;
    private var myModel: Object;     //either model1 or model2

    public function PausedSign( myModel: Object ) {
        this.myModel = myModel;
        this.myModel.registerView( this );
        paused_txt = new TextField();
        tFormat = new TextFormat();
        paused_str = FlexSimStrings.get( "paused", "PAUSED" );
        tFormat.font = "Arial";
        tFormat.size = 80;
        tFormat.color = 0xffcc00;
        paused_txt.text = paused_str;
        paused_txt.selectable = false;
        paused_txt.autoSize = TextFieldAutoSize.CENTER;
        paused_txt.setTextFormat( tFormat );
        this.addChild( paused_txt );
    }//end constructor

    //register this pausedSign view with the appropriate model
    public function setModel( currentModel: Object ): void {
        this.myModel.unregisterView( this );
        this.myModel = currentModel;
        this.myModel.registerView( this );
    }

    public function update(): void {
        this.visible = myModel.paused;
    }

}//end class
}//end package
