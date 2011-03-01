// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.colorado.phet.bendinglight.model.BendingLightModel;
import edu.colorado.phet.bendinglight.model.LightRay;
import edu.colorado.phet.bendinglight.model.Medium;
import edu.colorado.phet.bendinglight.util.RichSimpleObserver;
import edu.colorado.phet.bendinglight.view.LaserColor;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;

import static java.lang.Math.*;

/**
 * @author Sam Reid
 */
public class PrismsModel extends BendingLightModel {
    private ArrayList<Prism> prisms = new ArrayList<Prism>();
    public final Property<Boolean> manyRays = new Property<Boolean>( false );//show multiple beams to help show how lenses work
    public final Property<Medium> environment = new Property<Medium>( new Medium( new Rectangle2D.Double( -1, 0, 2, 1 ), AIR, colorMappingFunction.getValue().apply( AIR.index ) ) );
    public final Property<Medium> prismMedium = new Property<Medium>( new Medium( new Rectangle2D.Double( -1, -1, 2, 1 ), WATER, colorMappingFunction.getValue().apply( WATER.index ) ) );
    public final Property<Boolean> showReflections = new Property<Boolean>( false );//If false, will hide non TIR reflections
    public final ArrayList<Intersection> intersections = new ArrayList<Intersection>();
    public final ArrayList<VoidFunction1<Intersection>> intersectionListeners = new ArrayList<VoidFunction1<Intersection>>();
    public Function1<Double, Double> environmentIndexOfRefraction;
    public Function1<Double, Double> prismIndexOfRefraction;

    public PrismsModel() {
        super( PI, false, DEFAULT_DIST_FROM_PIVOT * 0.9 );
        new RichSimpleObserver() {
            public void update() {
                updateModel();
            }
        }.observe( environment, prismMedium, manyRays, laser.color, showReflections );
        final Function.LinearFunction dispersionFunction = new Function.LinearFunction( WAVELENGTH_RED, VisibleColor.MAX_WAVELENGTH / 1E9, 0, 0.04 ); // A function that uses the default value for RED, and changes the index of refraction by +/- 0.04
        environmentIndexOfRefraction = new Function1<Double, Double>() {
            public Double apply( Double wavelength ) {
                return environment.getValue().getIndexOfRefraction() + dispersionFunction.evaluate( wavelength );
            }
        };
        prismIndexOfRefraction = new Function1<Double, Double>() {
            public Double apply( Double wavelength ) {
                return prismMedium.getValue().getIndexOfRefraction() + dispersionFunction.evaluate( wavelength );
            }
        };
    }

    @Override
    public void resetAll() {
        super.resetAll();
        while ( prisms.size() > 0 ) {
            removePrism( prisms.get( 0 ) );//TODO: need to remove the graphic for this too
        }
        manyRays.reset();
        environment.reset();
        prismMedium.reset();
        showReflections.reset();
    }

    public static ArrayList<Prism> getPrismPrototypes() {
        return new ArrayList<Prism>() {{
            final double a = WAVELENGTH_RED * 10;//characteristic length scale
            final double b = a / 4;//characteristic length scale
            //Square
            add( new Prism( new ImmutableVector2D(),
                            new ImmutableVector2D( 0, a ),
                            new ImmutableVector2D( a, a ),
                            new ImmutableVector2D( a, 0 ) ) );

            //Triangle
            add( new Prism( new ImmutableVector2D(),
                            new ImmutableVector2D( a, 0 ),
                            new ImmutableVector2D( a / 2, a * sqrt( 3 ) / 2.0 ) ) );

            //Trapezoid
            add( new Prism( new ImmutableVector2D(),
                            new ImmutableVector2D( a, 0 ),
                            new ImmutableVector2D( a / 2 + b, a * sqrt( 3 ) / 2.0 ),
                            new ImmutableVector2D( a / 2 - b, a * sqrt( 3 ) / 2.0 )
            ) );

            //Circle
            add( new Prism( new Polygon( new ArrayList<ImmutableVector2D>() {{
                int numSamples = 200;
                for ( int i = 0; i < numSamples; i++ ) {
                    add( ImmutableVector2D.parseAngleAndMagnitude( a / 2, (double) i / numSamples * Math.PI * 2 ) );
                }
            }} ) ) );

            //Semicircle
            add( new Prism( new Polygon( new ArrayList<ImmutableVector2D>() {{
                int numSamples = 200;
                for ( int i = 0; i < numSamples / 2; i++ ) {
                    add( ImmutableVector2D.parseAngleAndMagnitude( a / 2, (double) i / numSamples * Math.PI * 2 +
                                                                          Math.PI / 2 ) );//turn it so that the circular part is on the left, not on the top
                }
            }} ) ) );

            //Diverging lens: half an hourglass shape
            add( new Prism( new Polygon( new ArrayList<ImmutableVector2D>() {{
                int numSamples = 200;
                for ( int i = numSamples / 2; i < numSamples; i++ ) {
                    add( ImmutableVector2D.parseAngleAndMagnitude( a / 2, (double) i / numSamples * Math.PI * 2 +
                                                                          Math.PI / 2 ) );//turn it so that the circular part is on the left, not on the top
                }
                add( new ImmutableVector2D( a * 0.6, a / 2 ) );
                add( new ImmutableVector2D( a * 0.6, -a / 2 ) );
            }} ) ) );
        }};
    }

