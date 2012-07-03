/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/17/12
 * Time: 9:08 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.view {
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;
import edu.colorado.phet.projectilemotionflex.model.TrajectoryModel;
import edu.colorado.phet.projectilemotionflex.tools.TapeMeasure;

import flash.display.GradientType;

import flash.display.Graphics;

import flash.display.Sprite;
import flash.geom.Matrix;

//backgroundView (= earth&sky ) contains a zoomable container for the cannon + trajectory + projectiles
public class BackgroundView extends Sprite {
    private var mainView: MainView;
    private var trajectoryModel: TrajectoryModel;
    private var stageW: Number;
    private var stageH: Number;
    public var container: Sprite;      //container for cannon, trajectory, projectiles, etc.  Can be zoomed
    public var cannonView: CannonView;
    public var trajectoryView: TrajectoryView;
    public var projectileView: ProjectileView;
    public var tapeMeasure: TapeMeasure;
    private var pixPerMeter: Number;   //in mainView
    private var magFactor: Number;      //magnification/demagnification linear scale factor = root-of-2
    private var nbrMag: Number;         //number of time screen is magnified: 0 = none, -1 = mag, +1 = demag, etc
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
        this.container = new Sprite();
        this.magFactor = 1.41421356;
        this.nbrMag = 0;
        this._originXInPix = mainView.originXInPix;    //must set originInPix prior to instantiating CannonView, since cannonView needs these coordinates
        this._originYInPix = mainView.originYInPix;
        this.drawBackground();
        this.addChild( container );
        this.cannonView = new CannonView( mainView, trajectoryModel, this );
        this.trajectoryView = new TrajectoryView( mainView, trajectoryModel );
        this.projectileView = new ProjectileView( mainView, trajectoryModel );
        this.container.addChild( cannonView );
        this.container.addChild( trajectoryView );
        this.container.addChild( projectileView );
        this.tapeMeasure = new TapeMeasure( this.mainView );
        this.addChild( tapeMeasure );
        //this.tapeMeasure.makeBodyGrabbable();
        this.tapeMeasure.x = 0.5*stageW;
        this.tapeMeasure.y = 0.8*stageH;
        this.cannonView.x = this._originXInPix;
        this.cannonView.y = this._originYInPix;
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
        var gB: Graphics = this.graphics;
        gB.clear();
        //gB.lineStyle( 1, 0x000000, 0 );
        var yHorizon = 0.7*stageH;
        var rectWidth: Number = 2*stageW;
        var rectHeight: Number = yHorizon;
        var skyMatrix: Matrix = new Matrix();
        skyMatrix.createGradientBox( rectWidth, rectHeight, Math.PI/2, 0, 0);
        var colors: Array = [0x55b7ff, 0xffffff];   //sky blue to white
        var alphas: Array = [1, 1];
        var ratios: Array = [0, 255];
        gB.beginGradientFill(GradientType.LINEAR, colors, alphas, ratios, skyMatrix);
        gB.drawRect( 0, 0, rectWidth, rectHeight );

        var grdMatrix: Matrix = new Matrix();
        rectHeight = 0.3*stageH;
        grdMatrix.createGradientBox( rectWidth, rectHeight, Math.PI/2, 0, yHorizon);
        colors= [0x01741E, 0x1afb1a];    //dark green to light green
        alphas = [1, 1];
        ratios = [0, 255];
        gB.beginGradientFill(GradientType.LINEAR, colors, alphas, ratios, grdMatrix);
        gB.drawRect( 0, yHorizon, rectWidth,  rectHeight )
        gB.endFill()
    }

    public function magnify():void{
        //trace("backgroundView.magnify called");
        nbrMag -= 1;
        var magF: Number = Math.pow( magFactor, nbrMag );  //total magnification factor = 2^0.5^nbrMag
        container.scaleX *= magFactor;
        container.scaleY *= magFactor;
        container.x = _originXInPix*( 1.0 - 1/magF );
        container.y = _originYInPix*( 1.0 - 1/magF );
        //must update tapeMeasure
        //must reset linewidths on trajectories
    }

    public function demagnify():void{
        //trace("backgroundView.demagnify called");
        nbrMag += 1;
        var magF: Number = Math.pow( magFactor, nbrMag );  //total magnification factor = 2^0.5^nbrMag
        container.scaleX *= 1/magFactor;
        container.scaleY *= 1/magFactor;
        container.x = _originXInPix*( 1.0 - 1/magF );
        container.y = _originYInPix*( 1.0 - 1/magF );
        //must update tapeMeasure
        //must reset linewidths on trajectories
    }

    public function update():void{
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
}//end class
}//end package
