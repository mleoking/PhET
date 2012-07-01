/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/17/12
 * Time: 9:08 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.view {
import edu.colorado.phet.projectilemotionflex.model.TrajectoryModel;

import flash.display.Graphics;

import flash.display.Sprite;

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
    private var pixPerMeter: Number;   //in mainView
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
        this.cannonView.x = this._originXInPix;
        this.cannonView.y = this._originYInPix;
    }

    private function drawBackground():void{   //draw earth and sky
        var gB: Graphics = this.graphics;
        gB.clear();
        gB.lineStyle( 1, 0x000000, 1 );
        var yHorizon = 0.7*stageH;
        gB.moveTo( 0, yHorizon);
        gB.lineTo( stageW,  yHorizon );
        gB.beginFill( 0xddddff,  1);     //sky blue
        gB.moveTo( 0, 0 );
        gB.lineTo( 0, yHorizon );
        gB.lineTo( stageW,  yHorizon );
        gB.lineTo( stageW,  0 );
        gB.lineTo( 0, 0 );
        gB.endFill()
        gB.beginFill( 0xaaffaa,  1);     //grass green
        gB.moveTo( 0, yHorizon );
        gB.lineTo( 0, stageH );
        gB.lineTo( stageW,  stageH );
        gB.lineTo( stageW,  yHorizon );
        gB.lineTo( 0, yHorizon );
        gB.endFill()
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
