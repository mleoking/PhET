// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.model;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction1;
import edu.colorado.phet.lightreflectionandrefraction.modules.intro.IntensityMeter;
import edu.colorado.phet.lightreflectionandrefraction.modules.intro.Medium;
import edu.colorado.phet.lightreflectionandrefraction.modules.intro.Reading;

import static java.lang.Math.*;

public class LRRModel {
    private List<LightRay> rays = new LinkedList<LightRay>();
    private ConstantDtClock clock;

    public static final MediumState VACUUM = new MediumState( "Vacuum", 1.0 );
    public static final MediumState AIR = new MediumState( "Air", 1.000293 );
    public static final MediumState WATER = new MediumState( "Water", 1.333 );
    public static final MediumState GLASS = new MediumState( "Glass", 1.5 );
    public static final MediumState DIAMOND = new MediumState( "Diamond", 2.419 );
    public static final MediumState MYSTERY_A = new MediumState( "Mystery A", DIAMOND.index ) {
        public boolean isMystery() {
            return true;
        }
    };
    public static final MediumState MYSTERY_B = new MediumState( "Mystery B", 1.4 ) {
        public boolean isMystery() {
            return true;
        }
    };

    public Property<Function1<Double, Color>> colorMappingFunction = new Property<Function1<Double, Color>>( new Function1<Double, Color>() {
        public Color apply( Double value ) {
            if ( value < WATER.index ) {
                double ratio = new Function.LinearFunction( 1.0, WATER.index, 0, 1 ).evaluate( value );
                return colorBlend( AIR_COLOR, WATER_COLOR, ratio );
            }
            else if ( value < GLASS.index ) {
                double ratio = new Function.LinearFunction( WATER.index, GLASS.index, 0, 1 ).evaluate( value );
                return colorBlend( WATER_COLOR, GLASS_COLOR, ratio );
            }
            else if ( value < DIAMOND.index ) {
                double ratio = new Function.LinearFunction( GLASS.index, DIAMOND.index, 0, 1 ).evaluate( value );
                return colorBlend( GLASS_COLOR, DIAMOND_COLOR, ratio );
            }
            else {
                return DIAMOND_COLOR;
            }
        }

        public Color colorBlend( Color a, Color b, double ratio ) {
            return new Color(
                    (int) ( ( (float) a.getRed() ) * ( 1 - ratio ) + ( (float) b.getRed() ) * ratio ),
                    (int) ( ( (float) a.getGreen() ) * ( 1 - ratio ) + ( (float) b.getGreen() ) * ratio ),
                    (int) ( ( (float) a.getBlue() ) * ( 1 - ratio ) + ( (float) b.getBlue() ) * ratio ),
                    (int) ( ( (float) a.getAlpha() ) * ( 1 - ratio ) + ( (float) b.getAlpha() ) * ratio )
            );
        }
    } );

    public static final double C = 2.99792458e8;
    final double redWavelength = 650E-9;

    final double modelWidth = redWavelength * 62;
    final double modelHeight = modelWidth * 0.7;

    private ArrayList<VoidFunction1<LightRay>> rayAddedListeners = new ArrayList<VoidFunction1<LightRay>>();
    private Laser laser = new Laser( 8.125E-6 );
    public final Property<Medium> topMedium = new Property<Medium>( new Medium( new Rectangle2D.Double( -1, 0, 2, 1 ), AIR, colorMappingFunction.getValue().apply( AIR.index ) ) );
    public final Property<Medium> bottomMedium = new Property<Medium>( new Medium( new Rectangle2D.Double( -1, -1, 2, 1 ), WATER, colorMappingFunction.getValue().apply( WATER.index ) ) );
    private IntensityMeter intensityMeter = new IntensityMeter( modelWidth * 0.3, -modelHeight * 0.3, modelWidth * 0.4, -modelHeight * 0.3 );
    //Alphas may be ignored, see MediumNode
    public static final Color AIR_COLOR = Color.white;
    public static final Color WATER_COLOR = new Color( 198, 226, 246 );
    public static final Color GLASS_COLOR = new Color( 171, 169, 212 );
    public static final Color DIAMOND_COLOR = new Color( 78, 79, 164 );

