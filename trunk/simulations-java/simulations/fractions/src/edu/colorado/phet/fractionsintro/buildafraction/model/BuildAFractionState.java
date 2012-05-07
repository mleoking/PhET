package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.F;
import fj.data.List;
import fj.data.Option;
import lombok.Data;

import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.buildafraction.controller.ModelUpdate;
import edu.colorado.phet.fractionsintro.buildafraction.view.ObjectID;

/**
 * Immutable model state for build a fraction.
 *
 * @author Sam Reid
 */
public @Data class BuildAFractionState {

    //The empty container that the user can drag around
    public final List<Container> containers;

    //The pieces that go in the containers
//    public final List<Piece> pieces;

    //The numbers that drag into the fraction numerator/denominator
    public final List<DraggableNumber> numbers;
//    public final List<Fraction> fractions;

    public final Mode mode;

    public BuildAFractionState withContainers( List<Container> containers ) { return new BuildAFractionState( containers, numbers, mode ); }

    public BuildAFractionState withNumbers( List<DraggableNumber> numbers ) { return new BuildAFractionState( containers, numbers, mode );}

    public BuildAFractionState withMode( final Mode mode ) { return new BuildAFractionState( containers, numbers, mode ); }

    public BuildAFractionState addEmptyContainer( final int numSegments, final Vector2D location ) { return addContainer( new Container( new DraggableObject( ObjectID.nextID(), location, true ), numSegments ) ); }

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
                withNumbers( numbers.map( new F<DraggableNumber, DraggableNumber>() {
                    @Override public DraggableNumber f( final DraggableNumber draggableNumber ) {
                        return draggableNumber.withDragging( false );
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

    public Option<Container> getContainer( final ObjectID id ) {
        return containers.find( new F<Container, Boolean>() {
            @Override public Boolean f( final Container container ) {
                return container.getID().equals( id );
            }
        } );
    }

    public BuildAFractionState startDraggingContainer( final ObjectID id ) {
        return withContainers( containers.map( new F<Container, Container>() {
            @Override public Container f( final Container container ) {
                return container.getID().equals( id ) ? container.withDragging( true ) : container;
            }
        } ) );
    }

    public BuildAFractionState startDraggingNumber( final ObjectID id ) {
        return withNumbers( numbers.map( new F<DraggableNumber, DraggableNumber>() {
            @Override public DraggableNumber f( final DraggableNumber container ) {
                return container.getID().equals( id ) ? container.withDragging( true ) : container;
            }
        } ) );
    }

    public static final ModelUpdate RELEASE_ALL = new ModelUpdate() {
        @Override public BuildAFractionState update( final BuildAFractionState state ) {
            return state.releaseAll();
        }
    };

    public BuildAFractionState addNumber( final DraggableNumber n ) { return withNumbers( numbers.snoc( n ) ); }

    public Option<DraggableNumber> getDraggableNumber( final ObjectID id ) {
        return numbers.find( new F<DraggableNumber, Boolean>() {
            @Override public Boolean f( final DraggableNumber draggableNumber ) {
                return draggableNumber.getID().equals( id );
            }
        } );
    }

    public BuildAFractionState dragNumbers( final Vector2D delta ) {
        return withNumbers( numbers.map( new F<DraggableNumber, DraggableNumber>() {
            @Override public DraggableNumber f( final DraggableNumber n ) {
                return n.isDragging() ? n.translate( delta ) : n;
            }
        } ) );
    }
}