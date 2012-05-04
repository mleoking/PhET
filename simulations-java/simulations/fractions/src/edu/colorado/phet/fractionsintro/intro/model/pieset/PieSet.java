// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset;

import fj.F;
import fj.F2;
import fj.P2;
import fj.data.List;
import fj.data.Option;
import lombok.Data;

import java.awt.geom.Area;
import java.util.Random;

import edu.colorado.phet.fractionsintro.intro.model.containerset.CellPointer;
import edu.colorado.phet.fractionsintro.intro.model.containerset.Container;
import edu.colorado.phet.fractionsintro.intro.model.containerset.ContainerSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.factories.SliceFactory;

import static edu.colorado.phet.fractions.util.FJUtils.ord;
import static fj.data.List.range;

/**
 * Immutable model representing the entire state at one instant, including the number and location of slices.  Note that pies can be square, circular, etc.
 *
 * @author Sam Reid
 */
@Data public class PieSet {

    //The denominator for this model
    public final int denominator;

    //The list of pies, which enumerates all of the (possibly empty) cells
    public final List<Pie> pies;

    //The movable slices
    public final List<Slice> slices;

    //The list of all cells, determined from the list of pies
    public final List<Slice> cells;

    //The factory, which is used to get locations for animating pieces to bucket and cells
    public final SliceFactory sliceFactory;

