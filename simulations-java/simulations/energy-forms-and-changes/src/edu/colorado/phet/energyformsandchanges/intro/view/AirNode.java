// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.intro.model.Air;
import edu.colorado.phet.energyformsandchanges.intro.model.EnergyChunk;
import edu.umd.cs.piccolo.PNode;

/**
 * @author John Blanco
 */
public class AirNode extends PNode {

    private static final boolean SHOW_BOUNDS = true;

    public AirNode( final Air air, final ModelViewTransform mvt ) {
        if ( SHOW_BOUNDS ) {
            addChild( new PhetPPath( mvt.modelToView( air.getThermalContactArea().getBounds() ), new BasicStroke( 1 ), Color.RED ) );
        }

        // Create a layer where energy chunks will be placed.
        final PNode energyChunkLayer = new PNode();
        addChild( energyChunkLayer );

        // Watch for energy chunks coming and going and add/remove nodes accordingly.
        air.getEnergyChunkList().addElementAddedObserver( new VoidFunction1<EnergyChunk>() {
            public void apply( final EnergyChunk addedEnergyChunk ) {
                final PNode energyChunkNode = new EnergyChunkNode( addedEnergyChunk, mvt );
                energyChunkLayer.addChild( energyChunkNode );
                air.getEnergyChunkList().addElementRemovedObserver( new VoidFunction1<EnergyChunk>() {
                    public void apply( EnergyChunk removedEnergyChunk ) {
                        if ( removedEnergyChunk == addedEnergyChunk ) {
                            energyChunkLayer.removeChild( energyChunkNode );
                            air.getEnergyChunkList().removeElementRemovedObserver( this );
                        }
                    }
                } );
            }
        } );
    }
}
