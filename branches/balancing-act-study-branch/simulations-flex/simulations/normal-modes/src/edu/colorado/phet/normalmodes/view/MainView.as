/*
 * Copyright 2002-2012, University of Colorado
 */

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
import edu.colorado.phet.flashcommon.view.PhetIcon;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;
import edu.colorado.phet.normalmodes.control.ButtonArrayPanel;
import edu.colorado.phet.normalmodes.control.ControlPanel;
import edu.colorado.phet.normalmodes.control.SliderArrayPanel;
import edu.colorado.phet.normalmodes.model.Model1D;
import edu.colorado.phet.normalmodes.model.Model2D;

import flash.display.Sprite;

import mx.containers.Canvas;

/*
*Class MainView initializes all views and controllers, and creates layout of Views.
*Acts as communications hub for sim.  Every model, view, and controller has an instance of MainView.
*Internationalized strings are always set with intializeStrings() method in:
*MainView, ControlPanel, PausedSign, SliderArrayPanel, SloMoStepControl, PolarizationPanel, ButtonArrayPanel
*/
public class MainView extends Canvas {
    private var tabBar: TabBar;        //tabBar at top of screen
    private var oneDimension_str: String;    //labels for tabs
    private var twoDimensions_str: String;

    public var oneDMode: Boolean;       //true if in 1D mode, false if in 2D mode
    public var myModel1D: Model1D;       //model for 1D array of masses and springs
    public var myModel2D: Model2D;       //model for 2D array of masses and springs
    public var myView1D: View1D;         //view for Model1D
    public var myView1DModes: View1DModes; //another view for Model1D, showing individual modes
    public var myView2D: View2D;         //view for Model2D

    public var mySliderArrayPanel: SliderArrayPanel;
    public var myButtonArrayPanel: ButtonArrayPanel;
    public var myControlPanel: ControlPanel;
    public var myPausedSign: PausedSign;
    public var phetLogo: Sprite;
    public var stageH: Number;
    public var stageW: Number;


    public function MainView( stageW: Number, stageH: Number ) {
        percentWidth = 100;
        percentHeight = 100;
        this.stageH = stageH;
        this.stageW = stageW;
        this.oneDMode = true;       //start up in 1D mode

        var oneDHolder: Canvas = new Canvas();
        var twoDHolder: Canvas = new Canvas();
        addChild( oneDHolder );
        addChild( twoDHolder );

        this.initializeStrings();
        tabBar = new TabBar();
        var oneDTab: Tab = new Tab( oneDimension_str, tabBar );
        var twoDTab: Tab = new Tab( twoDimensions_str, tabBar );
        tabBar.addTab( oneDTab );
        tabBar.addTab( twoDTab );

        tabBar.selectedTab = oneDTab;

        tabBar.addListener( function (): void {
            if ( tabBar.selectedTab == oneDTab ) {
                set1DOr2D( 1 );
            }
            else { // advanced tab
                set1DOr2D( 2 );
            }
        } );

        this.addChild( new SpriteUIComponent( tabBar ) );
        //Model
        this.myModel1D = new Model1D();
        this.myModel2D = new Model2D();
        this.myModel2D.stopMotion();
        this.myView1D = new View1D( this, myModel1D );
        this.myView1DModes = new View1DModes( this, myModel1D );
        this.myView2D = new View2D( this, myModel2D );

        this.myView1D.x = 0;
        this.myView1D.y = 0;
        this.myView1DModes.x = 0.83 * stageW;
        this.myView1DModes.y = 0.55 * stageH;
        this.myView2D.x = 0;
        this.myView2D.y = 0;

        this.mySliderArrayPanel = new SliderArrayPanel( this, this.myModel1D );
        this.mySliderArrayPanel.x = 0;
        this.mySliderArrayPanel.y = 0.5 * stageH;

        this.myControlPanel = new ControlPanel( this, myModel1D );
        this.myControlPanel.x = 0.83 * stageW; //- 3 * this.myControlPanel.width;
        this.myControlPanel.y = 0.075 * stageH;

        this.myButtonArrayPanel = new ButtonArrayPanel( this, this.myModel2D );
        this.myButtonArrayPanel.x = 0.70 * stageW;
        this.myButtonArrayPanel.y = 0.45 * stageH; //this.myControlPanel.y + this.myControlPanel.height + 20;
        this.myButtonArrayPanel.visible = false;

        this.myPausedSign = new PausedSign( this.myModel1D );
        this.myPausedSign.x = 0.36 * stageW;
        this.myPausedSign.y = 0.1 * stageH;

        this.phetLogo = new PhetIcon();
        this.phetLogo.x = stageW - 2.0 * this.phetLogo.width;
        this.phetLogo.y = 0;// stageH - 1.5 * this.phetLogo.height;

        this.addChild( myPausedSign );
        this.addChild( mySliderArrayPanel );
        this.addChild( new SpriteUIComponent( myView1D ) );
        this.addChild( new SpriteUIComponent( myView1DModes ) );
        this.addChild( new SpriteUIComponent( myView2D ) );
        this.myView2D.visible = false;
        this.addChild( myControlPanel );
        this.addChild( myButtonArrayPanel ); //new SpriteUIComponent( myButtonArrayPanel ) );

        //phetLogo is in tab area
        this.addChild( new SpriteUIComponent( phetLogo ) );
        this.initializeAll();
    }//end of constructor

