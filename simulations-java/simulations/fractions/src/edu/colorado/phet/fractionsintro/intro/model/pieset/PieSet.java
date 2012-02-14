// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset;

import fj.F;
import fj.F2;
import fj.Ord;
import fj.Ordering;
import fj.data.List;
import fj.data.Option;
import lombok.Data;

import java.awt.Color;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Bucket;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.fractionsintro.intro.model.CellPointer;
import edu.colorado.phet.fractionsintro.intro.model.Container;
import edu.colorado.phet.fractionsintro.intro.model.ContainerSet;

import static fj.Function.curry;
import static fj.Ord.ord;
import static fj.data.List.iterableList;
import static fj.data.List.range;

/**
 * Immutable model representing the entire state at one instant, including the number and location of slices
 *
 * @author Sam Reid
 */
@Data public class PieSet {
    public final int denominator;
    public final List<Pie> pies;
    public final List<Slice> slices;

    //The list of all cells
    public final List<Slice> cells;

    public static final Dimension2DDouble STAGE_SIZE = new Dimension2DDouble( 1024, 768 );
    public static final Color BUCKET_COLOR = new Color( 136, 177, 240 );//A shade that looks good behind the green objects
    public static final Bucket BUCKET = new Bucket( STAGE_SIZE.width / 2, -STAGE_SIZE.height + 200, new Dimension2DDouble( 300, 100 ), BUCKET_COLOR, "" );
    public static final Random RANDOM = new Random();
    public static final double PIE_DIAMETER = 155;
    public static final double PIE_RADIUS = PIE_DIAMETER / 2;
    public static final int NUM_PIES = 6;
    public static final double PIE_SPACING = 10;

    public PieSet() {
        this( 1, createEmptyPies( 1 ), createSlicesForBucket( 1, 6 ) );
    }

    //Slices to put in the buckets
    private static List<Slice> createSlicesForBucket( final int denominator, final int numSlices ) {
        return iterableList( new ArrayList<Slice>() {{
            for ( int i = 0; i < numSlices; i++ ) {
                add( createBucketSlice( denominator ) );
            }
        }} );
    }

    //Put the pieces right in the center of the bucket hole.
    //They are pointing up so that when they rotate to align with the closest targets (the bottom ones) they don't have far to rotate, since the bottom targets are also pointing up
    public static Slice createBucketSlice( int denominator ) {
        final double x = BUCKET.getHoleShape().getBounds2D().getCenterX() + BUCKET.getPosition().getX();
        final double y = -BUCKET.getHoleShape().getBounds2D().getCenterY() - BUCKET.getPosition().getY();

        final double anglePerSlice = 2 * Math.PI / denominator;
        return new Slice( new ImmutableVector2D( x + ( PieSet.RANDOM.nextDouble() * 2 - 1 ) * PieSet.PIE_RADIUS, y - PieSet.PIE_RADIUS / 2 ), 3 * Math.PI / 2 - anglePerSlice / 2, anglePerSlice, PieSet.PIE_RADIUS, false, null );
    }

    //Create some cells for the empty pies
    private static List<Pie> createEmptyPies( final int denominator ) {
        ArrayList<Pie> pies = new ArrayList<Pie>() {{
            for ( int i = 0; i < NUM_PIES; i++ ) {
                ArrayList<Slice> cells = new ArrayList<Slice>();
                for ( int k = 0; k < denominator; k++ ) {
                    cells.add( createPieCell( i, k, denominator ) );
                }
                add( new Pie( iterableList( cells ) ) );
            }
        }};

        return iterableList( pies );
    }

    private static Slice createPieCell( int pie, int cell, int denominator ) {
        final double anglePerSlice = 2 * Math.PI / denominator;
        return new Slice( new ImmutableVector2D( PieSet.PIE_DIAMETER * ( pie + 1 ) + PieSet.PIE_SPACING * ( pie + 1 ) - 80, 250 ), anglePerSlice * cell, anglePerSlice, PieSet.PIE_DIAMETER / 2, false, null );
    }

    public PieSet( int denominator, List<Pie> pies, List<Slice> slices ) {
        this.denominator = denominator;
        this.pies = pies;
        this.slices = slices;

        this.cells = pies.bind( new F<Pie, List<Slice>>() {
            @Override public List<Slice> f( Pie p ) {
                return p.cells;
            }
        } );
    }

    public List<Slice> getEmptyCells() {
        return pies.bind( new F<Pie, List<Slice>>() {
            @Override public List<Slice> f( Pie pie ) {
                return getEmptyCells( pie );
            }
        } );
    }

    private List<Slice> getEmptyCells( Pie pie ) {
        return pie.cells.filter( new F<Slice, Boolean>() {
            @Override public Boolean f( Slice slice ) {
                return !cellFilled( slice );
            }
        } );
    }

