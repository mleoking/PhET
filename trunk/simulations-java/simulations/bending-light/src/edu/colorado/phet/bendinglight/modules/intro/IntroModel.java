// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.intro;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.bendinglight.model.*;
import edu.colorado.phet.bendinglight.view.LaserView;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

import static edu.colorado.phet.bendinglight.model.IntensityMeter.Reading.MISS;
import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.parseAngleAndMagnitude;
import static edu.colorado.phet.common.phetcommon.math.MathUtil.getLineCircleIntersection;
import static java.lang.Math.*;

/**
 * Model for the "intro" tab, which has an upper and lower medium, interfacing at the middle of the screen, and the laser at the top left shining toward the interface.
 *
 * @author Sam Reid
 */
public class IntroModel extends BendingLightModel {
    private double incomingRayPhase = 0.0;//Keep track of the phase across time steps so that we can maintain instead of resetting phase under angle or index of refraction changes

    public final Property<Medium> topMedium = new Property<Medium>( new Medium( new Rectangle2D.Double( -1, 0, 2, 1 ), AIR, MediumColorFactory.getColor( AIR.index() ) ) );
    public final Property<Medium> bottomMedium;

    public IntroModel( MediumState _bottomMedium ) {
        super( PI * 3 / 4, true, DEFAULT_DIST_FROM_PIVOT );
        SimpleObserver updateRays = new SimpleObserver() {
            public void update() {
                updateModel();
            }
        };
        topMedium.addObserver( updateRays );
        bottomMedium = new Property<Medium>( new Medium( new Rectangle2D.Double( -1, -1, 2, 1 ), _bottomMedium, MediumColorFactory.getColor( _bottomMedium.index() ) ) );
        bottomMedium.addObserver( updateRays );
    }

    protected void propagateRays() {
        final Rectangle bottom = new Rectangle( -10, -10, 20, 10 );
        final Rectangle top = new Rectangle( -10, 0, 20, 10 );
        if ( laser.on.getValue() ) {
            final ImmutableVector2D tail = new ImmutableVector2D( laser.emissionPoint.getValue() );

            //Snell's law, see http://en.wikipedia.org/wiki/Snell's_law for definition of n1, n2, theta1, theta2
            final double n1 = getN1();
            final double n2 = getN2();

            final double theta1 = laser.getAngle() - Math.PI / 2;
            double theta2 = asin( n1 / n2 * sin( theta1 ) );

            final double sourcePower = 1.0;
            double a = CHARACTERISTIC_LENGTH * 5;//cross section of incident light, used to compute wave widths

            double sourceWaveWidth = a / 2;//This one fixes the input beam to be a fixed width independent of angle

            //According to http://en.wikipedia.org/wiki/Wavelength
            final Color color = laser.color.getValue().getColor();
            final double wavelengthInTopMedium = laser.color.getValue().getWavelength() / n1;

            //Since the n1 depends on the wavelength, when you change the wavelength, the wavelengthInTopMedium also changes (seemingly in the opposite direction)
            final LightRay incidentRay = new LightRay( tail, new ImmutableVector2D(), n1, wavelengthInTopMedium, sourcePower, color, sourceWaveWidth, incomingRayPhase, bottom, true, false );
            final boolean rayAbsorbed = addAndAbsorb( incidentRay );
            if ( !rayAbsorbed ) {

                double thetaOfTotalInternalReflection = asin( n2 / n1 );
                boolean hasTransmittedRay = Double.isNaN( thetaOfTotalInternalReflection ) || theta1 < thetaOfTotalInternalReflection;

                //reflected
                //assuming perpendicular beam polarization, compute percent power
                double reflectedPowerRatio;
                if ( hasTransmittedRay ) {
                    reflectedPowerRatio = getReflectedPower( n1, n2, cos( theta1 ), cos( theta2 ) );
                }
                else {
                    reflectedPowerRatio = 1.0;
                }
                double reflectedWaveWidth = sourceWaveWidth;
                addAndAbsorb( new LightRay( new ImmutableVector2D(),
                                            parseAngleAndMagnitude( 1, Math.PI - laser.getAngle() ), n1, wavelengthInTopMedium, reflectedPowerRatio * sourcePower, color, reflectedWaveWidth, incidentRay.getNumberOfWavelengths(), bottom, true, false ) );

                if ( hasTransmittedRay ) {
                    //Transmitted
                    //n2/n1 = L1/L2 => L2 = L1*n2/n1
                    double transmittedWavelength = incidentRay.getWavelength() / n2 * n1;
                    if ( Double.isNaN( theta2 ) || Double.isInfinite( theta2 ) ) {}
                    else {
                        double transmittedPowerRatio = getTransmittedPower( n1, n2, cos( theta1 ), cos( theta2 ) );

                        double beamHalfWidth = a / 2;
                        double extentInterceptedHalfWidth = beamHalfWidth / Math.sin( Math.PI / 2 - theta1 ) / 2;
                        double transmittedBeamHalfWidth = cos( theta2 ) * extentInterceptedHalfWidth;
                        double transmittedWaveWidth = transmittedBeamHalfWidth * 2;

                        final LightRay transmittedRay = new LightRay( new ImmutableVector2D(),
                                                                      parseAngleAndMagnitude( 1, theta2 - Math.PI / 2 ), n2, transmittedWavelength,
                                                                      transmittedPowerRatio * sourcePower, color, transmittedWaveWidth, incidentRay.getNumberOfWavelengths(), top, true, true );
                        addAndAbsorb( transmittedRay );
                    }
                }
            }
            incidentRay.moveToFront();//For wave view
        }
    }

