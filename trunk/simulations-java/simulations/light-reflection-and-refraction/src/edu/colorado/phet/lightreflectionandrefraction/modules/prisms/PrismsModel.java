// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.prisms;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lightreflectionandrefraction.model.LRRModel;
import edu.colorado.phet.lightreflectionandrefraction.model.LightRay;
import edu.colorado.phet.lightreflectionandrefraction.model.Medium;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * @author Sam Reid
 */
public class PrismsModel extends LRRModel {
    private ArrayList<Prism> prisms = new ArrayList<Prism>();
    public final Property<Boolean> manyRays = new Property<Boolean>( false );//show multiple beams to help show how lenses work
    public final Property<Medium> outerMedium = new Property<Medium>( new Medium( new Rectangle2D.Double( -1, 0, 2, 1 ), AIR, colorMappingFunction.getValue().apply( AIR.index ) ) );
    public final Property<Medium> prismMedium = new Property<Medium>( new Medium( new Rectangle2D.Double( -1, -1, 2, 1 ), WATER, colorMappingFunction.getValue().apply( WATER.index ) ) );

    public PrismsModel() {
        final double a = WAVELENGTH_RED * 10;//characteristic length scale
        final double b = a / 4;//characteristic length scale

        //Square
        addPrism( new Prism( new ImmutableVector2D(),
                             new ImmutableVector2D( 0, a ),
                             new ImmutableVector2D( a, a ),
                             new ImmutableVector2D( a, 0 ) ) );

        //Triangle
        addPrism( new Prism( new ImmutableVector2D(),
                             new ImmutableVector2D( a, 0 ),
                             new ImmutableVector2D( a / 2, a * sqrt( 3 ) / 2.0 ) ) );

        //Trapezoid
        addPrism( new Prism( new ImmutableVector2D(),
                             new ImmutableVector2D( a, 0 ),
                             new ImmutableVector2D( a / 2 + b, a * sqrt( 3 ) / 2.0 ),
                             new ImmutableVector2D( a / 2 - b, a * sqrt( 3 ) / 2.0 )
        ) );

        //Circle
        addPrism( new Prism( new Polygon( new ArrayList<ImmutableVector2D>() {{
            int numSamples = 200;
            for ( int i = 0; i < numSamples; i++ ) {
                add( ImmutableVector2D.parseAngleAndMagnitude( a / 2, (double) i / numSamples * Math.PI * 2 ) );
            }
        }} ) ) );

        //Semicircle
        addPrism( new Prism( new Polygon( new ArrayList<ImmutableVector2D>() {{
            int numSamples = 200;
            for ( int i = 0; i < numSamples / 2; i++ ) {
                add( ImmutableVector2D.parseAngleAndMagnitude( a / 2, (double) i / numSamples * Math.PI * 2 +
                                                                      Math.PI / 2 ) );//turn it so that the circular part is on the left, not on the top
            }
        }} ) ) );

        final SimpleObserver updateModel = new SimpleObserver() {
            public void update() {
                updateModel();
            }
        };

        outerMedium.addObserver( updateModel );
        prismMedium.addObserver( updateModel );
        manyRays.addObserver( updateModel );
    }

