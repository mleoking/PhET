package edu.colorado.phet.movingman.application.motionsuites;

import edu.colorado.phet.movingman.application.MovingManModule;
import edu.colorado.phet.movingman.elements.Man;
import edu.colorado.phet.movingman.elements.PlotsSetup;
import edu.colorado.phet.movingman.elements.stepmotions.AccelMotion;
import edu.colorado.phet.movingman.elements.stepmotions.StepMotion;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 4, 2003
 * Time: 10:06:07 PM
 */
public class VariableAcceleration extends MotionSuite {
    private AccelMotion motion;
    private MovingManModule module;
    private PlotsSetup plotsSetup;

    public VariableAcceleration( final MovingManModule module ) throws IOException {
        super( module, "Acceleration" );
        this.motion = new AccelMotion( module );
        this.module = module;
        this.plotsSetup = new PlotsSetup( 12, 10, 17, 16, 5, 4 );
        module.getAccelerationGraphic().getSlider().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double acceleration = module.getAccelerationGraphic().getSlider().getModelValue();
                motion.setAcceleration( acceleration * MovingManModule.TIMER_SCALE * MovingManModule.TIMER_SCALE * .9 );
                module.setPaused( false );
            }
        } );
    }

    public void initialize( Man man ) {
        plotsSetup.setup( module );
        module.setNumSmoothingPoints( MovingManModule.numSmoothingPoints );
    }

    public void showControls() {
        module.getPositionGraphic().setSliderVisible( false );
        module.getVelocityGraphic().setSliderVisible( false );
        module.getAccelerationGraphic().setSliderVisible( true );
    }

    public void collidedWithWall() {
        module.getAccelerationGraphic().getSlider().setModelValue( 0 );
    }

    public StepMotion getStepMotion() {
        return motion;
    }
}
