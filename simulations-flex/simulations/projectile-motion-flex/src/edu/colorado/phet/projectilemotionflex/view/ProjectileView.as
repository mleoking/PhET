/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/17/12
 * Time: 9:10 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.view {
import edu.colorado.phet.projectilemotionflex.model.Projectile;
import edu.colorado.phet.projectilemotionflex.model.TrajectoryModel;

import flash.display.Graphics;

import flash.display.Sprite;

// a single view for all 10 projectiles, view properties change when projectile changes
public class ProjectileView extends Sprite {
    private var mainView: MainView;
    private var trajectoryModel: TrajectoryModel;
//    private var stageW: Number;
//    private var stageH: Number;
    private var originXInPix: Number;
    private var originYInPix: Number;
    private var projectileSprite: Sprite;
    private var pIndex: int;        //index of current projectile, which is trajectoryModel.projectiles[pIndex]

    public function ProjectileView( mainView:MainView,  trajectoryModel: TrajectoryModel  ) {
        this.mainView = mainView;
        this.originXInPix = mainView.originXInPix;
        this.originYInPix = mainView.originYInPix;
        this.trajectoryModel = trajectoryModel;
        this.projectileSprite = new Sprite();
//        this.stageW = mainView.stageW;
//        this.stageH = mainView.stageH;

        this.initialize();
    }

    private function initialize():void{
        trajectoryModel.registerView( this );
        pIndex = trajectoryModel.pIndex;
        drawProjectile();
        this.addChild( projectileSprite );
    }

    public function drawProjectile():void {     //must be recalled if diameter is changed
        var radius:Number;
        var projectile: Projectile = trajectoryModel.projectiles[ pIndex ];
        radius = (0.5) * projectile.diameter * mainView.pixPerMeter;
        trace("ProjectileView.drawProjectile.radius = "+radius);
        var g:Graphics = this.projectileSprite.graphics;
        g.clear();
        g.lineStyle(1, 0x000000, 1);
        g.beginFill(0x000000);
        g.drawCircle(0, 0, radius);
        g.endFill();
    }


    public function update():void{
        var xInMeters: Number = trajectoryModel.xP;         //P for projectile
        var yInMeters: Number = trajectoryModel.yP;
        var xInPix: Number = this.originXInPix + xInMeters*mainView.pixPerMeter;
        var yInPix: Number = this.originYInPix - yInMeters*mainView.pixPerMeter;
        this.projectileSprite.x = xInPix;
        this.projectileSprite.y = yInPix;
    }
}//end class
}//end package
