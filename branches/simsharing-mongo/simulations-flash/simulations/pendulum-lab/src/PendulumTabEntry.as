
import edu.colorado.phet.flashcommon.*;

class PendulumTabEntry extends edu.colorado.phet.flashcommon.TabEntry {

    public var model : Pendulum;
    public var rootMC : MovieClip;
    public var view : View;

    public function PendulumTabEntry( myModel : Pendulum, myRootMC : MovieClip, myView : View ) {
        super( myRootMC.pendulum_mc.mass_mc, TabHandler.HIGHLIGHT_LOCAL );

        model = myModel;
        rootMC = myRootMC;
        view = myView;

        highlightColor = 0xFF00FF;
        highlightWidth = 5;
        highlightInset = -5;
    }

    // TODO: refactor all of the view stuff so that this code isn't duplicated

    public function onKeyDown( keyCode : Number ) {
        _level0.debug( "Key down\n" );
        if( keyCode == 37 || keyCode == 39 ) {
            _level0.debug( "Control key down\n" );
            model.stopMotion();
            rootMC.angle_txt._visible = true;

            var change : Number;
            if( keyCode == 37 ) {
                change = -1;
            } else {
                change = 1;
            }

            // new angle in degrees
            var angle : Number = Math.round( model.getAngleInDeg() + change );

            view.currentAngleInDeg = angle;
            model.setAngleInDeg( angle );
            rootMC.pendulum_mc._rotation = -angle;
            view.setPointerAngle( angle );
            var angleString : String = String( 0.1 * Math.round( 10 * angle ) );
            rootMC.angle_txt.text = angleString + view.angleUnit_str;

            updateAfterEvent();
        }
    }

    public function onKeyUp( keyCode : Number ) {
        if( keyCode == 37 || keyCode == 39 ) {
            view.pendulum.setAngleInDeg(view.currentAngleInDeg);
            view.pendulum.startMotion();
            rootMC.angle_txt._visible = false;
            //clipRef.onMouseMove = undefined;
        }
    }
    
}
