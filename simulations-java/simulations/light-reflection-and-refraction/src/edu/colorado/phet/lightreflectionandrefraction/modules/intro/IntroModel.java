// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lightreflectionandrefraction.model.LRRModel;
import edu.colorado.phet.lightreflectionandrefraction.model.LightRay;
import edu.colorado.phet.lightreflectionandrefraction.model.Medium;
import edu.colorado.phet.lightreflectionandrefraction.model.Reading;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.parseAngleAndMagnitude;
import static edu.colorado.phet.common.phetcommon.math.MathUtil.getLineCircleIntersection;
import static java.lang.Math.*;

/**
 * @author Sam Reid
 */
public class IntroModel extends LRRModel {
    private double incomingRayPhase = 0.0;//Keep track of the phase across time steps so that we can maintain instead of resetting phase under angle or index of refraction changes

    public final Property<Medium> topMedium = new Property<Medium>( new Medium( new Rectangle2D.Double( -1, 0, 2, 1 ), AIR, colorMappingFunction.getValue().apply( AIR.index ) ) );
    public final Property<Medium> bottomMedium = new Property<Medium>( new Medium( new Rectangle2D.Double( -1, -1, 2, 1 ), WATER, colorMappingFunction.getValue().apply( WATER.index ) ) );

    public IntroModel() {
        super( PI * 3 / 4 );
        colorMappingFunction.addObserver( new SimpleObserver() {
            public void update() {
                topMedium.setValue( new Medium( topMedium.getValue().getShape(), topMedium.getValue().getMediumState(), colorMappingFunction.getValue().apply( topMedium.getValue().getIndexOfRefraction() ) ) );
                bottomMedium.setValue( new Medium( bottomMedium.getValue().getShape(), bottomMedium.getValue().getMediumState(), colorMappingFunction.getValue().apply( bottomMedium.getValue().getIndexOfRefraction() ) ) );
            }
        } );
        SimpleObserver updateRays = new SimpleObserver() {
            public void update() {
                updateModel();
            }
        };
        topMedium.addObserver( updateRays );
        bottomMedium.addObserver( updateRays );
    }

    protected void propagateRays() {
        final Rectangle bottom = new Rectangle( -10, -10, 20, 10 );
        final Rectangle top = new Rectangle( -10, 0, 20, 10 );
        if ( laser.on.getValue() ) {
            final ImmutableVector2D tail = new ImmutableVector2D( laser.getEmissionPoint() );

            double n1 = topMedium.getValue().getIndexOfRefraction();
            double n2 = bottomMedium.getValue().getIndexOfRefraction();

            //Snell's law, see http://en.wikipedia.org/wiki/Snell's_law
            final double theta1 = laser.angle.getValue() - Math.PI / 2;
            double theta2 = asin( n1 / n2 * sin( theta1 ) );

            final double sourcePower = 1.0;
            double a = WAVELENGTH_RED * 5;//cross section of incident light, used to compute wave widths

            double sourceWaveWidth = a / 2;//This one fixes the input beam to be a fixed width independent of angle

            //According to http://en.wikipedia.org/wiki/Wavelength
            //lambda = lambda0 / n(lambda0)
            final Color color = laser.color.getValue().getColor();
            final LightRay incidentRay = new LightRay( tail, new ImmutableVector2D(), n1, WAVELENGTH_RED / n1, sourcePower, color, sourceWaveWidth, incomingRayPhase, bottom, incomingRayPhase, true, false );
            incidentRay.phase.addObserver( new SimpleObserver() {
                public void update() {
//                            incomingRayPhase = incidentRay.phase.getValue();//TODO: this is buggy
                    incomingRayPhase = 0.0;
                }
            } );
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
                                            parseAngleAndMagnitude( 1, Math.PI - laser.angle.getValue() ), n1, WAVELENGTH_RED / n1, reflectedPowerRatio * sourcePower, color, reflectedWaveWidth, incidentRay.getNumberOfWavelengths(), bottom, 0.0, true, false ) );

                if ( hasTransmittedRay ) {
                    //Transmitted
                    //n2/n1 = L1/L2 => L2 = L1*n2/n1
                    double transmittedWavelength = incidentRay.getWavelength() / n2 * n1;
                    if ( Double.isNaN( theta2 ) || Double.isInfinite( theta2 ) ) {}
                    else {
                        double transmittedPowerRatio = getTransmittedPower( n1, n2, cos( theta1 ), cos( theta2 ) );

                        double beamHalfWidth = a / 2;
                        double extentInterceptedHalfWidth = beamHalfWidth / Math.sin( Math.PI / 2 - theta1 ) / 2;
                        double transmittedBeamHalfWidth = Math.cos( theta2 ) * extentInterceptedHalfWidth;
                        double transmittedWaveWidth = transmittedBeamHalfWidth * 2;

                        final LightRay transmittedRay = new LightRay( new ImmutableVector2D(),
                                                                      parseAngleAndMagnitude( 1, theta2 - Math.PI / 2 ), n2, transmittedWavelength,
                                                                      transmittedPowerRatio * sourcePower, color, transmittedWaveWidth, incidentRay.getNumberOfWavelengths(), top, 0.0, true, true ) {

                        };
                        addAndAbsorb( transmittedRay );
                    }
                }
            }
            incidentRay.moveToFront();//For wave view
        }
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
                                                     ray.getWaveWidth(), ray.getNumWavelengthsPhaseOffset(), ray.getOppositeMedium(), ray.phase.getValue(), false, ray.extendBackwards );
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
            intensityMeter.addRayReading( new Reading( ray.getPowerFraction() ) );
        }
        else {
            intensityMeter.addRayReading( Reading.MISS );
        }
        return rayAbsorbed;
    }

    @Override
    public void resetAll() {
        super.resetAll();
        topMedium.reset();
        bottomMedium.reset();
    }
}
