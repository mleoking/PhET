package edu.colorado.phet.movingman.motion;

import edu.colorado.phet.movingman.Man;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.plots.PlotsSetup;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 4, 2003
 * Time: 10:06:07 PM
 */
public class VariableVelocity extends MotionSuite {
    private WalkSuite.WalkMotion motion;
    private MovingManModule module;
    private PlotsSetup plotsSetup;

    public VariableVelocity( final MovingManModule module ) throws IOException {
        super( module, "Velocity" );
        this.motion = new WalkSuite.WalkMotion( module );
        this.module = module;
        this.plotsSetup = new PlotsSetup( 12, 10, 5, 4, 55, 50 );
        module.getVelocityGraphic().getSlider().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double velocity = module.getVelocityGraphic().getSlider().getModelValue();
                motion.setVelocity( velocity * MovingManModule.TIMER_SCALE * .9 );
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
        module.getVelocityGraphic().setSliderVisible( true );
        module.getAccelerationGraphic().setSliderVisible( false );
    }

    public void collidedWithWall() {
        module.getVelocityGraphic().getSlider().setModelValue( 0 );
    }

    public StepMotion getStepMotion() {
        return motion;
    }
}
