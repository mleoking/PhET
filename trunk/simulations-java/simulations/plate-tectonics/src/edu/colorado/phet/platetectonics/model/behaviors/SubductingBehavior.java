// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.PlateMotionPlate;
import edu.colorado.phet.platetectonics.model.PlateType;
import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.model.regions.Boundary;
import edu.colorado.phet.platetectonics.model.regions.Region;
import edu.colorado.phet.platetectonics.util.Side;

public class SubductingBehavior extends PlateBehavior {

    private float timeElapsed = 0;

    public static final float PLATE_SPEED = 30000f / 2; // meters per millions of years

    public SubductingBehavior( PlateMotionPlate plate, PlateMotionPlate otherPlate ) {
        super( plate, otherPlate );

        plate.getLithosphere().moveToFront();
        plate.getCrust().moveToFront();
    }

    @Override public void stepInTime( float millionsOfYears ) {

        timeElapsed += millionsOfYears;

        final int size = plate.getCrust().getTopBoundary().samples.size();

        ColumnResult[] result = new ColumnResult[size];
        for ( int i = 0; i < size; i++ ) {
            // TODO: add in offset to have the x=0 boundary at the right place after a certain amount of time
            result[i] = computeSubductingPosition( getT( i ), new ImmutableVector2F() );
        }

        for ( Region region : new Region[] { plate.getCrust(), plate.getLithosphere() } ) {
            boolean isCrust = region == plate.getCrust();

            for ( int i = 0; i < size; i++ ) {
                ImmutableVector2F top = isCrust ? result[i].crustTop : result[i].crustBottom;
                ImmutableVector2F bottom = isCrust ? result[i].crustBottom : result[i].lithosphereBottom;

                for ( int boundaryIndex = 0; boundaryIndex < region.getBoundaries().size(); boundaryIndex++ ) {
                    Boundary boundary = region.getBoundaries().get( boundaryIndex );

                    float ratio = ( (float) boundaryIndex ) / ( (float) ( region.getBoundaries().size() - 1 ) );

                    Sample sample = boundary.samples.get( i );

                    ImmutableVector3F newPosition = new ImmutableVector3F(
                            ( 1 - ratio ) * top.x + ( ratio ) * bottom.x,
                            ( 1 - ratio ) * top.y + ( ratio ) * bottom.y,
                            sample.getPosition().z
                    );

                    sample.setPosition( newPosition );
                }
            }
        }
    }

    // NOTE: relies on slices not getting removed
    public float getT( int columnIndex ) {
        float pieceWidth = plate.getSimpleChunkWidth();
        float staticT = pieceWidth * ( plate.getSide() == Side.LEFT

                                       // for the left, we want the right-most column to have 0, then descending to the left
                                       ? -( plate.getCrust().getTopBoundary().samples.size() - 1 - columnIndex )

                                       // simplier for the right
                                       : -columnIndex );
        return staticT + timeElapsed * PLATE_SPEED;
    }

    /*---------------------------------------------------------------------------*
    * new method of computing the subducting crust's position
    * see shapes.nb in the assets directory for more information and graphics
    *----------------------------------------------------------------------------*/

    public static class ColumnResult {
        public final ImmutableVector2F crustTop;
        public final ImmutableVector2F crustBottom;
        public final ImmutableVector2F lithosphereBottom;
        public final ImmutableVector2F lithosphereCenter;

        public ColumnResult( ImmutableVector2F crustTop, ImmutableVector2F crustBottom, ImmutableVector2F lithosphereBottom, ImmutableVector2F lithosphereCenter ) {
            this.crustTop = crustTop;
            this.crustBottom = crustBottom;
            this.lithosphereBottom = lithosphereBottom;
            this.lithosphereCenter = lithosphereCenter;
        }
    }

    private final float y0 = plate.getPlateType().getCrustTopY() - plate.getPlateType().getLithosphereThickness() / 2;
    private final float m = plate.getPlateType().getLithosphereThickness() / 2;
    private final float cb = plate.getPlateType().getLithosphereThickness() / 2 - plate.getPlateType().getCrustThickness();
    private final float totalAngle = (float) ( ( Math.PI / 4 ) * ( plate.getPlateType() == PlateType.OLD_OCEANIC ? 1.2 : 0.8 ) );

