
/**
 * Created by IntelliJ IDEA.
 * User: Dubson
 * Date: 6/1/11
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.radiatingcharge.view {
import edu.colorado.phet.flashcommon.controls.Tab;
import edu.colorado.phet.flashcommon.controls.TabBar;
import edu.colorado.phet.radiatingcharge.*;
import edu.colorado.phet.radiatingcharge.control.ControlPanel;
import edu.colorado.phet.radiatingcharge.model.FieldModel;

import org.aswing.event.ModelEvent;


import edu.colorado.phet.flashcommon.view.PhetIcon;
import edu.colorado.phet.radiatingcharge.util.SpriteUIComponent;

import flash.display.Sprite;

import mx.containers.Canvas;
import mx.controls.sliderClasses.Slider;

//main view and communications hub for Radiating Charge sim
public class MainView extends Canvas {

    public var myFieldModel:FieldModel;
    public var myChargeView:ChargeView;
    public var myFieldView:FieldView;
    public var myControlPanel:ControlPanel;

//    public var myControlPanel:ControlPanel;

    public var phetLogo: Sprite;
    public var stageH: Number;
    public var stageW: Number;

    //Internalized strings are located at:
    //

    public function MainView( stageW: Number, stageH: Number ) {
        percentWidth = 100;
        percentHeight = 100;
        this.stageH = stageH;
        this.stageW = stageW;
        this.myFieldModel = new FieldModel(this);
        this.myChargeView = new ChargeView( this, myFieldModel ) ;
        this.myFieldView = new FieldView(this, myFieldModel );
        this.myControlPanel = new ControlPanel( this, this.myFieldModel );

        this.addChild( new SpriteUIComponent( this.myFieldView ));
        this.addChild( new SpriteUIComponent( this.myChargeView ));
        this.addChild( myControlPanel );
        this.myControlPanel.x = 0.8*stageW;
        this.myControlPanel.y = 0.1*stageH;

        this.phetLogo = new PhetIcon();
        this.phetLogo.x = stageW - 2.0 * this.phetLogo.width;
        this.phetLogo.y = stageH - 1.5 * this.phetLogo.height;

        //trace( "stageW:" + stageW + "   stageH:" +stageH );

        //phetLogo now in tab area
        this.addChild( new SpriteUIComponent( phetLogo ) );
        this.initializeAll();
    }//end of constructor


    public function initializeAll(): void {
//        this.myModel.pauseSim();

    }//end of initializeAll()



}//end of class
} //end of package
