// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.awt.Image;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;

/**
 * Base class for energy sources, converters, and users.
 *
 * @author John Blanco
 */
public abstract class EnergySystemElement extends PositionableFadableModelElement {

    // List of energy chunks contained and managed by this energy system element.
    public final ObservableList<EnergyChunk> energyChunkList = new ObservableList<EnergyChunk>();

    // Image that is used as an icon in the view to represent this element.
    private final Image iconImage;

    // State variable that tracks whether this element is currently active,
    // meaning that it is generating, transforming, and/or consuming energy.
    private final BooleanProperty active = new BooleanProperty( false );

    protected EnergySystemElement( Image iconImage ) {
        this.iconImage = iconImage;

        // Move all energy chunks with this element as it moves.
        getObservablePosition().addObserver( new ChangeObserver<Vector2D>() {
            public void update( Vector2D newPosition, Vector2D oldPosition ) {
                Vector2D deltaPosition = newPosition.minus( oldPosition );
                for ( EnergyChunk energyChunk : energyChunkList ) {
                    energyChunk.translate( deltaPosition );
                }
            }
        } );
    }

    public Image getIconImage() {
        return iconImage;
    }

    public abstract IUserComponent getUserComponent();

    public void activate() {
        active.set( true );
    }

    public void deactivate() {
        active.set( false );

        // All energy chunks go away when an energy system element is deactivated.
        clearEnergyChunks();
    }

    public ObservableProperty<Boolean> getObservableActiveState() {
        return active;
    }

    public boolean isActive() {
        return active.get();
    }

    /**
     * Remove all energy chunks being managed by this energy system element.
     */
    public void clearEnergyChunks() {
        energyChunkList.clear();
    }
}