    public LRRModel() {
        this.clock = new ConstantDtClock( 20, 1e-15 );
        colorMappingFunction.addObserver( new SimpleObserver() {
            public void update() {
                topMedium.setValue( new Medium( topMedium.getValue().getShape(), topMedium.getValue().getMediumState(), colorMappingFunction.getValue().apply( topMedium.getValue().getIndexOfRefraction() ) ) );
                bottomMedium.setValue( new Medium( bottomMedium.getValue().getShape(), bottomMedium.getValue().getMediumState(), colorMappingFunction.getValue().apply( bottomMedium.getValue().getIndexOfRefraction() ) ) );
            }
        } );
        final SimpleObserver updateRays = new SimpleObserver() {
            public void update() {
                for ( LightRay ray : rays ) {
                    ray.remove();
                }
                rays.clear();

                intensityMeter.clearRayReadings();
                if ( laser.on.getValue() ) {
                    final ImmutableVector2D tail = new ImmutableVector2D( laser.getEmissionPoint() );
                    final double sourcePower = 1.0;
                    final LightRay ray = new LightRay( tail, new ImmutableVector2D(), 1.0, redWavelength, sourcePower, laser.color.getValue() );
                    final boolean rayAbsorbed = handleAbsorb( ray );
                    if ( !rayAbsorbed ) {
                        double n1 = topMedium.getValue().getIndexOfRefraction();
                        double n2 = bottomMedium.getValue().getIndexOfRefraction();

                        //Snell's law, see http://en.wikipedia.org/wiki/Snell's_law
                        final double theta1 = laser.angle.getValue() - Math.PI / 2;
                        double theta2 = asin( n1 / n2 * sin( theta1 ) );

                        double thetaOfTotalInternalReflection = asin( n2 / n1 );
                        boolean hasTransmittedRay = Double.isNaN( thetaOfTotalInternalReflection ) || theta1 < thetaOfTotalInternalReflection;

                        //reflected
                        //assuming perpendicular beam, compute percent power
                        double reflectedPowerRatio;
                        if ( hasTransmittedRay ) {
                            reflectedPowerRatio = pow( ( n1 * cos( theta1 ) - n2 * cos( theta2 ) ) / ( n1 * cos( theta1 ) + n2 * cos( theta2 ) ), 2 );
                        }
                        else {
                            reflectedPowerRatio = 1.0;
                        }
                        handleAbsorb( new LightRay( new ImmutableVector2D(),
                                                    ImmutableVector2D.parseAngleAndMagnitude( 1, Math.PI - laser.angle.getValue() ), 1.0, redWavelength, reflectedPowerRatio * sourcePower, laser.color.getValue() ) );

                        if ( hasTransmittedRay ) {
                            //Transmitted
                            if ( Double.isNaN( theta2 ) || Double.isInfinite( theta2 ) ) {}
                            else {
                                double transmittedPowerRatio = 4 * n1 * n2 * cos( theta1 ) * cos( theta2 ) / ( pow( n1 * cos( theta1 ) + n2 * cos( theta2 ), 2 ) );
                                handleAbsorb( new LightRay( new ImmutableVector2D(),
                                                            ImmutableVector2D.parseAngleAndMagnitude( 1, theta2 - Math.PI / 2 ), 1.0, redWavelength, transmittedPowerRatio * sourcePower, laser.color.getValue() ) );
                            }
                        }
                    }
                }
            }
        };
//        updateRays.listenTo(topMedium,bottomMedium,laser.on,laser.angle,intensityMeter.sensorPosition);
        topMedium.addObserver( updateRays );
        bottomMedium.addObserver( updateRays );
        laser.on.addObserver( updateRays );
        laser.angle.addObserver( updateRays );
        intensityMeter.sensorPosition.addObserver( updateRays );
        intensityMeter.enabled.addObserver( updateRays );
        laser.color.addObserver( updateRays );
        clock.start();
    }

