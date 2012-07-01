/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 7/1/12
 * Time: 6:12 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.model {
import edu.colorado.phet.projectilemotionflex.view.MainView;
import edu.colorado.phet.projectilemotionflex.view.MainView;
import edu.colorado.phet.projectilemotionflex.view.ProjectileView;

public class Projectile {

    private var mainView: MainView;
    private var trajectoryModel: TrajectoryModel;
    private var _mass: Number;
    private var _diameter: Number;
    private var _dragCoefficient: Number;


    public function Projectile( mainView:MainView, trajectoryModel:TrajectoryModel, mass:Number,  diameter:Number,  dragCoefficient:Number ) {
        this.mainView = mainView;
        this.trajectoryModel = trajectoryModel
        _mass = mass;
        _diameter = diameter;
        _dragCoefficient = dragCoefficient;
    }//end constructor

    public function get mass():Number {
        return _mass;
    }

    public function set mass(value:Number):void {
        _mass = value;
    }

    public function get diameter():Number {
        return _diameter;
    }

    public function set diameter(value:Number):void {
        _diameter = value;
    }

    public function get dragCoefficient():Number {
        return _dragCoefficient;
    }

    public function set dragCoefficient(value:Number):void {
        _dragCoefficient = value;
    }

}//end lclass
}//end package
