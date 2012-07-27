/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/17/12
 * Time: 9:08 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.view {
import edu.colorado.phet.flashcommon.controls.NiceTextField;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;
import edu.colorado.phet.projectilemotionflex.control.SloMoStepControl;
import edu.colorado.phet.projectilemotionflex.model.TrajectoryModel;
import edu.colorado.phet.projectilemotionflex.tools.TapeMeasure;

import flash.display.GradientType;

import flash.display.Graphics;

import flash.display.Sprite;
import flash.geom.Matrix;

/**
 * backgroundView (= earth&sky ) contains readout fields for range, height, and time of projectile during flight
 * contains a zoomable container for the cannon + trajectory + projectiles
 */

public class BackgroundView extends Sprite {
    private var mainView: MainView;
    private var trajectoryModel: TrajectoryModel;
    private var stageW: Number;
    private var stageH: Number;
    private var rangeReadout: NiceTextField;
    private var heightReadout: NiceTextField;
    private var timeReadout: NiceTextField;
    private var range_str: String;             //labels for readout fields
    private var height_str: String;
    private var time_str: String;
    //private var sloMoStepControl: SloMoStepControl;
    public var container: Sprite;      //container for cannon, trajectory, projectiles, etc.  Can be zoomed
    public var cannonView: CannonView;
    public var trajectoryView: TrajectoryView;
    public var projectileView: ProjectileView;
    public var tapeMeasure: TapeMeasure;
    //private var pixPerMeter: Number;   //in mainView
    private var magFactor: Number;      //magnification/demagnification linear scale factor = root-of-2
    private var nbrMag: Number;         //number of time screen is magnified: 0 = none, -1 = mag, +1 = demag, etc
    private var _totMagF: Number;        //total magnification factor = magFactor^nbrMag
    private var _originXInPix: Number;       //x- and y-coords of origin in screen coordinates
    private var _originYInPix: Number;


    public function BackgroundView( mainView:MainView,  trajectoryModel: TrajectoryModel ) {
        this.mainView = mainView;
        this.trajectoryModel = trajectoryModel;
        this.stageW = mainView.stageW;
        this.stageH = mainView.stageH;
        //this.pixPerMeter = mainView.pixPerMeter;
        this.initialize();
    }//end constructor

    private function initialize():void{
        trajectoryModel.registerView( this );
        this.initializeStrings();
        this.rangeReadout = new NiceTextField( null, range_str, 0, 1000000, "bottom", 50, false, 1 );
        this.heightReadout = new NiceTextField( null, height_str, -100000, 100000, "bottom", 50, false, 1 );
        this.timeReadout = new NiceTextField( null, time_str, 0, 1000000, "bottom", 50, false, 1 );
        this.rangeReadout.y = this.heightReadout.y = this.timeReadout.y = 10;
        this.rangeReadout.x = 0.3*stageW;
        this.heightReadout.x = 0.4*stageW;
        this.timeReadout.x = 0.5*stageW;
//        this.sloMoStepControl = new SloMoStepControl( mainView, trajectoryModel );
//        this.sloMoStepControl.x = 0.5*stageW;
//        this.sloMoStepControl.y = 0.5*stageH;
        this.container = new Sprite();
        this.magFactor = 1.41421356;
        this.nbrMag = 0;
        this._totMagF = 1;
        this._originXInPix = mainView.originXInPix;    //must set originInPix prior to instantiating CannonView, since cannonView needs these coordinates
        this._originYInPix = mainView.originYInPix;
        this.drawBackground();
        this.addChild( rangeReadout );
        this.addChild( heightReadout );
        this.addChild( timeReadout );

        this.addChild( container );

        this.cannonView = new CannonView( mainView, trajectoryModel, this );
        this.trajectoryView = new TrajectoryView( mainView, trajectoryModel );
        this.projectileView = new ProjectileView( mainView, trajectoryModel );
        this.container.addChild( cannonView );
        this.container.addChild( trajectoryView );
        this.container.addChild( projectileView );
        this.tapeMeasure = new TapeMeasure( this.mainView );
        this.addChild( tapeMeasure );
        //this.addChild( sloMoStepControl );
        //this.tapeMeasure.makeBodyGrabbable();
        this.tapeMeasure.x = 0.8*stageW;
        this.tapeMeasure.y = 0.8*stageH;
        this.cannonView.x = this._originXInPix;
        this.cannonView.y = this._originYInPix;
    }

    private function initializeStrings():void{
        this.range_str = FlexSimStrings.get( "range(m)", "range(m)");
        this.height_str = FlexSimStrings.get( "height(m)", "height(m)");
        this.time_str = FlexSimStrings.get( "time(s)", "time(s)");
    }
    //for testing only
//    private function drawTapeMeasureMark():void{
//        var g:Graphics = this.tapeMeasure.graphics;
//        with(g){
//            lineStyle( 4, 0x0000ff );
//            beginFill( 0xffff00 );
//            drawCircle( 0, 0, 30 );
//            endFill();
//        }
//    }

    private function drawBackground():void{   //draw earth and sky
        var gB: Graphics = this.graphics;      //background graphics
        gB.clear();
        //gB.lineStyle( 1, 0x000000, 0 );
        var yHorizon = 0.7*stageH;             //level of horizon on screen, never changes
        var rectWidth: Number = 2*stageW;      //width and heigh of rectangle that sky will be drawn in
        var rectHeight: Number = yHorizon;
        var skyMatrix: Matrix = new Matrix();
        skyMatrix.createGradientBox( rectWidth, rectHeight, Math.PI/2, 0, 0);
        var colors: Array = [0x55b7ff, 0xffffff];   //sky blue to white
        var alphas: Array = [1, 1];
        var ratios: Array = [0, 255];
        gB.beginGradientFill(GradientType.LINEAR, colors, alphas, ratios, skyMatrix);
        gB.drawRect( 0, 0, rectWidth, rectHeight );

        var grdMatrix: Matrix = new Matrix();
        rectHeight = 0.3*stageH;             //height of rectangle that ground will be drawn in
        grdMatrix.createGradientBox( rectWidth, rectHeight, Math.PI/2, 0, yHorizon);
        colors= [0x01741E, 0x1afb1a];    //dark green to light green
        alphas = [1, 1];
        ratios = [0, 255];
        gB.beginGradientFill(GradientType.LINEAR, colors, alphas, ratios, grdMatrix);
        gB.drawRect( 0, yHorizon, rectWidth,  rectHeight )
        gB.endFill()
    }

    /**
     * Called when user clicks on magnify button in control panel,
     * increases magnification of container by magFactor = root-of-2
     */
    public function magnify():void{
        //trace("backgroundView.magnify called");
        nbrMag -= 1;
        totMagF = Math.pow( magFactor, nbrMag );  //total magnification factor = 2^0.5^nbrMag
        container.scaleX *= magFactor;
        container.scaleY *= magFactor;
        container.x = _originXInPix*( 1.0 - 1/totMagF );
        container.y = _originYInPix*( 1.0 - 1/totMagF );

        //reset pixPerMeter
        mainView.pixPerMeter = mainView.pixPerMeterUnZoomed/totMagF;

        //must update tapeMeasure
        mainView.backgroundView.tapeMeasure.resetReadout();
        //must reset linewidths on trajectories
    }

    //called when user clicks on demagnify button in control panel
    //decreases magnification of container by magFactor = root-of-2
    public function demagnify():void{
        //trace("backgroundView.demagnify called");
        nbrMag += 1;
        totMagF = Math.pow( magFactor, nbrMag );  //total magnification factor = 2^0.5^nbrMag
        container.scaleX *= 1/magFactor;
        container.scaleY *= 1/magFactor;
        container.x = _originXInPix*( 1.0 - 1/totMagF );
        container.y = _originYInPix*( 1.0 - 1/totMagF );
        //reset pixPerMeter
        mainView.pixPerMeter = mainView.pixPerMeterUnZoomed/totMagF;
        //must update tapeMeasure
        mainView.backgroundView.tapeMeasure.resetReadout();
        //must reset linewidths on trajectories
    }

    public function update():void{
        if( trajectoryModel.updateReadoutsNow ){
            this.rangeReadout.setVal( trajectoryModel.xP );
            this.heightReadout.setVal( trajectoryModel.yP );
            this.timeReadout.setVal( trajectoryModel.t );
            trajectoryModel.updateReadoutsNow = false;
        }
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


    public function get totMagF():Number {
        return _totMagF;
    }

    public function set totMagF(value:Number):void {
        _totMagF = value;
    }
}//end class
}//end package