    public PieSet stepInTime( final double simulationTimeChange ) {
        final List<Slice> slices = this.slices.map( new F<Slice, Slice>() {
            public Slice f( final Slice s ) {
                if ( s.dragging ) {

                    //TODO: make this minimum function a bit cleaner please?
                    Slice closest = getEmptyCells().minimum( ord( curry( new F2<Slice, Slice, Ordering>() {
                        public Ordering f( final Slice u1, final Slice u2 ) {
                            return Ord.<Comparable>comparableOrd().compare( u1.center().distance( s.center() ), u2.center().distance( s.center() ) );
                        }
                    } ) ) );

                    final Slice rotated = s.rotateTowardTarget( closest.angle );

                    //Keep the center in the same place
                    return rotated.translate( s.center().minus( rotated.center() ) );
                }
                else {
                    return s.stepAnimation();
                }
            }
        } );
        return slices( slices );
    }

    public PieSet slices( List<Slice> slices ) { return new PieSet( denominator, pies, slices ); }

    public boolean cellFilled( final Slice cell ) {
        return slices.exists( new F<Slice, Boolean>() {
            public Boolean f( Slice m ) {
                return m.movingToward( cell ) || m.positionAndAngleEquals( cell );
            }
        } );
    }

    //Find which cell a slice should get dropped into 
    public Slice getDropTarget( final Slice s ) {
        final Slice closestCell = getEmptyCells().minimum( ord( curry( new F2<Slice, Slice, Ordering>() {
            public Ordering f( final Slice u1, final Slice u2 ) {
                return Ord.<Comparable>comparableOrd().compare( u1.center().distance( s.center() ), u2.center().distance( s.center() ) );
            }
        } ) ) );

        //Only allow it if the shapes actually overlapped
        return closestCell != null && !( new Area( closestCell.shape() ) {{intersect( new Area( s.shape() ) );}}.isEmpty() ) ? closestCell : null;
    }

    public ContainerSet toContainerSet() {
        return new ContainerSet( denominator, pies.map( new F<Pie, Container>() {
            @Override public Container f( Pie p ) {
                return pieToContainer( p );
            }
        } ) );
    }

    private Container pieToContainer( final Pie pie ) {
        return new Container( pie.cells.length(), range( 0, pie.cells.length() ).filter( new F<Integer, Boolean>() {
            @Override public Boolean f( Integer i ) {
                return cellFilled( pie.cells.index( i ) );
            }
        } ) );
    }

    public static PieSet fromContainerSetState( ContainerSet containerSetState ) {
        final List<Pie> emptyPies = createEmptyPies( containerSetState.denominator );
        return new PieSet( containerSetState.denominator, emptyPies, createSlices( emptyPies, containerSetState ) );
    }

    private static List<Slice> createSlices( final List<Pie> emptyPies, final ContainerSet containerSetState ) {
        ArrayList<Slice> all = new ArrayList<Slice>();
        for ( int i = 0; i < containerSetState.containers.length(); i++ ) {
            Container c = containerSetState.containers.index( i );
            for ( Integer cell : c.filledCells ) {
                all.add( emptyPies.index( i ).cells.index( cell ) );
            }
        }
        return iterableList( all ).append( createSlicesForBucket( containerSetState.denominator, containerSetState.getEmptyCells().length() ) );
    }

    public PieSet animateBucketSliceToPie( CellPointer emptyCell ) {

        //Find a slice from the bucket, or one that is leaving the bucket

        //TODO: maybe check for bucket piece first, instead of piece going to the bucket as equal priority
        final Option<Slice> bucketSlice = slices.find( new F<Slice, Boolean>() {
            @Override public Boolean f( Slice m ) {
                final double bucketY = createBucketSlice( denominator ).tip.getY();
                return ( m.tip.getY() == bucketY && m.animationTarget == null ) ||
                       //Count piece going toward bucket as being in the bucket
                       ( m.animationTarget != null && m.animationTarget.position.getY() == bucketY );
            }
        } );

        //Could be none if still animating
        if ( bucketSlice.isSome() ) {
            final Slice target = createPieCell( emptyCell.container, emptyCell.cell, denominator );
            return slices( slices.map( new F<Slice, Slice>() {
                @Override public Slice f( Slice m ) {
                    return m == bucketSlice.some() ? m.animationTarget( new AnimationTarget( target.tip, target.angle ) ) : m;
                }
            } ) );
        }
        else {
            return this;
        }
    }

    public PieSet animateSliceToBucket( CellPointer cell ) {

        //Cell that should be moved
        //May choose a slice that is on its way to a pie
        final Slice prototype = createPieCell( cell.container, cell.cell, denominator );
        final Slice slice = slices.find( new F<Slice, Boolean>() {
            @Override public Boolean f( Slice m ) {
                return ( m.tip.equals( prototype.getTip() ) && m.angle == prototype.angle ) ||
                       m.movingToward( prototype );
            }
        } ).some();

        //Could be none if still animating
        final Slice target = createBucketSlice( denominator );
        return slices( slices.map( new F<Slice, Slice>() {
            @Override public Slice f( Slice m ) {

                //Stepping the animation ensures that its center won't be at the center of a pie and hence it won't be identified as being "contained" in that pie
                return m == slice ? m.animationTarget( new AnimationTarget( target.tip, target.angle ) ).stepAnimation() : m;
            }
        } ) );
    }
}