    /*
     Checks whether the intensity meter should absorb the ray, and if so adds a truncated ray.
     If the intensity meter misses the ray, the original ray is added.
     */
    private boolean handleAbsorb( LightRay ray ) {
        boolean rayAbsorbed = ray.intersects( intensityMeter.getSensorShape() ) && intensityMeter.enabled.getValue();
        if ( rayAbsorbed ) {
            Point2D[] intersects = MathUtil.getLineCircleIntersection( intensityMeter.getSensorShape(), ray.toLine2D() );
            if ( intersects != null && intersects[0] != null && intersects[1] != null ) {
                double x = intersects[0].getX() + intersects[1].getX();
                double y = intersects[0].getY() + intersects[1].getY();
                LightRay interrupted = new LightRay( ray.tail.getValue(), new ImmutableVector2D( x / 2, y / 2 ), 1.0, redWavelength, ray.getPowerFraction(), laser.color.getValue() );
                boolean isForward = ray.toVector2D().dot( interrupted.toVector2D() ) > 0;
                if ( interrupted.getLength() < ray.getLength() && isForward ) {
                    addRay( interrupted );
                }
                else {
                    addRay( ray );
                    rayAbsorbed = false;
                }
            }
        }
        else {
            addRay( ray );
        }
        if ( rayAbsorbed ) {
            intensityMeter.addRayReading( new Reading( ray.getPowerFraction() ) );
        }
        else {
            intensityMeter.addRayReading( Reading.MISS );
        }
        return rayAbsorbed;
    }

    private void addRay( LightRay ray ) {
        rays.add( ray );
        for ( VoidFunction1<LightRay> rayAddedListener : rayAddedListeners ) {
            rayAddedListener.apply( ray );
        }
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

    public Iterable<? extends LightRay> getRays() {
        return rays;
    }

    public static Vector3d reflect( Vector3d incident, Vector3d normal ) {
        assert ( Math.abs( incident.length() - 1 ) < Numbers.EPSILON );
        assert ( Math.abs( normal.length() - 1 ) < Numbers.EPSILON );

        double dot = incident.dot( normal );
        if ( dot > -Numbers.EPSILON ) {
            // TODO: change to assert?
            throw new RuntimeException( "dot > -Numbers.EPSILON" );
        }
        Vector3d ret = new Vector3d( normal );
        ret.scale( 2 * dot );
        ret.sub( incident );
        ret.negate();

        assert ( Math.abs( ret.length() - 1 ) < Numbers.EPSILON );
        return ret;
    }

    public static Vector3d transmit( Vector3d incident, Vector3d normal, double na, double nb ) {
        assert ( Math.abs( incident.length() - 1 ) < Numbers.EPSILON );
        assert ( Math.abs( normal.length() - 1 ) < Numbers.EPSILON );

        // check for TIR
        assert ( !isTotalInternalReflection( incident, normal, na, nb ) );

        assert ( na >= 1 );
        assert ( nb >= 1 );

        double q = na / nb;
        double dot = incident.dot( normal );
        if ( dot > -Numbers.EPSILON ) {
            // TODO: change to assert?
            throw new RuntimeException( "dot > -Numbers.EPSILON" );
        }
        Vector3d t = new Vector3d( normal );
        Vector3d ret = new Vector3d( incident );
        t.scale( q * dot + ( Math.sqrt( 1 - q * q * ( 1 - dot * dot ) ) ) );
        ret.scale( q );
        ret.sub( t );

        assert ( Math.abs( ret.length() - 1 ) < Numbers.EPSILON );
        return ret;
    }

    public static boolean isTotalInternalReflection( Vector3d incident, Vector3d normal, double na, double nb ) {
        if ( na <= nb ) {
            return false;
        }
        double dot = -normal.dot( incident );

        assert ( dot > Numbers.EPSILON );

        double cosineTIRAngle = Math.sqrt( 1 - ( nb / na ) * ( nb / na ) );

        // be conservative about TIR, so that if within epsilon we report TIR is true
        if ( dot >= cosineTIRAngle + Numbers.EPSILON ) {
            return false;
        }
        else {
            return true;
        }
    }

    public IntensityMeter getIntensityMeter() {
        return intensityMeter;
    }
}
