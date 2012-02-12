// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.slicemodel;

import fj.F;
import fj.F2;
import fj.Ord;
import fj.Ordering;
import fj.data.List;
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
    public final int numerator;
    public final int denominator;
    public final List<Pie> pies;
    public final List<MovableSlice> slices;

    //The list of all cells
    public final List<Slice> cells;

    public static final Dimension2DDouble STAGE_SIZE = new Dimension2DDouble( 1024, 768 );
    public static final Bucket bucket = new Bucket( STAGE_SIZE.width / 2, -STAGE_SIZE.height + 200, new Dimension2DDouble( 300, 100 ), Color.green, "pieces" );
    public static final Random RANDOM = new Random();
    public static final double pieDiameter = 155;
    public static final double radius = pieDiameter / 2;
    public static final int numPies = 6;
    public static final double pieSpacing = 10;

    public PieSet() {
        this( 0, 1, createEmptyPies( 1 ), createSlicesForBucket( 1, 6 ) );
    }

    //Slices to put in the buckets
    private static List<MovableSlice> createSlicesForBucket( final int denominator, final int numSlices ) {
        return iterableList( new ArrayList<MovableSlice>() {{
            for ( int i = 0; i < numSlices; i++ ) {
                add( new MovableSlice( createBucketSlice( denominator ), null ) );
            }
        }} );
    }

    //Put the pieces right in the center of the bucket hole.
    //They are pointing up so that when they rotate to align with the closest targets (the bottom ones) they don't have far to rotate, since the bottom targets are also pointing up
    public static Slice createBucketSlice( int denominator ) {
        final double x = bucket.getHoleShape().getBounds2D().getCenterX() + bucket.getPosition().getX();
        final double y = -bucket.getHoleShape().getBounds2D().getCenterY() - bucket.getPosition().getY();

        final double anglePerSlice = 2 * Math.PI / denominator;
        return new Slice( new ImmutableVector2D( x + ( PieSet.RANDOM.nextDouble() * 2 - 1 ) * PieSet.radius, y - PieSet.radius / 2 ), 3 * Math.PI / 2 - anglePerSlice / 2, anglePerSlice, PieSet.radius, false, null );
    }

    //Create some cells for the empty pies
    private static List<Pie> createEmptyPies( final int denominator ) {
        ArrayList<Pie> pies = new ArrayList<Pie>() {{
            for ( int i = 0; i < numPies; i++ ) {
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
        return new Slice( new ImmutableVector2D( PieSet.pieDiameter * ( pie + 1 ) + PieSet.pieSpacing * ( pie + 1 ) - 80, 250 ), anglePerSlice * cell, anglePerSlice, PieSet.pieDiameter / 2, false, null );
    }

    public PieSet( int numerator, int denominator, List<Pie> pies, List<MovableSlice> slices ) {
        this.numerator = numerator;
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
        final List<MovableSlice> slices = this.slices.map( new F<MovableSlice, MovableSlice>() {
            public MovableSlice f( final MovableSlice s ) {
                if ( s.dragging() ) {

                    //TODO: make this minimum function a bit cleaner please?
                    Slice closest = getEmptyCells().minimum( ord( curry( new F2<Slice, Slice, Ordering>() {
                        public Ordering f( final Slice u1, final Slice u2 ) {
                            return Ord.<Comparable>comparableOrd().compare( u1.center().distance( s.center() ), u2.center().distance( s.center() ) );
                        }
                    } ) ) );

                    final MovableSlice rotated = s.rotateTowardTarget( closest.angle );

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

    public PieSet slices( List<MovableSlice> slices ) { return new PieSet( numerator, denominator, pies, slices ); }

    public boolean cellFilled( final Slice cell ) {
        return slices.exists( new F<MovableSlice, Boolean>() {
            public Boolean f( MovableSlice m ) {
                return m.container == cell || m.movingToward( cell );
            }
        } );
    }

    //Find which cell a slice should get dropped into 
    public Slice getDropTarget( final MovableSlice s ) {
        final Slice closestCell = getEmptyCells().minimum( ord( curry( new F2<Slice, Slice, Ordering>() {
            public Ordering f( final Slice u1, final Slice u2 ) {
                return Ord.<Comparable>comparableOrd().compare( u1.center().distance( s.center() ), u2.center().distance( s.center() ) );
            }
        } ) ) );

        //Only allow it if the shapes actually overlapped
        return closestCell != null && !( new Area( closestCell.shape() ) {{intersect( new Area( s.shape() ) );}}.isEmpty() ) ? closestCell : null;
    }

    public ContainerSet toContainerState() {
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

    public PieSet denominator( int denominator ) {
        return fromContainerSetState( toContainerState().denominator( denominator ) );
    }

    public static PieSet fromContainerSetState( ContainerSet containerSetState ) {
        final List<Pie> emptyPies = createEmptyPies( containerSetState.denominator );
        return new PieSet( containerSetState.numerator, containerSetState.denominator, emptyPies, createSlices( emptyPies, containerSetState ) );
    }

    private static List<MovableSlice> createSlices( final List<Pie> emptyPies, final ContainerSet containerSetState ) {
        ArrayList<MovableSlice> all = new ArrayList<MovableSlice>();
        for ( int i = 0; i < containerSetState.containers.length(); i++ ) {
            Container c = containerSetState.containers.index( i );
            for ( Integer cell : c.filledCells ) {
                final Slice slice = emptyPies.index( i ).cells.index( cell );
                all.add( new MovableSlice( slice, slice ) );
            }
        }
        return iterableList( all ).append( createSlicesForBucket( containerSetState.denominator, containerSetState.getEmptyCells().length() ) );
    }

    public PieSet animateBucketSliceToPie( CellPointer emptyCell ) {

        //Find a slice from the bucket
        //TODO: Should find any slice at the bucket or heading toward the bucket (if the user toggles the buttons fast)
        final MovableSlice bucketSlice = slices.find( new F<MovableSlice, Boolean>() {
            @Override public Boolean f( MovableSlice m ) {
                return m.tip().getY() == createBucketSlice( denominator ).tip.getY();
            }
        } ).some();
        final Slice target = createPieCell( emptyCell.container, emptyCell.cell, denominator );
        return slices( slices.map( new F<MovableSlice, MovableSlice>() {
            @Override public MovableSlice f( MovableSlice m ) {
                return m == bucketSlice ? bucketSlice.animationTarget( new AnimationTarget( target.tip, target.angle ) ) : m;
            }
        } ) );
    }
}