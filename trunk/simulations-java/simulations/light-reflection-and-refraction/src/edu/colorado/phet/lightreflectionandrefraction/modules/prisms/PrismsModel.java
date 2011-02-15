// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.prisms;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lightreflectionandrefraction.model.LRRModel;
import edu.colorado.phet.lightreflectionandrefraction.model.LightRay;
import edu.colorado.phet.lightreflectionandrefraction.modules.intro.Medium;

import static java.lang.Math.*;

/**
 * @author Sam Reid
 */
public class PrismsModel extends LRRModel {
    private ArrayList<Prism> prisms = new ArrayList<Prism>();
    public final Property<Medium> outerMedium = new Property<Medium>( new Medium( new Rectangle2D.Double( -1, 0, 2, 1 ), AIR, colorMappingFunction.getValue().apply( AIR.index ) ) );
    public final Property<Medium> prismMedium = new Property<Medium>( new Medium( new Rectangle2D.Double( -1, -1, 2, 1 ), WATER, colorMappingFunction.getValue().apply( WATER.index ) ) );

    public PrismsModel() {
        prisms.add( new Prism( new Polygon( new ArrayList<ImmutableVector2D>() {{
            add( new ImmutableVector2D() );
            add( new ImmutableVector2D( 0, WAVELENGTH_RED * 10 ) );
            add( new ImmutableVector2D( WAVELENGTH_RED * 10, WAVELENGTH_RED * 10 ) );
            add( new ImmutableVector2D( WAVELENGTH_RED * 10, 0 ) );
        }} ) ) {{
            shape.addObserver( new SimpleObserver() {
                public void update() {
                    updateModel();
                }
            } );
        }} );
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
//            final LightRay incidentRay = new LightRay( tail, tail.getAddedInstance( unitVector.getScaledInstance( 1 ) )//1 meter long ray
//                    , n1, WAVELENGTH_RED / n1, sourcePower, laser.color.getValue(), sourceWaveWidth, 0, null, 0, true, false );
            propagate( new Ray( tail, new ImmutableVector2D( tail.toPoint2D(), new Point2D.Double() ) ), 0 );
//            addRay( incidentRay );
        }
    }

    private void propagate( Ray incidentRay, int count ) {
        double fakeIntensity = 1.0 / Math.pow( 2, count );//temporarily dim everything, later should be replaced by actual intensity rules
        if ( count > 10 ) {//binary recursion: 2^10 = 1024
            return;
        }
        Intersection intersection = getIntersection( incidentRay, prisms );
        ImmutableVector2D L = incidentRay.directionUnitVector;
        final double n1 = outerMedium.getValue().getIndexOfRefraction();
        final double n2 = prismMedium.getValue().getIndexOfRefraction();
        if ( intersection != null ) {
            ImmutableVector2D point = intersection.getPoint();
            ImmutableVector2D n = intersection.getUnitNormal();
            //See http://en.wikipedia.org/wiki/Snell's_law#Vector_form
            double cosTheta1 = n.dot( L.getScaledInstance( -1 ) );
            double cosTheta2 = sqrt( 1 - pow( n1 / n2, 2 ) * ( 1 - pow( cosTheta1, 2 ) ) );
            ImmutableVector2D vReflect = L.getAddedInstance( n.getScaledInstance( 2 * cosTheta1 ) );
            ImmutableVector2D vRefract = cosTheta1 > 0 ?
                                         L.getScaledInstance( n1 / n2 ).getAddedInstance( n.getScaledInstance( n1 / n2 * cosTheta1 - cosTheta2 ) ) :
                                         L.getScaledInstance( n1 / n2 ).getAddedInstance( n.getScaledInstance( n1 / n2 * cosTheta1 + cosTheta2 ) );
            Ray reflected = new Ray( point.getAddedInstance( incidentRay.directionUnitVector.getScaledInstance( -1E-12 ) ), vReflect );
            Ray refracted = new Ray( point.getAddedInstance( incidentRay.directionUnitVector.getScaledInstance( +1E-12 ) ), vRefract );
            propagate( reflected, count + 1 );
            propagate( refracted, count + 1 );

            addRay( new LightRay( incidentRay.tail, intersection.getPoint(), n1, WAVELENGTH_RED / n1, fakeIntensity, laser.color.getValue(), 1, 0, null, 0, true, false ) );
        }
        else {
            addRay( new LightRay( incidentRay.tail, incidentRay.tail.getAddedInstance( incidentRay.directionUnitVector.getScaledInstance( 1 ) )//1 meter long ray
                    , n1, WAVELENGTH_RED / n1, fakeIntensity, laser.color.getValue(), 1, 0, null, 0, true, false ) );
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
