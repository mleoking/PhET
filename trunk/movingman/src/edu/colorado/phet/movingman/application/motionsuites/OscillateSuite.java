package edu.colorado.phet.movingman.application.motionsuites;

import edu.colorado.phet.movingman.application.MovingManModule;
import edu.colorado.phet.movingman.common.PhetLookAndFeel;
import edu.colorado.phet.movingman.common.VerticalLayoutPanel;
import edu.colorado.phet.movingman.elements.Man;
import edu.colorado.phet.movingman.elements.stepmotions.OscMotion;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 4, 2003
 * Time: 10:06:07 PM
 * To change this template use Options | File Templates.
 */
public class OscillateSuite extends MotionSuite {
    private OscMotion oscillate;
    private JPanel controlPanel;
    private MovingManModule module;

    public OscillateSuite( MovingManModule module ) {
        super( "Oscillate" );
        this.module = module;
        oscillate = new OscMotion( module.getMotionState(), .01 );
        controlPanel = new VerticalLayoutPanel();
        double min = 0;
        double max = 2;
        int numSteps = 20;
        SpinnerNumberModel m = new SpinnerNumberModel( ( max - min ) / 2, min, max, ( max - min ) / numSteps );
        final JSpinner js = new JSpinner( m );
        js.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Double d = (Double)js.getValue();
                oscillate.setK( d.doubleValue() / 100 );
            }
        } );
        js.setBorder( PhetLookAndFeel.createSmoothBorder( "Spring Constant" ) );

        controlPanel.add( js );
        super.setControlPanel( controlPanel );
        controlPanel.add( new JLabel( "<html>That's the Spring Constant, <br>in Newtons per Meter.</html>" ) );
        super.setMotion( oscillate );
    }

    public JPanel getControlPanel() {
        return controlPanel;
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
    }

}
