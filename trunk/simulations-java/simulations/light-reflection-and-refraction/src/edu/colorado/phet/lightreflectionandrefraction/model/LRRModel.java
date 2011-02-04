// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
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
    private LightRay laserEmissionRay;
    private Laser laser = new Laser( modelWidth / 8 );
    public Point2D lastEmissionPoint = new Point2D.Double();
    private double lightSpeedScaleFactor = 1.0;

    public LRRModel() {
        this.clock = new ConstantDtClock( 20, 1e-15 );
        final SimpleObserver rayMaker = new SimpleObserver() {
            public void update() {
                if ( laserEmissionRay != null ) {
                    rays.add( laserEmissionRay );
                    laserEmissionRay = null;
                }
            }
        };
        laserOn.addObserver( rayMaker );
        laser.angle.addObserver( rayMaker );
        clock.addClockListener( new ClockAdapter() {

            @Override
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                if ( laserOn.getValue() ) {
                    if ( laserEmissionRay == null ) {
                        final ImmutableVector2D tail = new ImmutableVector2D( laser.getEmissionPoint() );
                        ImmutableVector2D diff = ImmutableVector2D.parseAngleAndMagnitude( 1E-12, laser.angle.getValue() ).getScaledInstance( -1 );

                        laserEmissionRay = new LightRay( new Property<ImmutableVector2D>( tail ), new Property<ImmutableVector2D>( tail.getAddedInstance( diff ) ), 1.0, redWavelength, lightSpeedScaleFactor );
                        for ( VoidFunction1<LightRay> rayAddedListener : rayAddedListeners ) {
                            rayAddedListener.apply( laserEmissionRay );
                        }
                        lastEmissionPoint = laser.getEmissionPoint().toPoint2D();
                    }
                }
                if ( laserEmissionRay != null ) {
                    laserEmissionRay.tail.setValue( laser.getEmissionPoint() );
                    laserEmissionRay.propagateTip( clock.getSimulationTimeChange() );
                }
                for ( LightRay ray : rays ) {
                    ray.propagate( clock.getSimulationTimeChange() );
                }

                //TODO: remove rays from the model when they are far enough away
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

    public Laser getLaser() {
        return laser;
    }

    public void setLightSpeedScaleFactor( double scaleFactor ) {
        this.lightSpeedScaleFactor = scaleFactor;
    }

    public IClock getClock() {
        return clock;
    }
}
