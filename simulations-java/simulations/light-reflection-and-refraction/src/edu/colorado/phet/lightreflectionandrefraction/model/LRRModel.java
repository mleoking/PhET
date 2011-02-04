// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.VoidFunction1;
import edu.umd.cs.piccolo.util.PDimension;

public class LRRModel {
    private List<LightRay> rays = new LinkedList<LightRay>();
    private ConstantDtClock clock;

    public static final double C = 2.99792458e8;
    public Property<Boolean> laserOn = new Property<Boolean>( false );
    final double redWavelength = 650E-9;
    final double modelWidth = redWavelength * 50;
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 680 );
    final double modelHeight = STAGE_SIZE.getHeight() / STAGE_SIZE.getWidth() * modelWidth;
    private ArrayList<VoidFunction1<LightRay>> rayAddedListeners = new ArrayList<VoidFunction1<LightRay>>();
    private LightRay emittingRay;

    public LRRModel( final ConstantDtClock clock ) {
        this.clock = clock;
        clock.addClockListener( new ClockAdapter() {
            @Override
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                if ( laserOn.getValue() ) {
                    if ( emittingRay == null ) {
                        final ImmutableVector2D tail = new ImmutableVector2D( -modelWidth / 2, modelHeight / 2 );
                        final ImmutableVector2D origin = new ImmutableVector2D( 0, 0 );
                        ImmutableVector2D diff = origin.getSubtractedInstance( tail ).getInstanceOfMagnitude( 1.0 ).getScaledInstance( 1E-12 );//point in the right direction, but keep it small

                        emittingRay = new LightRay( new Property<ImmutableVector2D>( tail ), new Property<ImmutableVector2D>( tail.getAddedInstance( diff ) ), 1.0, redWavelength );
                        for ( VoidFunction1<LightRay> rayAddedListener : rayAddedListeners ) {
                            rayAddedListener.apply( emittingRay );
                        }
                    }
                }
                else {
                    if ( emittingRay != null ) {
                        rays.add( emittingRay );
                        emittingRay = null;
                    }
                }
                if ( emittingRay != null ) { emittingRay.propagateTip( clock.getSimulationTimeChange() ); }
                for ( LightRay ray : rays ) {
                    ray.propagate( clock.getSimulationTimeChange() );
                }
            }
        } );
        clock.start();
    }

    public double getWidth() {
        return modelWidth;
    }

    public double getHeight() {
        return modelHeight;
    }

    public void addRayAddedListener( VoidFunction1<LightRay> listener ) {
        rayAddedListeners.add( listener );
    }
}
