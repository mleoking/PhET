package edu.colorado.phet.lightreflectionandrefraction.model;

import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

public class LRRModel {
    private List<LightRay> rays = new LinkedList<LightRay>();
    private ConstantDtClock clock;

    public static final double C = 2.99792458e8;

    public LRRModel( final ConstantDtClock clock ) {
        this.clock = clock;
        clock.addClockListener( new ClockAdapter() {
            @Override
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                for ( LightRay ray : rays ) {
                    ray.propagate( clock.getSimulationTimeChange() );
                }
            }
        } );
        clock.start();
    }

    public void addRay( LightRay ray ) {
        rays.add( ray );
    }


}
