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

public class TrajectoryView extends Sprite {
    private var mainView: MainView;
    private var trajectoryModel: TrajectoryModel;
    private var stageW: Number;
    private var stageH: Number;
    private var originXInPix: Number;
    private var originYInPix: Number;

    private var trajectories_arr: Array;  //array of trajectories, each trajectory is an array of (x, y) positions
    private var trajectory_arr: Array;
    private var currentTrajectoryView: Sprite;
    private var currentTicMarkView: Sprite;
    private var nextTicMarkTime: int;     //time in seconds of the previous 1-second interval tic mark on the trajectory
//    private var x0InPix: Number;
//    private var y0InPix: Number;
    private var currentX: Number;          //most recent (x, y, t) coordinates in meters and seconds
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
        //trace("TrajectoryView.initialize  mainView = " + this.mainView );
        //trace("TrajectoryView.initialize  mainView.backgroundView = " + this.mainView.backgroundView );
        //trace("TrajectoryView.initialize  mainView.backgroundView.originXInPix = " + this.mainView.backgroundView.originXInPix );
        this.originXInPix = this.mainView.originXInPix;
        this.originYInPix = this.mainView.originYInPix;
        //trace("TrajectoryView.initialize  originYInPix = " + originYInPix );
        trajectoryModel.registerView( this );
        trajectory_arr = new Array();
        this.currentTrajectoryView = new Sprite();
        this.currentTicMarkView = new Sprite();
        this.addChild( currentTrajectoryView );
        this.addChild( currentTicMarkView );
        //TEST
//        var g:Graphics = this.graphics;
//        g.lineStyle( 10, 0x000000, 1 );
//        g.drawCircle( 0, 0, 100 );
    }

    public function startTrajectory():void{
        //trace("TrajectoryView.startTrajectory() called") ;
        currentIndex = 0;
        currentT = 0;
        nextTicMarkTime = 1;
//        currentX = trajectoryModel.xP0;
//        currentY = trajectoryModel.yP0;
        trajectory_arr[0] = new XytPoint( currentX,  currentY, currentT,  0 );
        var gT: Graphics = currentTrajectoryView.graphics;
        gT.lineStyle( 4, 0x0000ff, 1 );
        var x0: Number = trajectoryModel.xP0;
        var y0: Number = trajectoryModel.yP0;
        var x0InPix: Number = this.originXInPix + x0*mainView.pixPerMeter;
        var y0InPix: Number = this.originYInPix - y0*mainView.pixPerMeter;
        gT.moveTo( x0InPix, y0InPix );
    }

    private function addNewPointToTrajectory():void{
        var xytPoint: XytPoint = new XytPoint( currentX, currentY, currentT, currentIndex );
        trajectory_arr[ currentIndex ] = xytPoint;
        var gT: Graphics = currentTrajectoryView.graphics;
        var xInPix: Number = this.originXInPix + currentX*mainView.pixPerMeter;
        var yInPix: Number = this.originYInPix - currentY*mainView.pixPerMeter;
        gT.lineTo( xInPix,  yInPix );
        //trace("TrajectoryView.addNewPoint  yInPix = " + yInPix );
//        this.x = this.originXInPix + this.trajectoryModel.xP0*mainView.pixPerMeter;
//        this.y = this.originYInPix - this.trajectoryModel.yP0*mainView.pixPerMeter;
    }

    private function drawTicMarkOnTrajectory():void{
        var gM: Graphics = currentTicMarkView.graphics;
        var xInPix: Number = this.originXInPix + currentX*mainView.pixPerMeter;
        var yInPix: Number = this.originYInPix - currentY*mainView.pixPerMeter;
        gM.lineStyle( 4, 0x000000, 1 );
        var ticRadius = 10;
        gM.moveTo( xInPix - ticRadius, yInPix );
        gM.lineTo( xInPix + ticRadius, yInPix );
        gM.moveTo( xInPix, yInPix - ticRadius );
        gM.lineTo( xInPix, yInPix + ticRadius )
    }

    public function eraseTrajectory():void{
        var gT: Graphics = currentTrajectoryView.graphics;
        gT.clear();
        var gM: Graphics = currentTicMarkView.graphics;
        gM.clear();
    }

    public function update():void{

        currentX = trajectoryModel.xP;         //P for projectile
        currentY = trajectoryModel.yP;
        if( currentT != trajectoryModel.t ){
            currentIndex += 1;
        }
        currentT = trajectoryModel.t;
        addNewPointToTrajectory();
        if( currentT > nextTicMarkTime ){
            drawTicMarkOnTrajectory();
            nextTicMarkTime += 1;
            trace("TrajectoryView.update  currentT = " + currentT );
        }
        //trace( "trajectoryView.update  currentX = " + currentX) ;
    }

}//end of class
}//end of package
