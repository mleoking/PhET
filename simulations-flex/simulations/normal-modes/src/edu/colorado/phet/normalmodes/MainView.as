
/**
 * Created by IntelliJ IDEA.
 * User: Dubson
 * Date: 6/1/11
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes {
import flash.display.Sprite;

import mx.containers.Canvas;
import mx.controls.sliderClasses.Slider;

public class MainView extends Canvas {

    var myModel: Model;
    var myView: View;
    var myPlayPauseButtons: PlayPauseButtons;
    var mySliderArrayPanel: SliderArrayPanel;
    //var ruler:VerticalRuler;
    var myControlPanel: ControlPanel;
    var phetLogo: Sprite;
    public var stageH: Number;
    public var stageW: Number;

    //Internalized strings are located at:


    public function MainView( myModel: Model, stageW: Number, stageH: Number ) {
        percentWidth = 100;
        percentHeight = 100;
        this.stageH = stageH;
        this.stageW = stageW;
        this.myModel = myModel;
        this.myView = new View( this, myModel );

        this.myView.x = 0 * stageW;
        this.myView.y = 0 * stageH;

        this.myPlayPauseButtons = new PlayPauseButtons( this, myModel );
        this.myPlayPauseButtons.x = 0.5*stageW;
        this.myPlayPauseButtons.y = 0.9 * stageH; //this.myShakerView.y + this.myPlayPauseButtons.height;

        this.mySliderArrayPanel = new SliderArrayPanel( this, this.myModel );
        this.mySliderArrayPanel.x = 0*stageW;
        this.mySliderArrayPanel.y = 0.5*stageH;

        this.myControlPanel = new ControlPanel( this, myModel );
        this.myControlPanel.x = 0.1 * stageW; //- 3 * this.myControlPanel.width;
        this.myControlPanel.y = 0.02 * stageH;


        this.phetLogo = new PhetIcon();

        this.phetLogo.x = stageW - 1.5 * this.phetLogo.width;
        this.phetLogo.y = stageH - 1.5 * this.phetLogo.height;

        this.addChild( new SpriteUIComponent( myPlayPauseButtons ) );
        this.addChild( new SpriteUIComponent ( mySliderArrayPanel ) );
        this.addChild( new SpriteUIComponent( myView ) );
        //this.addChild( new SpriteUIComponent( ruler ));
        this.addChild( myControlPanel );
        this.addChild( new SpriteUIComponent( phetLogo ) );
        this.initializeAll();

    }//end of constructor



    public function initializeAll(): void {
//        this.myControlPanel.setNbrResonatorsExternally( 1 );
//        this.myControlPanel.setResonatorIndex(1);
//        this.myControlPanel.setPresetComboBoxExternally( 3 );
//        this.myControlPanel.setGravityExternally( false );
//        this.myControlPanel.setDampingExternally( 1 );   //max is 5
//        this.myControlPanel.setRulerCheckBoxExternally( false );
        this.myView.initializeControls();
        this.myPlayPauseButtons.unPauseExternally();
        this.myPlayPauseButtons.setSliderExternally(1);
        this.myControlPanel.setNbrMassesExternally( 5 );
        //this.setNbrResonators(2);
    }

    public function positionSliderPanel():void{
        trace("MainView.positionSliderPanel  mySliderArrayPanel.x = "+this.mySliderArrayPanel.x)
       //this.mySliderArrayPanel.x = 0.5*stageW - this.mySliderArrayPanel.width/2;
    }


}//end of class
} //end of package
