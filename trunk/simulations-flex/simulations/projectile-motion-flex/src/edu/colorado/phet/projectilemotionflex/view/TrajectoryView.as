/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/17/12
 * Time: 9:09 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.view {
import edu.colorado.phet.projectilemotionflex.model.TrajectoryModel;

import flash.display.Graphics;

import flash.display.Sprite;
import flash.geom.Point;

public class TrajectoryView extends Sprite  {
    private var mainView: MainView;
    private var trajectoryModel: TrajectoryModel;
    private var stageW: Number;
    private var stageH: Number;
    private var trajectories_arr: Array;  //array of trajectories, each trajectory is an array of (x, y) positions
    private var trajectory_arr: Array;
    private var currentTrajectoryView: Sprite;
    private var currentX: Number;          //most recent (x, y, t) coordinates
    private var currentY: Number;
    private var currentT: Number;
    private var currentIndex: Number;
    

    public function TrajectoryView( mainView:MainView,  trajectoryModel: TrajectoryModel ) {
        this.mainView = mainView;
        this.trajectoryModel = trajectoryModel;
        this.stageW = mainView.stageW;
        this.stageH = mainView.stageH;
        this.initialize();
    }

    private function initialize():void{
        trajectoryModel.registerView( this );
        trajectory_arr = new Array();
        this.currentTrajectoryView = new Sprite();
        this.addChild( currentTrajectoryView );
    }

    public function startTrajectory():void{
        trace("TrajectoryView.startTrajectory() called") ;
        currentIndex = 0;
        currentT = 0;
        currentX = trajectoryModel.xP0;
        currentY = trajectoryModel.yP0;
        trajectory_arr[0] = new XytPoint( currentX,  currentY, currentT,  0 );
        var gT: Graphics = currentTrajectoryView.graphics;

        gT.lineStyle( 4, 0x0000ff, 1 );
        var x0: Number = trajectoryModel.xP0;
        var y0: Number = trajectoryModel.yP0;
        var xInPix: Number = x0*mainView.pixPerMeter;
        var yInPix: Number = stageH - y0*mainView.pixPerMeter;
        gT.moveTo( xInPix, yInPix );
    }

    private function addNewPointToTrajectory():void{
        var xytPoint: XytPoint = new XytPoint( currentX, currentY, currentT, currentIndex );
        trajectory_arr[ currentIndex ] = xytPoint;
        var gT: Graphics = currentTrajectoryView.graphics;
        var xInPix: Number = currentX*mainView.pixPerMeter;
        var yInPix: Number = stageH - currentY*mainView.pixPerMeter;
        gT.lineTo( xInPix,  yInPix );
    }

    public function eraseTrajectory():void{
        var gT: Graphics = currentTrajectoryView.graphics;
        gT.clear();
    }

    public function update():void{

        currentX = trajectoryModel.xP;
        currentY = trajectoryModel.yP;
        if( currentT != trajectoryModel.t ){
            currentIndex += 1;
        }
        currentT = trajectoryModel.t;
        addNewPointToTrajectory();
    }

}//end of class
}//end of package
