package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.F;
import fj.data.List;
import fj.data.Option;
import lombok.Data;

import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.buildafraction.controller.ModelUpdate;

/**
 * Immutable model state for build a fraction.
 *
 * @author Sam Reid
 */
public @Data class BuildAFractionState {

    //The empty container that the user can drag around
    public final List<Container> containers;

    //The pieces that go in the containers
    public final List<Piece> pieces;

    //The numbers that drag into the fraction numerator/denominator
    public final List<DraggableNumber> draggableNumbers;

    //The fractions that numbers get dragged into as numerator/denominator
    public final List<DraggableFraction> draggableFractions;

    public final Mode mode;

    public static final ModelUpdate RELEASE_ALL = new ModelUpdate() {
        @Override public BuildAFractionState update( final BuildAFractionState state ) {
            return state.releaseAll();
        }
    };

    //Use a getter instead of inheritance to make it easy to match multiple different types
    public static <T> F<T, Boolean> matchID( final ObjectID id, final F<T, ObjectID> getID ) {
        return new F<T, Boolean>() {
            @Override public Boolean f( final T t ) {
                return getID.f( t ).equals( id );
            }
        };
    }

    public BuildAFractionState withContainers( List<Container> containers ) { return new BuildAFractionState( containers, pieces, draggableNumbers, draggableFractions, mode ); }

    public BuildAFractionState withDraggableNumbers( List<DraggableNumber> numbers ) { return new BuildAFractionState( containers, pieces, numbers, draggableFractions, mode );}

    public BuildAFractionState withDraggableFractions( List<DraggableFraction> draggableFractions ) { return new BuildAFractionState( containers, pieces, draggableNumbers, draggableFractions, mode );}

    public BuildAFractionState withMode( final Mode mode ) { return new BuildAFractionState( containers, pieces, draggableNumbers, draggableFractions, mode ); }

    public BuildAFractionState addEmptyContainer( final int numSegments, final Vector2D location ) { return addContainer( new Container( ContainerID.nextID(), new DraggableObject( location, true ), numSegments ) ); }

    public BuildAFractionState addContainer( Container container ) { return withContainers( containers.cons( container ) ); }

    public BuildAFractionState dragContainers( final Vector2D delta ) {
        return withContainers( containers.map( new F<Container, Container>() {
            @Override public Container f( final Container container ) {
                return container.isDragging() ? container.translate( delta ) : container;
            }
        } ) );
    }

    public BuildAFractionState releaseAll() {
        return withContainers( containers.map( new F<Container, Container>() {
            @Override public Container f( final Container container ) {
                return container.withDragging( false );
            }
        } ) ).
                withDraggableNumbers( draggableNumbers.map( new F<DraggableNumber, DraggableNumber>() {
                    @Override public DraggableNumber f( final DraggableNumber f ) {
                        return f.withDragging( false );
                    }
                } ) ).
                withDraggableFractions( draggableFractions.map( new F<DraggableFraction, DraggableFraction>() {
                    @Override public DraggableFraction f( final DraggableFraction f ) {
                        return f.withDragging( false );
                    }
                } ) );
    }

    public BuildAFractionState replaceContainer( final Container c, final Container replacement ) {
        return withContainers( containers.map( new F<Container, Container>() {
            @Override public Container f( final Container container ) {
                return container == c ? replacement : container;
            }
        } ) );
    }

    public Option<Container> getContainer( final ContainerID id ) { return containers.find( matchID( id, Container.ID ) ); }

    public BuildAFractionState startDraggingContainer( final ContainerID id ) {
        return withContainers( containers.map( new F<Container, Container>() {
            @Override public Container f( final Container container ) {
                return container.getID().equals( id ) ? container.withDragging( true ) : container;
            }
        } ) );
    }

    public BuildAFractionState startDraggingNumber( final DraggableNumberID id ) {
        return withDraggableNumbers( draggableNumbers.map( new F<DraggableNumber, DraggableNumber>() {
            @Override public DraggableNumber f( final DraggableNumber container ) {
                return container.getID().equals( id ) ? container.withDragging( true ) : container;
            }
        } ) );
    }

    public BuildAFractionState addNumber( final DraggableNumber n ) { return withDraggableNumbers( draggableNumbers.snoc( n ) ); }

    public Option<DraggableNumber> getDraggableNumber( final DraggableNumberID id ) { return draggableNumbers.find( matchID( id, DraggableNumber.ID ) ); }

    public BuildAFractionState dragNumbers( final Vector2D delta ) {
        return withDraggableNumbers( draggableNumbers.map( new F<DraggableNumber, DraggableNumber>() {
            @Override public DraggableNumber f( final DraggableNumber n ) {
                return n.isDragging() ? n.translate( delta ) : n;
            }
        } ) );
    }

    public BuildAFractionState addDraggableFraction( final DraggableFraction d ) { return withDraggableFractions( draggableFractions.snoc( d ) ); }

    public Option<DraggableFraction> getDraggableFraction( final FractionID id ) { return draggableFractions.find( matchID( id, DraggableFraction.ID ) ); }

    public BuildAFractionState dragFractions( final Vector2D delta ) {
        return withDraggableFractions( draggableFractions.map( new F<DraggableFraction, DraggableFraction>() {
            @Override public DraggableFraction f( final DraggableFraction draggableFraction ) {
                return draggableFraction.isDragging() ? draggableFraction.translate( delta ) : draggableFraction;
            }
        } ) );
    }

    public BuildAFractionState startDraggingFraction( final FractionID id ) {
        return withDraggableFractions( draggableFractions.map( new F<DraggableFraction, DraggableFraction>() {
            @Override public DraggableFraction f( final DraggableFraction f ) {
                return f.getID().equals( id ) ? f.withDragging( true ) : f;
            }
        } ) );
    }

    public Option<Piece> getPiece( final PieceID id ) { return pieces.find( matchID( id, Piece.ID ) ); }

    //Remove the number from the model and signify that it is attached to the fraction.
    public BuildAFractionState attachNumberToFraction( final DraggableNumberID number, final FractionID fraction, final boolean numerator ) {
        return withDraggableFractions( draggableFractions.map( new F<DraggableFraction, DraggableFraction>() {
            @Override public DraggableFraction f( final DraggableFraction f ) {
                return f.getID().equals( fraction ) && numerator ? f.withNumerator( Option.some( number ) ) :
                       f.getID().equals( fraction ) && !numerator ? f.withDenominator( Option.some( number ) ) :
                       f;
            }
        } ) ).withDraggableNumbers( draggableNumbers.map( new F<DraggableNumber, DraggableNumber>() {
            @Override public DraggableNumber f( final DraggableNumber n ) {
                return n.getID().equals( number ) ? n.attachToFraction( fraction, numerator ) : n;
            }
        } ) );
    }
}