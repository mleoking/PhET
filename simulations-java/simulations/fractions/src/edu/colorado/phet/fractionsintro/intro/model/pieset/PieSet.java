// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset;

import fj.F;
import fj.F2;
import fj.Ord;
import fj.Ordering;
import fj.data.List;
import fj.data.Option;
import lombok.Data;

import java.awt.geom.Area;

import edu.colorado.phet.fractionsintro.intro.model.CellPointer;
import edu.colorado.phet.fractionsintro.intro.model.Container;
import edu.colorado.phet.fractionsintro.intro.model.ContainerSet;

import static fj.Function.curry;
import static fj.Ord.ord;
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
    public final List<Slice> cells;              //The list of all cells
    public final SliceFactory sliceFactory;

    public PieSet( SliceFactory sliceFactory ) {
        this( 1, sliceFactory.createEmptyPies( 1 ), sliceFactory.createSlicesForBucket( 1, 6 ), sliceFactory );
    }

    public PieSet( int denominator, List<Pie> pies, List<Slice> slices, SliceFactory sliceFactory ) {
        this.denominator = denominator;
        this.pies = pies;
        this.slices = slices;
        this.sliceFactory = sliceFactory;

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

    public PieSet slices( List<Slice> slices ) { return new PieSet( denominator, pies, slices, sliceFactory ); }

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

    public PieSet animateBucketSliceToPie( CellPointer emptyCell ) {

        //Find a slice from the bucket, or one that is leaving the bucket
        //Reverse the list so that slices are taken from the front in z-ordering
        //TODO: maybe check for bucket piece first, instead of piece going to the bucket as equal priority
        final Option<Slice> bucketSlice = slices.reverse().find( new F<Slice, Boolean>() {
            @Override public Boolean f( Slice m ) {
                final double bucketY = sliceFactory.createBucketSlice( denominator ).tip.getY();
                return ( m.tip.getY() == bucketY && m.animationTarget == null ) ||
                       //Count piece going toward bucket as being in the bucket
                       ( m.animationTarget != null && m.animationTarget.position.getY() == bucketY );
            }
        } );

        //Could be none if still animating
        if ( bucketSlice.isSome() ) {
            final Slice target = sliceFactory.createPieCell( emptyCell.container, emptyCell.cell, denominator );
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
        final Slice prototype = sliceFactory.createPieCell( cell.container, cell.cell, denominator );
        final Slice slice = slices.find( new F<Slice, Boolean>() {
            @Override public Boolean f( Slice m ) {
                return ( m.tip.equals( prototype.getTip() ) && m.angle == prototype.angle ) ||
                       m.movingToward( prototype );
            }
        } ).some();

        //Could be none if still animating
        final Slice target = sliceFactory.createBucketSlice( denominator );
        return slices( slices.map( new F<Slice, Slice>() {
            @Override public Slice f( Slice m ) {

                //Stepping the animation ensures that its center won't be at the center of a pie and hence it won't be identified as being "contained" in that pie
                return m == slice ? m.animationTarget( new AnimationTarget( target.tip, target.angle ) ).stepAnimation() : m;
            }
        } ) );
    }
}