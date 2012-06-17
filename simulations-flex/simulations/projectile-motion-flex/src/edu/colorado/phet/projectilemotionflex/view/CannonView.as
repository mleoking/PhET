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

public class CannonView extends Sprite {
    private var mainView: MainView;
    private var trajectoryModel: TrajectoryModel;
    private var stageW: Number;
    private var stageH: Number;
    private var barrel: Sprite;
    private var carriage: Sprite;
    private var platform: Sprite;
    private var flames: Sprite;
    private var pixelsPerMeter: Number;



    public function CannonView( mainView:MainView,  trajectoryModel: TrajectoryModel ) {
        this.mainView = mainView;
        this.trajectoryModel = trajectoryModel;
        this.stageW = mainView.stageW;
        this.stageH = mainView.stageH;
        this.barrel = new Sprite();
        this.carriage = new Sprite();
        this.platform = new Sprite();
        this.flames = new Sprite();
        this.initialize();
    }//end constructor

    private function initialize():void{
        this.pixelsPerMeter = 30;

        this.drawCannon();
        this.drawCarriage();
        this.addChild( barrel );
        this.addChild( carriage );
        this.addChild( platform );
    }//end initialize()

    private function drawCannon():void{
        var L:Number = 2*pixelsPerMeter;
        var W:Number = 0.3*pixelsPerMeter;
        var gC: Graphics = barrel.graphics;
        with( gC ){
            clear();
            lineStyle( 1, 0xffffff, 1 );
            beginFill( 0xbbbbbb, 1 );
            moveTo( 0 , 0 );
            lineTo( 0,  W );
            lineTo( L,  W );
            lineTo( L, 0 );
            lineTo( 0, 0 );
            endFill();
        }
    }//end drawCannon()

    private function drawCarriage():void{

    }

} //end class
} //end package
