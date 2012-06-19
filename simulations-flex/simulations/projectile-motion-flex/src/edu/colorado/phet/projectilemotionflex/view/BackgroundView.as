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

public class BackgroundView extends Sprite {
    private var mainView: MainView;
    private var trajectoryModel: TrajectoryModel;
    private var stageW: Number;
    private var stageH: Number;
    public var container: Sprite;      //container for cannon, trajectory, projectiles, etc.  Can be zoomed
    public var cannonView: CannonView;
    public var trajectoryView: TrajectoryView;
    private var pixPerMeter: Number;   //in mainView
    private var xOrigin: Number;       //x- and y-coords of origin in screen coordinates
    private var yOrigin: Number;


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
        this.cannonView = new CannonView( mainView, trajectoryModel, this );
        this.drawBackground();
        this.addChild( container );
        this.container.addChild( cannonView );
        this.cannonView.x = trajectoryModel.xP0*mainView.pixPerMeter;
        this.cannonView.y = trajectoryModel.yP0*mainView.pixPerMeter;
    }

    private function drawBackground():void{
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

}//end class
}//end package
