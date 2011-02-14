// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.prisms;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.lightreflectionandrefraction.model.LRRModel;
import edu.colorado.phet.lightreflectionandrefraction.model.LightRay;
import edu.colorado.phet.lightreflectionandrefraction.modules.intro.Medium;

import static java.lang.Math.asin;
import static java.lang.Math.sin;

/**
 * @author Sam Reid
 */
public class PrismsModel extends LRRModel {
    private ArrayList<Prism> prisms = new ArrayList<Prism>();
    public final Property<Medium> outerMedium = new Property<Medium>( new Medium( new Rectangle2D.Double( -1, 0, 2, 1 ), AIR, colorMappingFunction.getValue().apply( AIR.index ) ) );
    public final Property<Medium> prismMedium = new Property<Medium>( new Medium( new Rectangle2D.Double( -1, -1, 2, 1 ), WATER, colorMappingFunction.getValue().apply( WATER.index ) ) );

    public PrismsModel() {
        prisms.add( new Prism( new Rectangle2D.Double( 0, 0, WAVELENGTH_RED * 10, WAVELENGTH_RED * 10 ) ) );
    }

    public Iterable<? extends Prism> getPrisms() {
        return prisms;
    }

    @Override
    protected void addRays() {
        super.addRays();
        if ( laser.on.getValue() ) {
            final ImmutableVector2D tail = new ImmutableVector2D( laser.getEmissionPoint() );

            double n1 = outerMedium.getValue().getIndexOfRefraction();
            double n2 = prismMedium.getValue().getIndexOfRefraction();

            //Snell's law, see http://en.wikipedia.org/wiki/Snell's_law
            final double theta1 = laser.angle.getValue() - Math.PI / 2;
            double theta2 = asin( n1 / n2 * sin( theta1 ) );

            final double sourcePower = 1.0;
            double a = WAVELENGTH_RED * 5;//cross section of incident light, used to compute wave widths
            double sourceWaveWidth = a * Math.cos( theta1 );

            //According to http://en.wikipedia.org/wiki/Wavelength
            //lambda = lambda0 / n(lambda0)
            final LightRay incidentRay = new LightRay( tail, new ImmutableVector2D(), n1, WAVELENGTH_RED / n1, sourcePower, laser.color.getValue(), sourceWaveWidth, 0, null, 0, true, false );
            final boolean rayAbsorbed = addAndAbsorb( incidentRay );
//            if ( !rayAbsorbed ) {
//
//                double thetaOfTotalInternalReflection = asin( n2 / n1 );
//                boolean hasTransmittedRay = Double.isNaN( thetaOfTotalInternalReflection ) || theta1 < thetaOfTotalInternalReflection;
//
//                //reflected
//                //assuming perpendicular beam, compute percent power
//                double reflectedPowerRatio;
//                if ( hasTransmittedRay ) {
//                    reflectedPowerRatio = pow( ( n1 * cos( theta1 ) - n2 * cos( theta2 ) ) / ( n1 * cos( theta1 ) + n2 * cos( theta2 ) ), 2 );
//                }
//                else {
//                    reflectedPowerRatio = 1.0;
//                }
//                double reflectedWaveWidth = sourceWaveWidth;
//                addAndAbsorb( new LightRay( new ImmutableVector2D(),
//                                            parseAngleAndMagnitude( 1, Math.PI - laser.angle.getValue() ), n1, WAVELENGTH_RED / n1, reflectedPowerRatio * sourcePower, laser.color.getValue(), reflectedWaveWidth, incidentRay.getNumberOfWavelengths(), bottom, 0.0, true, false ) );
//
//                if ( hasTransmittedRay ) {
//                    //Transmitted
//                    //n2/n1 = L1/L2 => L2 = L1*n2/n1
//                    double transmittedWavelength = incidentRay.getWavelength() / n2 * n1;
//                    if ( Double.isNaN( theta2 ) || Double.isInfinite( theta2 ) ) {}
//                    else {
//                        double transmittedPowerRatio = 4 * n1 * n2 * cos( theta1 ) * cos( theta2 ) / ( pow( n1 * cos( theta1 ) + n2 * cos( theta2 ), 2 ) );
//                        double transmittedWaveWidth = a * Math.cos( theta2 );
//                        final LightRay transmittedRay = new LightRay( new ImmutableVector2D(),
//                                                                      parseAngleAndMagnitude( 1, theta2 - Math.PI / 2 ), n2, transmittedWavelength,
//                                                                      transmittedPowerRatio * sourcePower, laser.color.getValue(), transmittedWaveWidth, incidentRay.getNumberOfWavelengths(), top, 0.0, true, true ) {
//
//                        };
//                        addAndAbsorb( transmittedRay );
//                    }
//                }
//            }
//            incidentRay.moveToFront();//For wave view
        }
    }
}
