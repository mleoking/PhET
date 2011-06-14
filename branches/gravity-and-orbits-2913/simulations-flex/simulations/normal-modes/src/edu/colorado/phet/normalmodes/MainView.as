
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

    var oneDMode:Boolean;       //true if in 1D mode, false if in 2D mode
    var myModel1: Model1;       //model for 1D array of masses and springs
    var myModel2: Model2;       //model for 2D array of masses and springs
    var myView1: View1;         //view for Model1
    var myView2: View2;         //view for Model2

    var myPlayPauseButtons: PlayPauseButtons;
    var mySliderArrayPanel: SliderArrayPanel;
    //var ruler:VerticalRuler;
    var myControlPanel: ControlPanel;
    var phetLogo: Sprite;
    public var stageH: Number;
    public var stageW: Number;

    //Internalized strings are located at:


    public function MainView( myModel1: Model1, myModel2: Model2,  stageW: Number, stageH: Number ) {
        percentWidth = 100;
        percentHeight = 100;
        this.stageH = stageH;
        this.stageW = stageW;
        this.oneDMode = true;       //start up in 1D mode

        this.myModel1 = myModel1;
        this.myModel2 = myModel2;
        this.myModel2.stopMotion();
        this.myView1 = new View1( this, myModel1 );
        this.myView2 = new View2( this, myModel2 );

        this.myView1.x = 0 * stageW;
        this.myView1.y = 0 * stageH;
        this.myView2.x = 0 * stageW;
        this.myView2.y = 0 * stageH;

        this.myPlayPauseButtons = new PlayPauseButtons( this, myModel1 );
        this.myPlayPauseButtons.x = 0.5*stageW;
        this.myPlayPauseButtons.y = 0.9 * stageH; //this.myShakerView.y + this.myPlayPauseButtons.height;

        this.mySliderArrayPanel = new SliderArrayPanel( this, this.myModel1 );
        this.mySliderArrayPanel.x = 0*stageW;
        this.mySliderArrayPanel.y = 0.5*stageH;

        this.myControlPanel = new ControlPanel( this, myModel1, myModel2 );
        this.myControlPanel.x = 0.1 * stageW; //- 3 * this.myControlPanel.width;
        this.myControlPanel.y = 0.02 * stageH;

        this.phetLogo = new PhetIcon();

        this.phetLogo.x = stageW - 1.5 * this.phetLogo.width;
        this.phetLogo.y = stageH - 1.5 * this.phetLogo.height;

        this.addChild( new SpriteUIComponent( myPlayPauseButtons ) );
        this.addChild( new SpriteUIComponent ( mySliderArrayPanel ) );
        this.addChild( new SpriteUIComponent( myView1 ) );
        this.addChild( new SpriteUIComponent( myView2 ) );
        this.myView2.visible = false;
        //this.addChild( new SpriteUIComponent( ruler ));
        this.addChild( myControlPanel );
        this.addChild( new SpriteUIComponent( phetLogo ) );
        this.initializeAll();
    }//end of constructor

    public function set1DOr2D(oneOrTwo:int):void{
        if(oneOrTwo == 1){
            this.oneDMode = true;
            this.myModel1.startMotion();
            this.myModel2.stopMotion();
            this.myView1.visible = true;
            this.myView2.visible = false;
            this.mySliderArrayPanel.visible = true;
            this.myPlayPauseButtons.setModel( this.myModel1 );
        }else if(oneOrTwo == 2){
            this.oneDMode = false;
            this.myModel1.stopMotion();
            this.myModel2.startMotion();
            this.myView1.visible = false;
            this.myView2.visible = true;
            this.mySliderArrayPanel.visible = false;
            this.myPlayPauseButtons.setModel( this.myModel2 );
        }
    }

    public function initializeAll(): void {

        this.myView1.initializeControls();
        this.myPlayPauseButtons.unPauseExternally();
        this.myPlayPauseButtons.setSliderExternally(1);
        this.myControlPanel.setNbrMassesExternally( 5 );
        //this.setNbrResonators(2);
    }



}//end of class
} //end of package
