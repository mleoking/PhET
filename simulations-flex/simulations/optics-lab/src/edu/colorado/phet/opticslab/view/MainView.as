
/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created by IntelliJ IDEA.
 * User: Dubson
 * Date: 5/21/2014
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.opticslab.view {
import edu.colorado.phet.flashcommon.view.PhetIcon;
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;
import edu.colorado.phet.opticslab.OpticsLabCanvas;
import edu.colorado.phet.opticslab.control.ComponentDrawer;
import edu.colorado.phet.opticslab.control.ControlPanel;
import edu.colorado.phet.opticslab.model.OpticsModel;

import mx.containers.Canvas;

//main view and communications hub for Optics Lab sim;  all Views reside on the MainView

public class MainView extends Canvas {

    //private var tabBar: TabBar;       //tabBar at top of screen. In case need multiple tabs
    //private var intro_str: String;      //labels for tabs
    //private var game_str: String;
    //public var introMode: Boolean;       //true if on intro tab, false if on game tab

    public var myOpticsModel:OpticsModel;  //main model
    public var myLayoutView: LayoutView;   //View of sources and components, main play area
    public var myComponentDrawer: ComponentDrawer; //Controller/View from which user draws sources, lenses, etc
    public var myControlPanel:ControlPanel;
    public var topCanvas:OpticsLabCanvas;

    public var phetLogo: PhetIcon;
    public var stageH: Number;
    public var stageW: Number;



    public function MainView( topCanvas:OpticsLabCanvas, stageW: Number, stageH: Number ) {
        //this.topCanvas = topCanvas;   //this line is unnecessary (isn't it?)

        percentWidth = 100;
        percentHeight = 100;
        this.stageH = stageH;
        this.stageW = stageW;
        this.myOpticsModel = new OpticsModel(this);
        this.myLayoutView = new LayoutView( this, this.myOpticsModel );
        this.myComponentDrawer = new ComponentDrawer( this, this.myOpticsModel );
        this.myControlPanel = new ControlPanel( this, this.myOpticsModel );

        this.addChild( new SpriteUIComponent( this.myLayoutView ));
        myLayoutView.x = 0;
        myLayoutView.y = 0;
        this.addChild( new SpriteUIComponent( this.myComponentDrawer ));
        myComponentDrawer.x = 0.1*stageW;
        myComponentDrawer.y = 0.8*stageH;

//        this.addChild( new SpriteUIComponent( this.myUnitCircleView ));
//        this.myUnitCircleView.x = 0.3*stageW;
//        this.myUnitCircleView.y = 0.27*stageW;
//
//        this.addChild( myReadoutView );
//        this.myReadoutView.x = 0.52*stageW;
//        this.myReadoutView.y = 0.05*stageH;
//
//        this.addChild( new SpriteUIComponent( this.myGraphView ));
//        this.myGraphView.x = 0.6*stageW;
//        this.myGraphView.y = 0.8*stageH;

        this.addChild( myControlPanel );
        this.myControlPanel.x = 0.85*stageW;
        this.myControlPanel.y = 0.05*stageH;

        this.phetLogo = new PhetIcon();
        this.phetLogo.setColor( 0xffffff );
        this.phetLogo.x = stageW - 2.0 * this.phetLogo.width;
        this.phetLogo.y = stageH - 1.5 * this.phetLogo.height;
        this.addChild( new SpriteUIComponent( phetLogo ) );
        this.initializeAll();
    }//end of constructor

    private function initializeStrings(): void {
//        intro_str = FlexSimStrings.get( "intro", "Intro   " );
//        game_str = FlexSimStrings.get( "game", "Game   " );
    }

//    public function setTabView( tabView:int ):void{
//        if ( tabView == 1 ){
//
//        }else if ( tabView == 2 ){
//
//        }
//    }

    public function initializeAll(): void {
        trace("MainView.initializeAll called.")
        //myTrigModel.smallAngle = 0;

    }//end of initializeAll()



}//end of class
} //end of package
