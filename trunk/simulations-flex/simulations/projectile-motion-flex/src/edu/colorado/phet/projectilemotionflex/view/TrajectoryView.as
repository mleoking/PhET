/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/17/12
 * Time: 9:09 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.view {
import edu.colorado.phet.projectilemotionflex.model.TrajectoryModel;

import flash.display.Sprite;

public class TrajectoryView extends Sprite  {
    private var mainView: MainView;
    private var trajectoryModel: TrajectoryModel;
    private var stageW: Number;
    private var stageH: Number;
    private var trajectories_arr: Array;  //array of trajectories, each trajectory is an array of (x, y) positions
    private var trajectory_arr: Array;
    private var trajectoryView: Sprite;
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
        this.trajectoryView = new Sprite();
        this.addChild( trajectoryView );
    }

    private function startTrajectory():void{
        currentIndex = 0;

    }

    private function addNewPointToTrajectory():void{
         var xytPoint: XytPoint = new XytPoint( currentX, currentY, currentT );
    }

    private function eraseTrajectory():void{

    }

    public function update():void{
        currentX = trajectoryModel.xP;
        currentY = trajectoryModel.yP;
        currentT = trajectoryModel.t;
        addNewPointToTrajectory();
    }

}//end of class
}//end of package