    public void addPrism( Prism prism ) {
        prism.shape.addObserver( new SimpleObserver() {
            public void update() {
                updateModel();
            }
        } );
        prisms.add( prism );
    }

    public Iterable<? extends Prism> getPrisms() {
        return prisms;
    }

    @Override
    protected void propagateRays() {
        super.propagateRays();
        if ( laser.on.getValue() ) {
            final ImmutableVector2D tail = new ImmutableVector2D( laser.emissionPoint.getValue() );

            final boolean laserInPrism = isLaserInPrism();
            final ImmutableVector2D directionUnitVector = laser.getDirectionUnitVector();

            final double wavelength = laser.color.getValue().getWavelength();

            //This can be used to show the main central ray
            if ( !manyRays.getValue() ) {
                propagateColor( new Ray( tail, directionUnitVector, 1.0, laserInPrism ? prismIndexOfRefraction : environmentIndexOfRefraction, wavelength ) );
            }
            else {
                //Many parallel rays
                for ( double x = -WAVELENGTH_RED; x <= WAVELENGTH_RED * 1.1; x += WAVELENGTH_RED / 2 ) {
                    ImmutableVector2D offsetDir = directionUnitVector.getRotatedInstance( Math.PI / 2 ).getScaledInstance( x );
                    propagateColor( new Ray( tail.getAddedInstance( offsetDir ), directionUnitVector, 1.0, laserInPrism ? prismIndexOfRefraction : environmentIndexOfRefraction, wavelength ) );
                }
            }
        }
    }

    //Determines whether to use white light or single color light
    private void propagateColor( Ray incidentRay ) {
        if ( laser.color.getValue() == LaserColor.WHITE_LIGHT ) {
            final double min = VisibleColor.MIN_WAVELENGTH / 1E9;
            final double max = VisibleColor.MAX_WAVELENGTH / 1E9;
            double dw = ( max - min ) / 16;//This number sets the number of (equally spaced wavelength) rays to show in a white beam.  More rays looks better but is more computationally intensive.
            for ( double wavelength = min; wavelength <= max; wavelength += dw ) {
                propagate( new Ray( incidentRay.tail, incidentRay.directionUnitVector, incidentRay.power, incidentRay.indexOfRefraction, wavelength ), 0 );
            }
        }
        else {
            propagate( incidentRay, 0 );
        }
    }

    private boolean isLaserInPrism() {
        for ( Prism prism : prisms ) {
            if ( prism.contains( laser.emissionPoint.getValue() ) ) { return true; }
        }
        return false;
    }

