/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created with IntelliJ IDEA.
 * User: Dubson
 * Date: 6/17/12
 * Time: 9:09 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.view {
import edu.colorado.phet.projectilemotionflex.model.TrajectoryModel;

import flash.display.Graphics;

import flash.display.Sprite;
import flash.geom.Point;
import flash.utils.getTimer;

public class TrajectoryView extends Sprite {
    private var mainView: MainView;
    private var trajectoryModel: TrajectoryModel;
    private var stageW: Number;
    private var stageH: Number;
    private var originXInPix: Number;
    private var originYInPix: Number;

    private var trajectories_arr: Array;  //array of trajectories, each trajectory is an array of (x, y, t) positions
    private var trajectory_arr: Array;
    private var currentTrajectoryView: Sprite;
    private var currentTicMarkView: Sprite;
    //private var ticMark: Sprite;
    private var nextTicMarkTime: int;     //time in seconds of the next 1-second interval tic mark on the trajectory
//    private var x0InPix: Number;
//    private var y0InPix: Number;
    private var currentX: Number;          //most recent (x, y, t) coordinates in meters and seconds
    private var currentY: Number;
    private var currentT: Number;
    private var currentIndex: int;      //index for individual points along trajectory
    private var ticIndex: int;          //index for 1-s tic marks on trajectory
    

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
        //this.ticMark = new Sprite()
        //this.drawTicMark();
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
        if( trajectoryModel.airResistance ){
            gT.lineStyle( 4, 0xff0000, 1 );
        }else{
            gT.lineStyle( 4, 0x0000ff, 1 );
        }

        var x0: Number = trajectoryModel.xP0;
        var y0: Number = trajectoryModel.yP0;
        var x0InPix: Number = this.originXInPix + x0*mainView.pixPerMeter;
        var y0InPix: Number = this.originYInPix - y0*mainView.pixPerMeter;
        gT.moveTo( x0InPix, y0InPix );
    }

    public function updateTrajectoryColor():void{
        var gT: Graphics = currentTrajectoryView.graphics;
        if( trajectoryModel.airResistance ){
            gT.lineStyle( 4, 0xff0000, 1 );
        }else{
            gT.lineStyle( 4, 0x0000ff, 1 );
        }
    }

    private function addNewPointToTrajectory():void{
        var xytPoint: XytPoint = new XytPoint( currentX, currentY, currentT, currentIndex );
        trajectory_arr[ currentIndex ] = xytPoint;
        var gT: Graphics = currentTrajectoryView.graphics;
        var xInPix: Number = this.originXInPix + currentX*mainView.pixPerMeterUnZoomed;
        var yInPix: Number = this.originYInPix - currentY*mainView.pixPerMeterUnZoomed;
        gT.lineTo( xInPix,  yInPix );
    }

    private function drawTicMarkOnTrajectory( delT: Number ):void{
        var gM: Graphics = currentTicMarkView.graphics;
        currentX -= trajectoryModel.vX*delT;
        currentY -= trajectoryModel.vY*delT;
        var xInPix: Number = this.originXInPix + currentX*mainView.pixPerMeterUnZoomed;
        var yInPix: Number = this.originYInPix - currentY*mainView.pixPerMeterUnZoomed;
        gM.lineStyle( 4, 0x000000, 1 );
        var ticRadius = 10;
        gM.moveTo( xInPix - ticRadius, yInPix );
        gM.lineTo( xInPix + ticRadius, yInPix );
        gM.moveTo( xInPix, yInPix - ticRadius );
        gM.lineTo( xInPix, yInPix + ticRadius );
    }

//    private function drawTicMark():void{
//        var g: Graphics = ticMark.graphics;
//        var ticRadius: Number = 10;
//        with(g){
//            lineStyle( 4, 0x000000, 1 );
//            moveTo( 0 - ticRadius, 0 );
//            lineTo( 0 + ticRadius, 0 );
//            moveTo( 0, 0 - ticRadius );
//            lineTo( 0, 0 + ticRadius );
//        }
//    }

    public function eraseTrajectory():void{
        var gT: Graphics = currentTrajectoryView.graphics;
        gT.clear();
        var gM: Graphics = currentTicMarkView.graphics;
        gM.clear();
    }

    public function update():void{

        currentX = trajectoryModel.xP;         //P for projectile
        currentY = trajectoryModel.yP;
        //trace("TrajectoryView.update. currentY = "  + currentY );
        if( currentT != trajectoryModel.t ){
            currentIndex += 1;
        }
        currentT = trajectoryModel.t;
        addNewPointToTrajectory();
        if( trajectoryModel.drawTicMarkNow ){
            var delT: Number = currentT - trajectoryModel.ticMarkTime;
            drawTicMarkOnTrajectory( delT );
            trajectoryModel.drawTicMarkNow = false;
        }
        //trace( "trajectoryView.update  currentX = " + currentX) ;
    }

}//end of class
}//end of package
