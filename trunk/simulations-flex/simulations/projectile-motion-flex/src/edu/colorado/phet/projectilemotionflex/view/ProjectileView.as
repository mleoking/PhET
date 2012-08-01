/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created with IntelliJ IDEA.
 * User: Dubson
 * Date: 6/17/12
 * Time: 9:10 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.view {
import edu.colorado.phet.projectilemotionflex.model.Projectile;
import edu.colorado.phet.projectilemotionflex.model.MainModel;

import flash.display.DisplayObjectContainer;

import flash.display.Graphics;

import flash.display.Sprite;

// a single view for all 10 projectiles, view properties change when projectile changes
public class ProjectileView extends Sprite {
    private var mainView: MainView;
    private var trajectoryModel: MainModel;
//    private var stageW: Number;
//    private var stageH: Number;
    private var originXInPix: Number;
    private var originYInPix: Number;
    private var projectileImage:DisplayObjectContainer   //could be Sprite or MovieClip
    private var projectileInFlight: Sprite;
    private var projectileOnGround: Sprite;
    private var pIndex: int;        //index of current projectile, which is trajectoryModel.projectiles[pIndex]

    public function ProjectileView( mainView:MainView,  trajectoryModel: MainModel, projectileImage:DisplayObjectContainer = null  ) {
        this.mainView = mainView;
        this.originXInPix = mainView.originXInPix;
        this.originYInPix = mainView.originYInPix;
        this.trajectoryModel = trajectoryModel;
        this.projectileImage = projectileImage;
        this.projectileInFlight = new Sprite();
        this.projectileOnGround = new Sprite();
//        this.stageW = mainView.stageW;
//        this.stageH = mainView.stageH;

        this.initialize();
    }

    private function initialize():void{
        trajectoryModel.registerView( this );
        pIndex = trajectoryModel.pIndex;
        drawProjectileInFlight();
        //drawProjectileOnGround();
        if( projectileImage != null ){
            this.addChild( projectileImage );
        }

        this.addChild( projectileInFlight );
        this.addChild( projectileOnGround );
    }

    public function drawProjectileInFlight():void {     //must be recalled if diameter is changed
        var radius:Number;
        var projectile: Projectile = trajectoryModel.projectiles[ pIndex ];
        radius = (0.5) * projectile.diameter * mainView.pixPerMeter;
        trace("ProjectileView.drawProjectile.radius = "+radius);
        var g:Graphics = this.projectileInFlight.graphics;
        g.clear();
        g.lineStyle(1, 0x000000, 1);
        g.beginFill(0x000000);
        g.drawCircle(0, 0, radius);
        g.endFill();
    }

    public function drawProjectileOnGround():void {     //must be recalled if diameter is changed
        var radius:Number;
        var projectile: Projectile = trajectoryModel.projectiles[ pIndex ];
        radius = (0.5) * projectile.diameter * mainView.pixPerMeter;
        trace("ProjectileView.drawProjectile.radius = "+radius);
        var g:Graphics = this.projectileInFlight.graphics;
        g.clear();
        g.lineStyle(1, 0x000000, 1);
        g.beginFill(0x000000);
        g.moveTo( radius,  0 );
        for( var angle: Number = 0; angle <= Math.PI; angle += Math.PI/30 ){
            g.lineTo( radius*Math.cos( angle ), -radius*Math.sin( angle ) );
        }
        g.lineTo( radius,  0 );
        g.endFill();
    }

    public function setVisibilityOfProjectiles():void{

    }


    public function update():void{
        var xInMeters: Number = trajectoryModel.xP;         //P for projectile
        var yInMeters: Number = trajectoryModel.yP;
        var xInPix: Number = this.originXInPix + xInMeters*mainView.pixPerMeterUnZoomed;
        var yInPix: Number = this.originYInPix - yInMeters*mainView.pixPerMeterUnZoomed;
        this.projectileInFlight.x = xInPix;
        this.projectileInFlight.y = yInPix;
    }
}//end class
}//end package