    private void propagate( Ray incidentRay, int count ) {
        double waveWidth = WAVELENGTH_RED * 5;
        if ( count > 50 || incidentRay.power < 0.001 ) {//binary recursion: 2^10 = 1024
            return;
        }
        Intersection intersection = getIntersection( incidentRay, prisms );
        ImmutableVector2D L = incidentRay.directionUnitVector;
        final double n1 = incidentRay.indexOfRefraction.apply( incidentRay.wavelength );

        if ( intersection != null ) {
            ImmutableVector2D pointOnOtherSide = new ImmutableVector2D( intersection.getPoint() ).getAddedInstance( incidentRay.directionUnitVector.getInstanceOfMagnitude( 1E-12 ) );
            boolean outputInsidePrism = false;
            for ( Prism prism : prisms ) {
                if ( prism.contains( pointOnOtherSide ) ) {
                    outputInsidePrism = true;
                }
            }
            Function1<Double, Double> otherMediumIndexOfRefraction = outputInsidePrism ? prismIndexOfRefraction : environmentIndexOfRefraction;

            final double n2 = otherMediumIndexOfRefraction.apply( incidentRay.wavelength );

            ImmutableVector2D point = intersection.getPoint();
            ImmutableVector2D n = intersection.getUnitNormal();
            addIntersection( intersection );
            //See http://en.wikipedia.org/wiki/Snell's_law#Vector_form
            double cosTheta1 = n.dot( L.getScaledInstance( -1 ) );
            final double cosTheta2Radicand = 1 - pow( n1 / n2, 2 ) * ( 1 - pow( cosTheta1, 2 ) );
            double cosTheta2 = sqrt( cosTheta2Radicand );

            boolean totalInternalReflection = cosTheta2Radicand < 0;
            ImmutableVector2D vReflect = L.getAddedInstance( n.getScaledInstance( 2 * cosTheta1 ) );
            ImmutableVector2D vRefract = cosTheta1 > 0 ?
                                         L.getScaledInstance( n1 / n2 ).getAddedInstance( n.getScaledInstance( n1 / n2 * cosTheta1 - cosTheta2 ) ) :
                                         L.getScaledInstance( n1 / n2 ).getAddedInstance( n.getScaledInstance( n1 / n2 * cosTheta1 + cosTheta2 ) );

            final double reflectedPower = totalInternalReflection ? 1 : MathUtil.clamp( 0, getReflectedPower( n1, n2, cosTheta1, cosTheta2 ), 1 );
            final double transmittedPower = totalInternalReflection ? 0 : MathUtil.clamp( 0, getTransmittedPower( n1, n2, cosTheta1, cosTheta2 ), 1 );

            Ray reflected = new Ray( point.getAddedInstance( incidentRay.directionUnitVector.getScaledInstance( -1E-12 ) ), vReflect, incidentRay.power * reflectedPower, incidentRay.indexOfRefraction, incidentRay.wavelength );
            Ray refracted = new Ray( point.getAddedInstance( incidentRay.directionUnitVector.getScaledInstance( +1E-12 ) ), vRefract, incidentRay.power * transmittedPower, otherMediumIndexOfRefraction, incidentRay.wavelength );
            if ( showReflections.getValue() || totalInternalReflection ) {
                propagate( reflected, count + 1 );
            }
            propagate( refracted, count + 1 );

            addRay( new LightRay( incidentRay.tail, intersection.getPoint(), n1, WAVELENGTH_RED / n1, incidentRay.power, new VisibleColor( incidentRay.wavelength * 1E9 ), waveWidth, 0, null, 0, true, false ) );
        }
        else {
            addRay( new LightRay( incidentRay.tail, incidentRay.tail.getAddedInstance( incidentRay.directionUnitVector.getScaledInstance( 1 ) )//1 meter long ray
                    , n1, WAVELENGTH_RED / n1, incidentRay.power, new VisibleColor( incidentRay.wavelength * 1E9 ), waveWidth, 0, null, 0, true, false ) );
        }
    }

    private void addIntersection( Intersection intersection ) {
        intersections.add( intersection );
        for ( VoidFunction1<Intersection> intersectionListener : intersectionListeners ) {
            intersectionListener.apply( intersection );
        }
    }

    public void addIntersectionListener( VoidFunction1<Intersection> listener ) {
        intersectionListeners.add( listener );
    }

    private static Intersection getIntersection( final Ray incidentRay, ArrayList<Prism> prisms ) {
        ArrayList<Intersection> allIntersections = new ArrayList<Intersection>();
        for ( Prism prism : prisms ) {
            allIntersections.addAll( prism.getIntersections( incidentRay ) );
        }
        Collections.sort( allIntersections, new Comparator<Intersection>() {
            public int compare( Intersection o1, Intersection o2 ) {
                return Double.compare( o1.getPoint().getDistance( incidentRay.tail ),
                                       o2.getPoint().getDistance( incidentRay.tail ) );
            }
        } );
        return allIntersections.size() == 0 ? null : allIntersections.get( 0 );
    }

    public void removePrism( Prism prism ) {
        prisms.remove( prism );
        updateModel();
    }

    @Override
    protected void clearModel() {
        super.clearModel();
        if ( intersections != null ) {
            for ( Intersection intersection : intersections ) {
                intersection.remove();
            }
            intersections.clear();
        }
    }
}