// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Shape;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.ObservableList;

/**
 * This class represents a "slice" within a 2D container that contains a set of
 * energy chunks and can be used to add some limited 3D capabilities by having
 * some z-dimension information.  The slice consists of a 2D shape and a Z
 * value representing its position in Z space.
 *
 * @author John Blanco
 */
public class EnergyChunkContainerSlice {
    public ObservableList<EnergyChunk> energyChunkList = new ObservableList<EnergyChunk>();
    private final double zPosition;

    private Shape shape;

    public EnergyChunkContainerSlice( Shape shape, double zPosition, ObservableProperty<ImmutableVector2D> anchorPoint ) {
        this.shape = shape;
        this.zPosition = zPosition;

        // Monitor the anchor position and move the contained energy chunks to match.
        anchorPoint.addObserver( new ChangeObserver<ImmutableVector2D>() {
            public void update( ImmutableVector2D newPosition, ImmutableVector2D oldPosition ) {
                ImmutableVector2D translation = newPosition.getSubtractedInstance( oldPosition );
                EnergyChunkContainerSlice.this.shape = AffineTransform.getTranslateInstance( translation.getX(), translation.getY() ).createTransformedShape( EnergyChunkContainerSlice.this.shape );
                for ( EnergyChunk energyChunk : energyChunkList ) {
                    energyChunk.translate( translation );
                }
            }
        } );
    }

    public void addEnergyChunk( EnergyChunk energyChunk ) {
        energyChunk.zPosition.set( zPosition );
        energyChunkList.add( energyChunk );
    }

    public int getNumEnergyChunks() {
        return energyChunkList.size();
    }

    public void setShape( Shape shape ) {
        this.shape = shape;
    }

    public Shape getShape() {
        return shape;
    }
}
