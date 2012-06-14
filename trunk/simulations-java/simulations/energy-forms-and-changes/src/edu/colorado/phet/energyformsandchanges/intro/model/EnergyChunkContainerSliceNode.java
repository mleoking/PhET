// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.energyformsandchanges.intro.view.EnergyChunkNode;
import edu.umd.cs.piccolo.PNode;

/**
 * A node that represents a 2D surface on which energy chunks reside.  The
 * surface contains z-dimension information, and can thus be used to create an
 * effect of layering in order to get a bit of a 3D appearance.
 *
 * @author John Blanco
 */
public class EnergyChunkContainerSliceNode extends PNode {
    public EnergyChunkContainerSliceNode( final EnergyChunkContainerSlice energyChunkContainerSlice, final ModelViewTransform mvt ) {
        // Watch for energy chunks coming and going and add/remove nodes accordingly.
        energyChunkContainerSlice.energyChunkList.addElementAddedObserver( new VoidFunction1<EnergyChunk>() {
            public void apply( final EnergyChunk addedEnergyChunk ) {
                final PNode energyChunkNode = new EnergyChunkNode( addedEnergyChunk, mvt );
                addChild( energyChunkNode );
                energyChunkContainerSlice.energyChunkList.addElementRemovedObserver( new VoidFunction1<EnergyChunk>() {
                    public void apply( EnergyChunk removedEnergyChunk ) {
                        if ( removedEnergyChunk == addedEnergyChunk ) {
                            removeChild( energyChunkNode );
                            energyChunkContainerSlice.energyChunkList.removeElementRemovedObserver( this );
                        }
                    }
                } );
            }
        } );
    }
}
