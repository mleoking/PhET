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

    public function TrajectoryView( mainView:MainView,  trajectoryModel: TrajectoryModel ) {
        this.mainView = mainView;
        this.trajectoryModel = trajectoryModel;
        this.stageW = mainView.stageW;
        this.stageH = mainView.stageH;
        this.initialize();
    }

    private function initialize():void{
        trajectoryModel.registerView( this );
    }

    public function update():void{
    }

}//end of class
}//end of package
