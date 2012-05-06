package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.F;
import fj.data.List;
import fj.data.Option;
import lombok.Data;

import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.buildafraction.controller.ModelUpdate;
import edu.colorado.phet.fractionsintro.buildafraction.view.ContainerID;

/**
 * Immutable model state for build a fraction.
 *
 * @author Sam Reid
 */
public @Data class BuildAFractionState {

    //The empty container that the user can drag around
    public final List<Container> containers;

    public BuildAFractionState withContainers( List<Container> containers ) { return new BuildAFractionState( containers ); }

    public BuildAFractionState addEmptyContainer( final int numSegments, final Vector2D location ) { return addContainer( new Container( ContainerID.nextID(), numSegments, location, true ) ); }

    public BuildAFractionState addContainer( Container container ) { return withContainers( containers.cons( container ) ); }

    public BuildAFractionState drag( final Vector2D delta ) {
        return withContainers( containers.map( new F<Container, Container>() {
            @Override public Container f( final Container container ) {
                return container.dragging ? container.translate( delta ) : container;
            }
        } ) );
    }

    public BuildAFractionState releaseAll() {
        return withContainers( containers.map( new F<Container, Container>() {
            @Override public Container f( final Container container ) {
                return container.withDragging( false );
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

    public Option<Container> getContainer( final ContainerID id ) {
        return containers.find( new F<Container, Boolean>() {
            @Override public Boolean f( final Container container ) {
                return container.id.equals( id );
            }
        } );
    }

    public BuildAFractionState startDragging( final ContainerID id ) {
        return withContainers( containers.map( new F<Container, Container>() {
            @Override public Container f( final Container container ) {
                return container.id.equals( id ) ? container.withDragging( true ) : container;
            }
        } ) );
    }

    public static final ModelUpdate RELEASE_ALL = new ModelUpdate() {
        @Override public BuildAFractionState update( final BuildAFractionState state ) {
            return state.releaseAll();
        }
    };
}