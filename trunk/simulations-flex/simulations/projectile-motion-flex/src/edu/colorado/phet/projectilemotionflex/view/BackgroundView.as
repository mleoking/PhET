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

    public function BackgroundView( mainView:MainView,  trajectoryModel: TrajectoryModel ) {
        this.mainView = mainView;
        this.trajectoryModel = trajectoryModel;
        this.stageW = mainView.stageW;
        this.stageH = mainView.stageH;
        this.initialize();
    }//end constructor

    private function initialize():void{
        this.drawBackground();
    }

    private function drawBackground():void{
        var gB: Graphics = this.graphics;
        gB.clear();
        gB.lineStyle( 1, 0x000000, 1 );
        var yHorizon = 0.7*stageH;
        gB.moveTo( 0, yHorizon);
        gB.lineTo( stageW,  yHorizon );
        gB.beginFill( 0xbbbbff,  1);     //sky blue
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

}//end class
}//end package
