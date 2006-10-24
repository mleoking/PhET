/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.qm.davissongermer.QWIStrings;
import edu.colorado.phet.qm.model.QWIModel;
import edu.colorado.phet.qm.model.WaveSetup;
import edu.colorado.phet.qm.model.waves.GaussianWave2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

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
    private QWIControlPanel qwiControlPanel;

    public InitialConditionPanel( QWIControlPanel qwiControlPanel ) {
        this.qwiControlPanel = qwiControlPanel;

        xSlider = new ModelSlider( QWIStrings.getString( "x0" ), QWIStrings.getString( "1.l" ), 0, 1, 0.5 );
        ySlider = new ModelSlider( QWIStrings.getString( "y0" ), QWIStrings.getString( "1.l" ), 0, 1, 0.75 );
        pxSlider = new ModelSlider( QWIStrings.getString( "momentum.x0" ), "", -1.5, 1.5, 0 );
        pySlider = new ModelSlider( QWIStrings.getString( "momentum.y0" ), "", -1.5, 1.5, -0.8 );
        dxSlider = new ModelSlider( QWIStrings.getString( "size0" ), "", 0, 0.25, 0.04 );

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