    private final float theta0 = totalAngle * 0.25f;
    private final float theta1 = totalAngle * 0.5f;
    private final float theta2 = totalAngle * 0.25f;

    private final float radius0 = 90000;
    private final float radius1 = 40000;
    private final float radius2 = 90000;

    private final float t0 = 0;
    private final float t1 = theta0 * radius0;
    private final float t2 = t1 + theta1 * radius1;
    private final float t3 = t2 + theta2 * radius2;

    private final ImmutableVector2F center0 = new ImmutableVector2F( 0, y0 - radius0 );

    private ImmutableVector2F p0( float t ) {
        return new ImmutableVector2F( -t, y0 );
    }

    private static final ImmutableVector2F value_pd0 = new ImmutableVector2F( -1, 0 );

    // TODO: refactor
    private ImmutableVector2F pd0( float t ) {
        return value_pd0;
    }

    private ImmutableVector2F p1( float t ) {
        float theta = (float) ( Math.PI / 2 + ( t - t0 ) / radius0 );
        return center0.plus( vectorFromAngle( theta ).times( radius0 ) );
    }

    private ImmutableVector2F pd1( float t ) {
        return bottomFromTangent( p1( t ).minus( center0 ).normalized() );
    }

    private final ImmutableVector2F center1 = p1( t1 ).plus( center0.minus( p1( t1 ) ).normalized().times( radius1 ) );

    private ImmutableVector2F p2( float t ) {
        float theta = (float) ( Math.PI / 2 + theta0 + ( t - t1 ) / radius1 );
        return center1.plus( vectorFromAngle( theta ).times( radius1 ) );
    }

    private ImmutableVector2F pd2( float t ) {
        return bottomFromTangent( p2( t ).minus( center1 ).normalized() );
    }

    private final ImmutableVector2F center2 = p2( t2 ).plus( center1.minus( p2( t2 ) ).normalized().times( radius2 ) );

    private ImmutableVector2F p3( float t ) {
        float theta = (float) ( Math.PI / 2 + theta0 + theta1 + ( t - t2 ) / radius2 );
        return center2.plus( vectorFromAngle( theta ).times( radius2 ) );
    }

    private ImmutableVector2F pd3( float t ) {
        return bottomFromTangent( p3( t ).minus( center2 ).normalized() );
    }

    private final ImmutableVector2F dir4 = vectorFromAngle( (float) ( totalAngle + Math.PI ) );

    private final ImmutableVector2F p3oft3 = p3( t3 );

    private ImmutableVector2F p4( float t ) {
        return p3oft3.plus( dir4.times( t - t3 ) );
    }

    private ImmutableVector2F pd4( float t ) {
        return dir4;
    }

    public ColumnResult computeSubductingPosition( float t, ImmutableVector2F offset ) {
        ImmutableVector2F position;
        ImmutableVector2F derivative;

        if ( t < t0 ) {
            position = p0( t );
            derivative = pd0( t );
        }
        else if ( t < t1 ) {
            position = p1( t );
            derivative = pd1( t );
        }
        else if ( t < t2 ) {
            position = p2( t );
            derivative = pd2( t );
        }
        else if ( t < t3 ) {
            position = p3( t );
            derivative = pd3( t );
        }
        else {
            position = p4( t );
            derivative = pd4( t );
        }

        ImmutableVector2F tangent = topFromTangent( derivative );

        // add in the offset
        position = position.plus( offset );

        // if the plate is the left side, we actually switch it here
        if ( plate.getSide() == Side.LEFT ) {
            position = new ImmutableVector2F( -position.x, position.y );
            tangent = new ImmutableVector2F( -tangent.x, tangent.y );
        }

        return new ColumnResult(
                position.plus( tangent.times( m ) ),
                position.plus( tangent.times( cb ) ),
                position.plus( tangent.times( -m ) ),
                position
        );
    }

    private static ImmutableVector2F topFromTangent( ImmutableVector2F v ) {
        return new ImmutableVector2F( v.y, -v.x );
    }

    private static ImmutableVector2F bottomFromTangent( ImmutableVector2F v ) {
        return topFromTangent( v ).negate();
    }

    private static ImmutableVector2F vectorFromAngle( float angle ) {
        return new ImmutableVector2F( Math.cos( angle ), Math.sin( angle ) );
    }
}