    public PieSet( int numPies, SliceFactory sliceFactory, long randomSeed ) {
        this( 1, sliceFactory.createEmptyPies( numPies, 1 ), sliceFactory.createSlicesForBucket( 1, 6, randomSeed ), sliceFactory );
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

    //Copy methods
    public PieSet withSlices( List<Slice> slices ) { return new PieSet( denominator, pies, slices, sliceFactory ); }

    //Find which cells are empty
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
                return !cellFilledNowOrSoon( slice );
            }
        } );
    }

    //Update the model by stepping in time, and creating a new one
    public PieSet stepInTime() {
        final List<Slice> slices = this.slices.map( new F<Slice, Slice>() {
            public Slice f( final Slice s ) {
                if ( s.dragging ) {

                    if ( getEmptyCells().length() > 0 ) {
                        Slice closest = getEmptyCells().minimum( ord( new F<Slice, Double>() {
                            @Override public Double f( final Slice slice ) {
                                return slice.getCenter().distance( s.getCenter() );
                            }
                        } ) );
                        final Slice rotated = s.rotateTowardTarget( closest.angle );

                        //Keep the center in the same place
                        return rotated.translate( s.getCenter().minus( rotated.getCenter() ) );
                    }
                    else {
                        return s;
                    }
                }
                else {
                    return s.stepAnimation();
                }
            }
        } );

        //If a piece reached the bucket, remove it from the model.  This prevents the buckets from overflowing, especially when the user just clicks the bucket pieces repeatedly
        List<P2<Slice, Slice>> zipped = this.slices.zip( slices );
        List<Slice> filtered = zipped.filter( new F<P2<Slice, Slice>, Boolean>() {
            @Override public Boolean f( P2<Slice, Slice> p ) {
                return !( isInBucket( p._2() ) && !isInBucket( p._1() ) );
            }
        } ).map( new F<P2<Slice, Slice>, Slice>() {
            @Override public Slice f( P2<Slice, Slice> p ) {
                return p._2();
            }
        } );
        return withSlices( filtered );
    }

    //True if a piece is in the cell, or animating toward the cell, but not if the user is dragging it (since it should not contribute to the sum if the user is dragging it)
    public boolean cellFilledNowOrSoon( final Slice cell ) {
        return slices.exists( new F<Slice, Boolean>() {
            public Boolean f( Slice m ) {
                return m.movingToward( cell ) || m.positionAndAngleEquals( cell ) && !m.dragging;
            }
        } );
    }

    //True if a piece is in the cell
    public boolean cellCurrentlyFilled( final Slice cell ) {
        return slices.exists( new F<Slice, Boolean>() {
            public Boolean f( Slice m ) {
                return m.positionAndAngleEquals( cell ) && !m.dragging;
            }
        } );
    }

    //Find which cell a slice should get dropped into 
    public Slice getDropTarget( final Slice s ) {
        if ( getEmptyCells().length() == 0 ) { return null; }
        final Slice closestCell = getEmptyCells().minimum( ord( new F<Slice, Double>() {
            @Override public Double f( final Slice slice ) {
                return slice.getCenter().distance( s.getCenter() );
            }
        } ) );

        //Only allow it if the shapes actually overlapped
        return closestCell != null && !( new Area( closestCell.getShape() ) {{intersect( new Area( s.getShape() ) );}}.isEmpty() ) ? closestCell : null;
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
                return cellFilledNowOrSoon( pie.cells.index( i ) );
            }
        } ) );
    }

    private Container pieToContainerLazy( final Pie pie ) {
        return new Container( pie.cells.length(), range( 0, pie.cells.length() ).filter( new F<Integer, Boolean>() {
            @Override public Boolean f( Integer i ) {
                return cellCurrentlyFilled( pie.cells.index( i ) );
            }
        } ) );
    }

    //When managing a mirror as for Equality lab, don't mark a cell as occupied until the animating piece has reached it.
    public ContainerSet toLazyContainerSet() {
        return new ContainerSet( denominator, pies.map( new F<Pie, Container>() {
            @Override public Container f( Pie p ) {
                return pieToContainerLazy( p );
            }
        } ) );
    }

    public PieSet animateBucketSliceToPie( CellPointer emptyCell, long randomSeed ) {

        final List<Slice> bucketSlices = getBucketSlices();

        //Randomly choose a slice as the prototype for where the slice should come from.
        //But do not actually delete the slice because there must seem to be an "infinite" supply from the bucket.
        final Slice bucketSlice = bucketSlices.index( new Random( randomSeed ).nextInt( bucketSlices.length() ) );

        final Slice target = sliceFactory.createPieCell( pies.length(), emptyCell.container, emptyCell.cell, denominator );
        return withSlices( slices.snoc( bucketSlice.animationTarget( target ) ) );
    }

    private List<Slice> getBucketSlices() {
        return slices.filter( new F<Slice, Boolean>() {
            @Override public Boolean f( Slice s ) {
                return isInBucket( s );
            }
        } );
    }

    public PieSet animateSliceToBucket( CellPointer cell, long randomSeed ) {

        //Cell that should be moved
        //May choose a slice that is on its way to a pie
        final Slice prototype = sliceFactory.createPieCell( pies.length(), cell.container, cell.cell, denominator );
        final Option<Slice> sliceOption = slices.find( new F<Slice, Boolean>() {
            @Override public Boolean f( Slice m ) {
                return ( m.position.equals( prototype.position ) && m.angle == prototype.angle ) || m.movingToward( prototype );
            }
        } );

        final Slice slice = sliceOption.isSome() ? sliceOption.some() : slices.find( new F<Slice, Boolean>() {
            @Override public Boolean f( Slice s ) {
                return s.position.getY() == prototype.position.getY();
            }
        } ).some();

        //Could be none if still animating
        return animateSliceToBucket( slice, randomSeed );
    }

    public PieSet animateSliceToBucket( final Slice slice, final long randomSeed ) {
        //Stepping the animation ensures that its center won't be at the center of a pie and hence it won't be identified as being "contained" in that pie
        final List<Slice> newSlices = slices.map( new F<Slice, Slice>() {
            @Override public Slice f( Slice m ) {
                return m == slice ? m.animationTarget( sliceFactory.createBucketSlice( denominator, randomSeed ) ).stepAnimation() : m;
            }
        } );

        return withSlices( newSlices );
    }

    //Find out whether the pie contains a movable slice instead of just empty cells--if so it will be drawn with a thicker darker background
    public boolean pieContainsSliceForCell( Slice cell ) {
        return getEmptyCells( getPie( cell ) ).length() < denominator;
    }

    //Find the pie that contains the cell
    private Pie getPie( final Slice cell ) {
        return pies.find( new F<Pie, Boolean>() {
            @Override public Boolean f( Pie p ) {
                return p.cells.find( new F<Slice, Boolean>() {
                    @Override public Boolean f( Slice s ) {
                        return s == cell;
                    }
                } ).isSome();
            }
        } ).some();
    }

    //Determine whether a slice is marked as in the bucket.  Not sure why the fudge term is needed, but without it there are bugs.
    public boolean isInBucket( Slice s ) {
        return s.animationTarget == null && !s.dragging && s.position.y >= sliceFactory.getBucketCenter().y - 50;
    }

    public int countFilledCells( Pie pie ) {
        return pie.cells.map( new F<Slice, Integer>() {
            @Override public Integer f( Slice cell ) {
                return cellCurrentlyFilled( cell ) ? 1 : 0;
            }
        } ).foldLeft( new F2<Integer, Integer, Integer>() {
            @Override public Integer f( Integer o, Integer integer ) {
                return o + integer;
            }
        }, 0 );
    }

    public boolean isInContainer( Slice slice ) {
        return !isInBucket( slice ) && !slice.dragging && slice.animationTarget == null;
    }

    //Only keep pieces sitting in the cell, for the scaled representation in Equality Lab
    public PieSet createScaledCopy() {
        return withSlices( slices.filter( new F<Slice, Boolean>() {
            @Override public Boolean f( Slice s ) {
                return sliceSittingInCell( s );
            }
        } ) );
    }

    private Boolean sliceSittingInCell( final Slice s ) {
        return cells.exists( new F<Slice, Boolean>() {
            @Override public Boolean f( Slice cell ) {
                return cell.positionAndAngleEquals( s );
            }
        } );
    }
}