
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
package edu.colorado.phet.projectilemotionflex.view {
import edu.colorado.phet.flashcommon.view.PhetIcon;
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;
import edu.colorado.phet.projectilemotionflex.control.ControlPanel;
import edu.colorado.phet.projectilemotionflex.control.SloMoStepControl;
import edu.colorado.phet.projectilemotionflex.model.TrajectoryModel;

import flash.display.Sprite;

import mx.containers.Canvas;

//main view and communications hub for Projectile Motion sim (Flex version)
public class MainView extends Canvas {

    public var phetLogo: Sprite;
    public var stageH: Number;
    public var stageW: Number;
    public var trajectoryModel: TrajectoryModel;
    public var backgroundView: BackgroundView;
    public var controlPanel: ControlPanel;
    public var sloMoStepControl: SloMoStepControl;
    private var _pixPerMeter: Number;
    private var _originXInPix: Number;
    private var _originYInPix: Number;

    //Internalized strings are located at:
    //

    public function MainView( stageW: Number, stageH: Number ) {
        percentWidth = 100;
        percentHeight = 100;
        this.stageH = stageH;
        this.stageW = stageW;
        this._pixPerMeter = stageW/30 ;   //width of stage is 40 meters
        this._originXInPix = 0.2*stageW;
        this._originYInPix = 0.8*stageH;

        this.trajectoryModel = new TrajectoryModel( this );
        this.backgroundView = new BackgroundView( this, trajectoryModel );
        this.controlPanel = new ControlPanel( this, trajectoryModel );
        this.sloMoStepControl = new SloMoStepControl( this, trajectoryModel );
        trajectoryModel.updateViews();

        this.addChild( new SpriteUIComponent( backgroundView ));
        this.addChild( controlPanel );
        this.addChild( sloMoStepControl );
        controlPanel.x = 0.8 * stageW;
        controlPanel.y = 0.05 * stageH;
        this.sloMoStepControl.x = 0.5*stageW;
        this.sloMoStepControl.y = 0.93*stageH;

        this.phetLogo = new PhetIcon();
        this.phetLogo.x = stageW - 2.0 * this.phetLogo.width;
        this.phetLogo.y = stageH - 1.5 * this.phetLogo.height;

        this.addChild( new SpriteUIComponent( phetLogo ) );
        this.initializeAll();
    }//end of constructor


    public function initializeAll(): void {
//        this.myModel.pauseSim();
        //stage.quality = StageQuality.LOW;

    }//end of initializeAll()


    public function get pixPerMeter():Number {
        return _pixPerMeter;
    }


    public function get originXInPix():Number {
        return _originXInPix;
    }

    public function set originXInPix( value:Number ):void {
        _originXInPix = value;
    }

    public function get originYInPix():Number {
        return _originYInPix;
    }

    public function set originYInPix( value:Number ):void {
        _originYInPix = value;
    }
}//end of class
} //end of package
