/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.GaussianWave;
import edu.colorado.phet.qm.model.WaveSetup;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.model.operators.YValue;
import edu.colorado.phet.qm.model.propagators.ClassicalWavePropagator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 30, 2005
 * Time: 9:07:55 AM
 * Copyright (c) Jun 30, 2005 by Sam Reid
 */

public class InitialConditionPanel extends VerticalLayoutPanel {
    private ModelSlider xSlider;
    private ModelSlider ySlider;
    private ModelSlider pxSlider;
    private ModelSlider pySlider;
    private ModelSlider dxSlider;
    private SchrodingerControlPanel schrodingerControlPanel;

    public InitialConditionPanel( SchrodingerControlPanel schrodingerControlPanel ) {
        this.schrodingerControlPanel = schrodingerControlPanel;

        xSlider = new ModelSlider( "X0", "1/L", 0, 1, 0.5 );
        ySlider = new ModelSlider( "Y0", "1/L", 0, 1, 0.75 );
        pxSlider = new ModelSlider( "Momentum-x0", "", -1.5, 1.5, 0 );
        pySlider = new ModelSlider( "Momentum-y0", "", -1.5, 1.5, -0.8 );
        dxSlider = new ModelSlider( "Size0", "", 0, 0.25, 0.04 );

//        wavelengthYSlider=new ModelSlider("Wavelength(y-dir)","",

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

    private DiscreteModel getDiscreteModel() {
        return schrodingerControlPanel.getDiscreteModel();
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
        WaveSetup waveSetup = new GaussianWave( new Point( (int)x, (int)y ),
                                                new Vector2D.Double( px, py ), dxLattice );
        return waveSetup;
    }

    public void initClassicalWave( ClassicalWavePropagator propagator2ndOrder ) {
//        WaveSetup wave=initialConditionPanel.getWaveSetup();
        double x = getStartX();
        double y0 = 0.5 * getDiscreteModel().getGridHeight();
        double px = getStartPx();
        double py = getStartPy();
        double dxLattice = getStartDxLattice();

        Wavefunction t0 = new Wavefunction( getDiscreteModel().getGridWidth(), getDiscreteModel().getGridHeight() );
        Wavefunction t1 = new Wavefunction( getDiscreteModel().getGridWidth(), getDiscreteModel().getGridHeight() );
        new GaussianWave( new Point2D.Double( x, y0 ), new Vector2D.Double( px, py ), dxLattice ).initialize( t0 );

        double time = 1.0;
        double y1 = y0 + propagator2ndOrder.getSpeed() * time;

        new GaussianWave( new Point2D.Double( x, y1 ), new Vector2D.Double( px, py ), dxLattice ).initialize( t1 );

        Wavefunction t2 = new Wavefunction( getDiscreteModel().getGridWidth(), getDiscreteModel().getGridHeight() );
        double y2 = y1 + propagator2ndOrder.getSpeed() * time;
        new GaussianWave( new Point2D.Double( x, y2 ), new Vector2D.Double( px, py ), dxLattice ).initialize( t2 );
        getDiscreteModel().getWavefunction().setWavefunction( t2 );

        System.out.println( "y0=" + y0 + ", y1 = " + y1 + ", y2=" + y2 );
        System.out.println( "new YValue().compute( t0) = " + new YValue().compute( t0 ) * getDiscreteModel().getGridHeight() );
        System.out.println( "new YValue().compute( t1) = " + new YValue().compute( t1 ) * getDiscreteModel().getGridHeight() );
        System.out.println( "new YValue().compute( t2) = " + new YValue().compute( t2 ) * getDiscreteModel().getGridHeight() );
        propagator2ndOrder.initialize( t2, t2 );

    }
}
