package edu.colorado.phet.movingman.motion;

import edu.colorado.phet.movingman.Man;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.common.PhetLookAndFeel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 4, 2003
 * Time: 10:06:07 PM
 * To change this template use Options | File Templates.
 */
public class OscillateSuite extends MotionSuite {
    public static class OscMotion implements StepMotion {
        double k;//spring constant.
        double center = 0;
        private double initX = 5;
        private MovingManModule module;

        public OscMotion( MovingManModule module, double k ) {
            this.module = module;
            this.k = k;
        }

        public double stepInTime( Man man, double dt ) {
            dt = dt * MovingManModule.TIMER_SCALE;
            double x = man.getX() - center;
            double acceleration = -k * x;
            double vnew = module.getMan().getVelocity() + acceleration * dt;
            module.getMan().setVelocity( vnew );
            double xnew = man.getX() + vnew * dt;
            return xnew;
        }

        public void setK( double k ) {
            this.k = k;
        }

        public void initialize( Man man ) {
            module.getMan().setVelocity( 0 );
            man.setX( initX );
        }
    }

    private OscMotion oscillate;
    private MovingManModule module;

    public OscillateSuite( MovingManModule module ) throws IOException {
        super( module, "Oscillate" );
        this.module = module;

        double min = 0;
        double max = 20;
        double numSteps = 200;
        SpinnerNumberModel m = new SpinnerNumberModel( ( max - min ) / 2, min, max, ( max - min ) / numSteps );
        oscillate = new OscMotion( module, ( (Number)m.getValue() ).doubleValue() );
        final JSpinner js = new JSpinner( m );
        js.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Double d = (Double)js.getValue();
                oscillate.setK( d.doubleValue() );
            }
        } );
        js.setBorder( PhetLookAndFeel.createSmoothBorder( "Spring Constant" ) );

        getControlPanel().add( js );
        getControlPanel().add( new JLabel( "<html>That's the Spring Constant, <br>in Newtons per Meter.</html>" ) );
        super.setMotion( oscillate );
    }

    public void collidedWithWall() {
        initialize( module.getMan() );
    }

    public void initialize( Man man ) {
        module.getPositionPlot().getGrid().setPaintYLines( new double[]{-10, -5, 0, 5, 10} );

        module.setVelocityPlotMagnitude( 21 );
        module.getVelocityPlot().getGrid().setPaintYLines( new double[]{-20, -10, 0, 10, 20} );

        module.setAccelerationPlotMagnitude( 55 );
        module.getAccelerationPlot().getGrid().setPaintYLines( new double[]{-50, -25, 0, 25, 50} );
        module.repaintBackground();

        oscillate.initialize( man );
        module.getPositionGraphic().setSliderVisible( false );
        module.getVelocityGraphic().setSliderVisible( false );
        module.getAccelerationGraphic().setSliderVisible( false );
    }

}
