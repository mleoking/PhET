
/**
 * Created by IntelliJ IDEA.
 * User: Dubson
 * Date: 6/1/11
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.triglab.view {
import edu.colorado.phet.flashcommon.controls.Tab;
import edu.colorado.phet.flashcommon.controls.TabBar;
import edu.colorado.phet.triglab.*;
import edu.colorado.phet.triglab.TrigLabApplication;
import edu.colorado.phet.triglab.control.ControlPanel;
import edu.colorado.phet.triglab.model.TrigModel;

import flash.display.StageQuality;

import org.aswing.event.ModelEvent;


import edu.colorado.phet.flashcommon.view.PhetIcon;
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;

import flash.display.Sprite;

import mx.containers.Canvas;
import mx.controls.sliderClasses.Slider;

//main view and communications hub for Trig Lab
// sim
public class MainView extends Canvas {

    public var myTrigModel:TrigModel;
    public var myUnitCircleView:UnitCircleView;
    public var myGraphView:GraphView;
    public var myControlPanel:ControlPanel;
    public var topCanvas:TrigLabCanvas;

    public var phetLogo: PhetIcon;
    public var stageH: Number;
    public var stageW: Number;



    public function MainView( topCanvas:TrigLabCanvas, stageW: Number, stageH: Number ) {
        //this.topCanvas = topCanvas;   //this line is unnecessary (isn't it?)
        percentWidth = 100;
        percentHeight = 100;
        this.stageH = stageH;
        this.stageW = stageW;
        this.myTrigModel = new TrigModel(this);
        this.myUnitCircleView = new UnitCircleView( this, myTrigModel ) ;
        this.myGraphView = new GraphView(this, myTrigModel );
        this.myControlPanel = new ControlPanel( this, this.myTrigModel );

        this.addChild( new SpriteUIComponent( this.myUnitCircleView ));
        this.addChild( new SpriteUIComponent( this.myGraphView ));
        this.addChild( myControlPanel );
        this.myControlPanel.x = 0.8*stageW;
        this.myControlPanel.y = 0.02*stageH;

        this.phetLogo = new PhetIcon();
        this.phetLogo.setColor( 0x3fd7fc );
        this.phetLogo.x = stageW - 2.0 * this.phetLogo.width;
        this.phetLogo.y = stageH - 1.5 * this.phetLogo.height;
        this.addChild( new SpriteUIComponent( phetLogo ) );
        this.initializeAll();
    }//end of constructor


    public function initializeAll(): void {
//        this.myModel.pauseSim();
        //stage.quality = StageQuality.LOW;

    }//end of initializeAll()



}//end of class
} //end of package
