/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.theramp.common.BarGraphic;
import edu.colorado.phet.theramp.model.RampModel;

/**
 * User: Sam Reid
 * Date: Feb 12, 2005
 * Time: 10:42:31 AM
 * Copyright (c) Feb 12, 2005 by Sam Reid
 */

public class BarGraphSet extends CompositePhetGraphic {
    private RampPanel rampPanel;
    private RampModel rampModel;

    private BarGraphic keGraphic;
    private BarGraphic peGraphic;
    private BarGraphic totalEnergyGraphic;
    private BarGraphic workFrictionGraphic;
    private BarGraphic thermalEnergyGraphic;

    private int dx = 10;
    private int dy = -10;
    private BarGraphic workAppliedGraphic;
    private BarGraphic workGravityGraphic;
    private BarGraphic workTotalGraphic;


    public BarGraphSet( RampPanel rampPanel, final RampModel rampModel ) {
        super( rampPanel );
        this.rampPanel = rampPanel;
        this.rampModel = rampModel;
        int y = 600;
        int width = 23;
        int dw = 10;
        int sep = width + dw;

        ModelViewTransform1D transform1D = new ModelViewTransform1D( 0, 150, 0, 10 );
        keGraphic = new BarGraphic( getComponent(), "Kinetic", transform1D,
                                    rampModel.getBlock().getKineticEnergy(), dw, width, y, dx, dy );
        addGraphic( keGraphic );

        addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                keGraphic.setValue( rampModel.getBlock().getKineticEnergy() );
            }
        } );

        peGraphic = new BarGraphic( getComponent(), "Potential", transform1D,
                                    rampModel.getPotentialEnergy(), dw + sep, width, y, dx, dy );
        addGraphic( peGraphic );
        addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                peGraphic.setValue( rampModel.getPotentialEnergy() );
            }
        } );

        thermalEnergyGraphic = new BarGraphic( getComponent(), "Thermal", transform1D,
                                               rampModel.getThermalEnergy(), dw + sep * 2, width, y, dx, dy );
        addGraphic( thermalEnergyGraphic );
        addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                thermalEnergyGraphic.setValue( rampModel.getThermalEnergy() );
            }
        } );

        totalEnergyGraphic = new BarGraphic( getComponent(), "Total", transform1D,
                                             rampModel.getTotalEnergy(), dw + sep * 3, width, y, dx, dy );
        addGraphic( totalEnergyGraphic );
        addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                totalEnergyGraphic.setValue( rampModel.getTotalEnergy() );
            }
        } );

        workAppliedGraphic = new BarGraphic( getComponent(), "Work Done By Applied Force", transform1D,
                                             0, dw + sep * 5, width, y, dx, dy );

        rampPanel.getRampModule().getClock().addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                double work = rampModel.getAppliedWork();
                workAppliedGraphic.setValue( work );
            }
        } );
        addGraphic( workAppliedGraphic );

        workFrictionGraphic = new BarGraphic( getComponent(), "Work Done By Friction", transform1D,
                                              0, dw + sep * 6, width, y, dx, dy );

        rampPanel.getRampModule().getClock().addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                double work = rampModel.getFrictiveWork();
                workFrictionGraphic.setValue( work );
            }
        } );
        addGraphic( workFrictionGraphic );

        workGravityGraphic = new BarGraphic( getComponent(), "Work Done By Gravity", transform1D,
                                             0, dw + sep * 7, width, y, dx, dy );

        rampPanel.getRampModule().getClock().addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                double work = rampModel.getGravityWork();
                workGravityGraphic.setValue( work );
            }
        } );
        addGraphic( workGravityGraphic );

        workTotalGraphic = new BarGraphic( getComponent(), "Total Work", transform1D,
                                           0, dw + sep * 8, width, y, dx, dy );

        rampPanel.getRampModule().getClock().addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                double work = rampModel.getGravityWork() + rampModel.getFrictiveWork() + rampModel.getAppliedWork();
                workTotalGraphic.setValue( work );
            }
        } );
        addGraphic( workTotalGraphic );

        setIgnoreMouse( true );
    }

    private void addClockTickListener( ClockTickListener clockTickListener ) {
        rampPanel.getRampModule().getClock().addClockTickListener( clockTickListener );
    }
}