    private function initializeStrings(): void {
        oneDimension_str = FlexSimStrings.get( "one Dimension", "1 Dimension   " );
        twoDimensions_str = FlexSimStrings.get( "two Dimensions", "2 Dimensions   " );
    }

    /*
     * This sim has two models, with accompanying views: Model1D = a 1D array of coupled masses and Model2D = a 2D array of coupled masses
     * The tab bar controls which model/view the user accesses/sees by calling the set1DOr2D function.
     * */

    public function set1DOr2D( oneOrTwo: int ): void {
        if ( oneOrTwo == 1 ) {
            this.myModel2D.interruptSim();
            this.myModel1D.resumeSim();
            this.myModel1D.updateViews();
            this.myPausedSign.x = 0.36 * stageW;
            this.oneDMode = true;
            this.myView1D.visible = true;
            this.myView1DModes.visible = true;
            this.myView2D.visible = false;
            this.mySliderArrayPanel.visible = true;
            this.myButtonArrayPanel.visible = false;
            this.myControlPanel.setModel( this.myModel1D );
            this.myControlPanel.mySloMoStepControl.setModel( this.myModel1D );
            this.myPausedSign.setModel( this.myModel1D );
            this.myControlPanel.setNbrMassesExternallyWithNoAction( this.myModel1D.N );
            this.myControlPanel.x = 0.83 * stageW;

        }
        else if ( oneOrTwo == 2 ) {
            this.myModel1D.interruptSim();
            this.myModel2D.resumeSim();
            this.myModel2D.updateViews();
            this.myPausedSign.x = 0.31 * stageW;
            this.oneDMode = false;
            this.myView1D.visible = false;
            this.myView1DModes.visible = false;
            this.myView2D.visible = true;
            this.mySliderArrayPanel.visible = false;
            this.myButtonArrayPanel.visible = true;
            this.myControlPanel.setModel( this.myModel2D );
            this.myControlPanel.mySloMoStepControl.setModel( this.myModel2D );
            this.myPausedSign.setModel( this.myModel2D );
            this.myControlPanel.setNbrMassesExternallyWithNoAction( this.myModel2D.N );
            this.myControlPanel.x = 0.78 * stageW;
        }
        this.myControlPanel.setShowPhasesControl();
        this.myControlPanel.initializeStartStopButton();
    }//end set1DOr2D

    public function initializeAll(): void {
        this.myModel1D.pauseSim();
        this.myModel2D.pauseSim();
        this.myModel1D.interrupted = false;
        this.myModel2D.interrupted = false;
        this.myView1D.initializeControls();
        this.myControlPanel.mySloMoStepControl.unPauseExternally();
        this.myControlPanel.mySloMoStepControl.setSliderExternally( 1 );
        this.myControlPanel.setNbrMassesExternally( 3 );    //this initializes Model1D
        this.myModel2D.setN( 2 );                            //this initializes Model2D
        this.mySliderArrayPanel.drawBoundingBox();
        this.myButtonArrayPanel.initializeButtonArray();
    }


}//end of class
} //end of package
