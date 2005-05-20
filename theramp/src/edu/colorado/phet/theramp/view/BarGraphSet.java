/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.theramp.common.BarGraphic;
import edu.colorado.phet.theramp.model.RampModel;
import edu.colorado.phet.theramp.model.ValueAccessor;

/**
 * User: Sam Reid
 * Date: Feb 12, 2005
 * Time: 10:42:31 AM
 * Copyright (c) Feb 12, 2005 by Sam Reid
 */

public class BarGraphSet extends CompositePhetGraphic {
    private RampPanel rampPanel;
    private RampModel rampModel;

    private int dx = 10;
    private int dy = -10;

    public BarGraphSet( RampPanel rampPanel, final RampModel rampModel ) {
        super( rampPanel );
        this.rampPanel = rampPanel;
        this.rampModel = rampModel;
        int y = 600;
        int width = 23;
        int dw = 10;
        int sep = width + dw;

        ModelViewTransform1D transform1D = new ModelViewTransform1D( 0, 150, 0, 10 );

        ValueAccessor[] energyAccess = new ValueAccessor[]{
            new ValueAccessor.KineticEnergy(), new ValueAccessor.PotentialEnergy(),
            new ValueAccessor.ThermalEnergy(), new ValueAccessor.TotalEnergy()

        };
        ValueAccessor[] workAccess = new ValueAccessor[]{
            new ValueAccessor.AppliedWork(), new ValueAccessor.FrictiveWork(),
            new ValueAccessor.GravityWork(), new ValueAccessor.TotalWork()
        };

        for( int i = 0; i < energyAccess.length; i++ ) {
            final ValueAccessor accessor = energyAccess[i];
            final BarGraphic barGraphic = new BarGraphic( getComponent(), accessor.getName(), transform1D,
                                                          accessor.getValue( rampModel ), sep * i + dw, width, y, dx, dy );
            addClockTickListener( new ClockTickListener() {
                public void clockTicked( ClockTickEvent event ) {
                    barGraphic.setValue( accessor.getValue( rampModel ) );
                }
            } );
            addGraphic( barGraphic );
        }

        for( int i = 0; i < workAccess.length; i++ ) {
            final ValueAccessor accessor = workAccess[i];
            final BarGraphic barGraphic = new BarGraphic( getComponent(), accessor.getName(), transform1D,
                                                          accessor.getValue( rampModel ), ( energyAccess.length + 1 + i ) * sep + dw, width, y, dx, dy );
            addClockTickListener( new ClockTickListener() {
                public void clockTicked( ClockTickEvent event ) {
                    barGraphic.setValue( accessor.getValue( rampModel ) );
                }
            } );
            addGraphic( barGraphic );
        }
        setIgnoreMouse( true );
    }

    private void addClockTickListener( ClockTickListener clockTickListener ) {
        rampPanel.getRampModule().getClock().addClockTickListener( clockTickListener );
    }
}