    private void addPrism( Prism prism ) {
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
    protected void addRays() {
        super.addRays();
        if ( laser.on.getValue() ) {
            final ImmutableVector2D tail = new ImmutableVector2D( laser.getEmissionPoint() );

            double n1 = outerMedium.getValue().getIndexOfRefraction();
            double n2 = prismMedium.getValue().getIndexOfRefraction();

            final boolean laserInPrism = laserInPrism();
            final ImmutableVector2D directionUnitVector = new ImmutableVector2D( tail.toPoint2D(), new Point2D.Double() ).getNormalizedInstance();

            //This can be used to show the main central ray
            if ( !manyRays.getValue() ) {
                propagate( new Ray( tail, directionUnitVector, 1.0, laserInPrism ? n2 : n1, laserInPrism ? n1 : n2 ), 0 );
            }
            else {
                //This is a test for showing multiple parallel rays for showing how focusing lenses work
                for ( double x = -WAVELENGTH_RED; x <= WAVELENGTH_RED * 1.1; x += WAVELENGTH_RED / 2 ) {
                    ImmutableVector2D offsetDir = directionUnitVector.getRotatedInstance( Math.PI / 2 ).getScaledInstance( x );
                    propagate( new Ray( tail.getAddedInstance( offsetDir ), directionUnitVector, 1.0, laserInPrism ? n2 : n1, laserInPrism ? n1 : n2 ), 0 );
                }
            }
        }
    }

    private boolean laserInPrism() {
        for ( Prism prism : prisms ) {
            if ( prism.contains( laser.getEmissionPoint() ) ) { return true; }
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
        final double n1 = incidentRay.indexOfRefraction;
        final double n2 = incidentRay.oppositeIndexOfRefraction;
        if ( intersection != null ) {
            ImmutableVector2D point = intersection.getPoint();
            ImmutableVector2D n = intersection.getUnitNormal();
            //See http://en.wikipedia.org/wiki/Snell's_law#Vector_form
            double cosTheta1 = n.dot( L.getScaledInstance( -1 ) );
            final double cosTheta2Radicand = 1 - pow( n1 / n2, 2 ) * ( 1 - pow( cosTheta1, 2 ) );
            double cosTheta2 = sqrt( cosTheta2Radicand );

//            System.out.println( "cosTheta2 = " + cosTheta2 );
//            System.out.println( "cosTheta2Radicand = " + cosTheta2Radicand );
            boolean totalInternalReflection = cosTheta2Radicand < 0;
            ImmutableVector2D vReflect = L.getAddedInstance( n.getScaledInstance( 2 * cosTheta1 ) );
            ImmutableVector2D vRefract = cosTheta1 > 0 ?
                                         L.getScaledInstance( n1 / n2 ).getAddedInstance( n.getScaledInstance( n1 / n2 * cosTheta1 - cosTheta2 ) ) :
                                         L.getScaledInstance( n1 / n2 ).getAddedInstance( n.getScaledInstance( n1 / n2 * cosTheta1 + cosTheta2 ) );

            final double reflectedPower = totalInternalReflection ? 1 : MathUtil.clamp( 0, getReflectedPower( n1, n2, cosTheta1, cosTheta2 ), 1 );
            final double transmittedPower = totalInternalReflection ? 0 : MathUtil.clamp( 0, getTransmittedPower( n1, n2, cosTheta1, cosTheta2 ), 1 );

            Ray reflected = new Ray( point.getAddedInstance( incidentRay.directionUnitVector.getScaledInstance( -1E-12 ) ), vReflect, incidentRay.power * reflectedPower, incidentRay.indexOfRefraction, incidentRay.oppositeIndexOfRefraction );
            Ray refracted = new Ray( point.getAddedInstance( incidentRay.directionUnitVector.getScaledInstance( +1E-12 ) ), vRefract, incidentRay.power * transmittedPower, incidentRay.oppositeIndexOfRefraction, incidentRay.indexOfRefraction );
            propagate( reflected, count + 1 );
            propagate( refracted, count + 1 );

            addRay( new LightRay( incidentRay.tail, intersection.getPoint(), n1, WAVELENGTH_RED / n1, incidentRay.power, laser.color.getValue(), waveWidth, 0, null, 0, true, false ) );
        }
        else {
            addRay( new LightRay( incidentRay.tail, incidentRay.tail.getAddedInstance( incidentRay.directionUnitVector.getScaledInstance( 1 ) )//1 meter long ray
                    , n1, WAVELENGTH_RED / n1, incidentRay.power, laser.color.getValue(), waveWidth, 0, null, 0, true, false ) );
        }
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
}