    private double getN1() {
        return topMedium.getValue().getIndexOfRefraction( laser.color.getValue().getWavelength() );
    }

    private double getN2() {
        return bottomMedium.getValue().getIndexOfRefraction( laser.color.getValue().getWavelength() );
    }

    /*
     Checks whether the intensity meter should absorb the ray, and if so adds a truncated ray.
     If the intensity meter misses the ray, the original ray is added.
     */
    protected boolean addAndAbsorb( LightRay ray ) {
        boolean rayAbsorbed = ray.intersects( intensityMeter.getSensorShape() ) && intensityMeter.enabled.getValue();
        if ( rayAbsorbed ) {
            Point2D[] intersects = getLineCircleIntersection( intensityMeter.getSensorShape(), ray.toLine2D() );
            if ( intersects != null && intersects[0] != null && intersects[1] != null ) {
                double x = intersects[0].getX() + intersects[1].getX();
                double y = intersects[0].getY() + intersects[1].getY();
                LightRay interrupted = new LightRay( ray.tail.getValue(), new ImmutableVector2D( x / 2, y / 2 ), ray.indexOfRefraction, ray.getWavelength(), ray.getPowerFraction(), laser.color.getValue().getColor(),
                                                     ray.getWaveWidth(), ray.getNumWavelengthsPhaseOffset(), ray.getOppositeMedium(), false, ray.extendBackwards );
                boolean isForward = ray.toVector2D().dot( interrupted.toVector2D() ) > 0; //don't let the wave intersect the intensity meter if it is behind the laser emission point
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
            intensityMeter.addRayReading( new IntensityMeter.Reading( ray.getPowerFraction() ) );
        }
        else {
            intensityMeter.addRayReading( MISS );
        }
        return rayAbsorbed;
    }

    @Override
    public void resetAll() {
        super.resetAll();
        topMedium.reset();
        bottomMedium.reset();
    }

    protected Option<ImmutableVector2D> getVelocity( ImmutableVector2D position ) {
        for ( LightRay ray : rays ) {
            if ( ray.contains( position, laserView.getValue() == LaserView.WAVE ) ) {
                return new Option.Some<ImmutableVector2D>( ray.getVelocity() );
            }
        }
        return new Option.None<ImmutableVector2D>();
    }

    protected Option<Double> getWaveValue( ImmutableVector2D position ) {
        for ( LightRay ray : rays ) {
            if ( ray.contains( position, laserView.getValue() == LaserView.WAVE ) ) {
                final double amplitude = Math.sqrt( ray.getPowerFraction() );
                final double distanceAlongRay = ray.getUnitVector().dot( new ImmutableVector2D( ray.tail.getValue().toPoint2D(), position.toPoint2D() ) );
                final double phase = ray.getCosArg( distanceAlongRay );
                return new Option.Some<Double>( amplitude * cos( phase + Math.PI ) );
            }
        }
        return new Option.None<Double>();
    }
}
