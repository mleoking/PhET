
/**
 * Created by IntelliJ IDEA.
 * User: Dubson
 * Date: 6/1/11
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes.view {
import edu.colorado.phet.flashcommon.controls.Tab;
import edu.colorado.phet.flashcommon.controls.TabBar;
import edu.colorado.phet.normalmodes.*;
import edu.colorado.phet.normalmodes.control.ButtonArrayPanel;
import edu.colorado.phet.normalmodes.control.ControlPanel;
import edu.colorado.phet.normalmodes.control.PolarizationPanel;
import edu.colorado.phet.normalmodes.control.SloMoStepControl;
import edu.colorado.phet.normalmodes.control.SliderArrayPanel;
import edu.colorado.phet.normalmodes.model.Model1;
import edu.colorado.phet.normalmodes.model.Model2;
import edu.colorado.phet.normalmodes.util.PhetIcon;
import edu.colorado.phet.normalmodes.util.SpriteUIComponent;

import flash.display.Sprite;

import mx.containers.Canvas;
import mx.controls.sliderClasses.Slider;

public class MainView extends Canvas {
    private var tabBar: TabBar;

    public var oneDMode:Boolean;       //true if in 1D mode, false if in 2D mode
    public var myModel1: Model1;       //model for 1D array of masses and springs
    public var myModel2: Model2;       //model for 2D array of masses and springs
    public var myView1: View1;         //view for Model1
    public var myView2: View2;         //view for Model2

    public var mySloMoStepControl: SloMoStepControl;
    public var mySliderArrayPanel: SliderArrayPanel;
    public var myButtonArrayPanel: ButtonArrayPanel;
    public var myControlPanel: ControlPanel;
    //public var myPolarizationPanel:PolarizationPanel;
    public var myPausedSign: PausedSign;
    public var phetLogo: Sprite;
    public var stageH: Number;
    public var stageW: Number;

    //Internalized strings are located at:
    //ControlPanel, PausedSign, SliderArrayPanel, SloMoStepControl, PolarizationPanel, ButtonArrayPanel

    public function MainView( stageW: Number, stageH: Number ) {
        percentWidth = 100;
        percentHeight = 100;
        this.stageH = stageH;
        this.stageW = stageW;
        this.oneDMode = true;       //start up in 1D mode

        var oneDHolder:Canvas = new Canvas();
        var twoDHolder:Canvas = new Canvas();
        addChild( oneDHolder );
        addChild( twoDHolder  );

        tabBar = new TabBar();
        var oneDTab: Tab = new Tab( "1 Dimension  ", tabBar );
        var twoDTab: Tab = new Tab( "2 Dimensions  ", tabBar );
        tabBar.addTab( oneDTab );
        tabBar.addTab( twoDTab );

        tabBar.selectedTab = oneDTab;

        tabBar.addListener( function(): void {
            if ( tabBar.selectedTab == oneDTab ) {
                set1DOr2D( 1 );
            }
            else { // advanced tab
                set1DOr2D( 2 );
            }
        } );

        this.addChild( new SpriteUIComponent( tabBar ));

        this.myModel1 = new Model1( ) ;
        this.myModel2 = new Model2( this );
        this.myModel2.stopMotion();
        this.myView1 = new View1( this, myModel1 );
        this.myView2 = new View2( this, myModel2 );

        this.myView1.x = 0 * stageW;
        this.myView1.y = 0 * stageH;
        this.myView2.x = 0 * stageW;
        this.myView2.y = 0 * stageH;

        this.mySloMoStepControl = new SloMoStepControl( this, myModel1 );
        this.mySloMoStepControl.x = 0.93 * stageW ;           // hard-coded, unfortunately
        this.mySloMoStepControl.y = 0.42 * stageH; //this.myShakerView.y + this.myPlayPauseButtons.height;
        //trace("MainView:  "+this.myPlayPauseButtons.width )
        this.mySliderArrayPanel = new SliderArrayPanel( this, this.myModel1 );
        this.mySliderArrayPanel.x = 0*stageW;
        this.mySliderArrayPanel.y = 0.5*stageH;

        this.myControlPanel = new ControlPanel( this, myModel1 );
        this.myControlPanel.x = 0.83 * stageW; //- 3 * this.myControlPanel.width;
        this.myControlPanel.y = 0.075 * stageH;

        //this.myPolarizationPanel = new PolarizationPanel( this, myModel1 );
        //this.locatePolarizationPanel( 1 );

        this.myButtonArrayPanel =  new ButtonArrayPanel( this, this.myModel2 );
        this.myButtonArrayPanel.x = 0.70*stageW;
        this.myButtonArrayPanel.y = 0.50*stageH; //this.myControlPanel.y + this.myControlPanel.height + 20;
        this.myButtonArrayPanel.visible = false;

        this.myPausedSign = new PausedSign( this.myModel1 );
        this.myPausedSign.x = 0.32*stageW;
        this.myPausedSign.y = 0.1*stageH;

        this.phetLogo = new PhetIcon();

        this.phetLogo.x = stageW - 2.0 * this.phetLogo.width;
        this.phetLogo.y = 0;// stageH - 1.5 * this.phetLogo.height;

        this.addChild( this.mySloMoStepControl );
        this.addChild( myPausedSign );
        this.addChild( mySliderArrayPanel );
        //this.addChild( new SpriteUIComponent ( mySliderArrayPanel ) );
        this.addChild( new SpriteUIComponent( myView1 ) );
        this.addChild( new SpriteUIComponent( myView2 ) );
        this.myView2.visible = false;
        this.addChild( myControlPanel );
        //this.addChild( myPolarizationPanel );
        this.addChild( myButtonArrayPanel); //new SpriteUIComponent( myButtonArrayPanel ) );

        //phetLogo now in tab area
        this.addChild( new SpriteUIComponent( phetLogo ) );
        this.initializeAll();
    }//end of constructor

    /*
    private function locatePolarizationPanel( oneDOr2D:int ):void{
        if( oneDOr2D == 1 ){
            this.myPolarizationPanel.x = 0.8*stageW;
            this.myPolarizationPanel.y = 0.5*stageH;
        }else{
            this.myPolarizationPanel.x = 0.8*stageW;
            this.myPolarizationPanel.y = 0.5*stageH;
        }
    }//end locatePolarizationPanel
    */


    public function set1DOr2D(oneOrTwo:int):void{
        this.myModel1.pauseSim();
        this.myModel2.pauseSim();
        this.myControlPanel.initializeStartStopButton();
        if(oneOrTwo == 1){
            this.oneDMode = true;
            this.myView1.visible = true;
            this.myView2.visible = false;
            this.mySliderArrayPanel.visible = true;
            this.myButtonArrayPanel.visible = false;
            this.myControlPanel.setModel( this.myModel1 );
            this.mySloMoStepControl.setModel( this.myModel1 );
            this.myPausedSign.setModel( this.myModel1 );
            this.myControlPanel.setNbrMassesExternally( this.myModel1.N );
            this.myControlPanel.showPhasesVisible( true );
        }else if(oneOrTwo == 2){
            this.oneDMode = false;
            this.myView1.visible = false;
            this.myView2.visible = true;
            this.mySliderArrayPanel.visible = false;
            this.myButtonArrayPanel.visible = true;
            this.myControlPanel.setModel( this.myModel2 );
            this.mySloMoStepControl.setModel( this.myModel2 );
            this.myPausedSign.setModel( this.myModel2 );
            this.myControlPanel.setNbrMassesExternally( this.myModel2.N );
            this.myControlPanel.showPhasesVisible( false );
        }
    }//end set1DOr2D

    public function initializeAll(): void {
        this.myView1.initializeControls();
        this.mySloMoStepControl.unPauseExternally();
        this.mySloMoStepControl.setSliderExternally(1);
        this.myControlPanel.setNbrMassesExternally( 5 );
        //this.myModel1.setTorL( "T" );
        //this.setNbrResonators(2);
    }



}//end of class
} //end of package
