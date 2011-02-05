// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
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
    private Laser laser = new Laser( modelWidth / 8 );

    public LRRModel() {
        this.clock = new ConstantDtClock( 20, 1e-15 );
        final SimpleObserver updateRays = new SimpleObserver() {
            public void update() {
                for ( LightRay ray : rays ) {
                    ray.remove();
                }
                rays.clear();

                if ( laserOn.getValue() ) {
                    final ImmutableVector2D tail = new ImmutableVector2D( laser.getEmissionPoint() );
                    ImmutableVector2D tip = ImmutableVector2D.parseAngleAndMagnitude( 1, laser.angle.getValue() ).getScaledInstance( -1 );
                    LightRay ray = new LightRay( new Property<ImmutableVector2D>( tail ), new Property<ImmutableVector2D>( tip ), 1.0, redWavelength );
                    rays.add( ray );
                    for ( VoidFunction1<LightRay> rayAddedListener : rayAddedListeners ) {
                        rayAddedListener.apply( ray );
                    }
                }
            }
        };
        laserOn.addObserver( updateRays );
        laser.angle.addObserver( updateRays );
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

    public IClock getClock() {
        return clock;
    }
}
