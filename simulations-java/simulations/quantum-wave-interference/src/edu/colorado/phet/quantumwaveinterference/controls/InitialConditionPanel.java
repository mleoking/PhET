/*  */
package edu.colorado.phet.quantumwaveinterference.controls;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.model.QWIModel;
import edu.colorado.phet.quantumwaveinterference.model.WaveSetup;
import edu.colorado.phet.quantumwaveinterference.model.waves.GaussianWave2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 30, 2005
 * Time: 9:07:55 AM
 */

public class InitialConditionPanel extends VerticalLayoutPanel {
    private ModelSlider xSlider;
    private ModelSlider ySlider;
    private ModelSlider pxSlider;
    private ModelSlider pySlider;
    private ModelSlider dxSlider;
    private QWIControlPanel qwiControlPanel;

    public InitialConditionPanel( QWIControlPanel qwiControlPanel ) {
        this.qwiControlPanel = qwiControlPanel;

        xSlider = new ModelSlider( QWIResources.getString( "x0" ), QWIResources.getString( "1.l" ), 0, 1, 0.5 );
        ySlider = new ModelSlider( QWIResources.getString( "y0" ), QWIResources.getString( "1.l" ), 0, 1, 0.75 );
        pxSlider = new ModelSlider( QWIResources.getString( "momentum.x0" ), "", -1.5, 1.5, 0 );
        pySlider = new ModelSlider( QWIResources.getString( "momentum.y0" ), "", -1.5, 1.5, -0.8 );
        dxSlider = new ModelSlider( QWIResources.getString( "size0" ), "", 0, 0.25, 0.04 );

        pxSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double lambda = 2 * Math.PI / pxSlider.getValue();
                System.out.println( "lambda = " + lambda );
            }
        } );

        add( xSlider );
        add( ySlider );
        add( pxSlider );
        add( pySlider );
        add( dxSlider );


    }

    private double getStartDxLattice() {
        double dxLattice = dxSlider.getValue() * getDiscreteModel().getGridWidth();
        System.out.println( "dxLattice = " + dxLattice );
        return dxLattice;
    }


    private double getStartPy() {
        return pySlider.getValue();
    }

    private double getStartPx() {
        return pxSlider.getValue();
    }

    private double getStartY() {
        return ySlider.getValue() * getDiscreteModel().getGridHeight();
    }

    private QWIModel getDiscreteModel() {
        return qwiControlPanel.getDiscreteModel();
    }

    private double getStartX() {
        return xSlider.getValue() * getDiscreteModel().getGridWidth();
    }

    public WaveSetup getWaveSetup() {
        double x = getStartX();
        double y = getStartY();
        double px = getStartPx();
        double py = getStartPy();
        double dxLattice = getStartDxLattice();
        return new GaussianWave2D( new Point( (int)x, (int)y ),
                                   new Vector2D.Double( px, py ), dxLattice, getHBar() );
    }

    private double getHBar() {
        return 1.0;
    }

}
