package edu.colorado.phet.movingman.application.motionsuites;

import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.movingman.application.MovingManModule;
import edu.colorado.phet.movingman.elements.Man;
import edu.colorado.phet.movingman.elements.stepmotions.OscMotion;

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
    }

}
