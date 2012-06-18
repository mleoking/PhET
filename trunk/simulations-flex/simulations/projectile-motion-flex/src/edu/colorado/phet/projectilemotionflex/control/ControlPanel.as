/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/17/12
 * Time: 9:10 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.control {
import edu.colorado.phet.flashcommon.controls.NiceButton2;
import edu.colorado.phet.projectilemotionflex.model.TrajectoryModel;
import edu.colorado.phet.projectilemotionflex.view.MainView;

import mx.containers.Canvas;
import mx.containers.VBox;
import mx.controls.ComboBox;

//control panel for projectile motion flex

public class ControlPanel extends Canvas {
    private var mainView: MainView;
    private var trajectoryModel: TrajectoryModel;
    private var stageW: Number;
    private var stageH: Number;

    private var selectProjectile: ComboBox;
    private var background: VBox;
    private var fireButton: NiceButton2;
    private var eraseButton: NiceButton2;

    public function ControlPanel( mainView:MainView,  trajectoryModel: TrajectoryModel ) {
        this.mainView = mainView;
        this.trajectoryModel = trajectoryModel;
        this.stageW = mainView.stageW;
        this.stageH = mainView.stageH;
    } //end constructor

}//end class
}//end package
