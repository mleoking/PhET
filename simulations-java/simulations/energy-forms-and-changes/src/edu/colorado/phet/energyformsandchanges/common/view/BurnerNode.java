// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.HeaterCoolerNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.intro.model.Burner;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo node that represents a burner in the view.
 *
 * @author John Blanco
 */
public class BurnerNode extends PNode {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    public static final double EDGE_TO_HEIGHT_RATIO = 0.2;

    // For debug purposes.
    private static final boolean SHOW_2D_RECT = true;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    protected final HeaterCoolerNode heaterCoolerNode;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public BurnerNode( final Burner burner, final ModelViewTransform mvt ) {

        if ( SHOW_2D_RECT ) {
            addChild( new PhetPPath( mvt.modelToViewRectangle( burner.getOutlineRect() ), new BasicStroke( 1 ), Color.RED ) );
        }

        // Get a version of the rectangle that defines the burner size and
        // location in the view.
        final Rectangle2D burnerViewRect = mvt.modelToView( burner.getOutlineRect() ).getBounds2D();

        // Add the heater-cooler node to the center bottom.
        heaterCoolerNode = new HeaterCoolerNode( burner.heatCoolLevel, EnergyFormsAndChangesResources.Strings.HEAT, EnergyFormsAndChangesResources.Strings.COOL ) {{
            setScale( mvt.modelToViewDeltaX( burner.getOutlineRect().getWidth() ) * 0.7 / getFullBoundsReference().width );
            setOffset( burnerViewRect.getX() + burnerViewRect.getWidth() / 2 - getFullBoundsReference().width / 2,
                       burnerViewRect.getMaxY() - getFullBoundsReference().height * 0.9 );
        }};
        addChild( heaterCoolerNode );

        // Add the stand that goes around and above the burner.
        addChild( new BurnerStandNode( burnerViewRect, burnerViewRect.getHeight() * EDGE_TO_HEIGHT_RATIO ) );

        // Watch for energy chunks coming and going and add/remove nodes accordingly.
        burner.energyChunkList.addElementAddedObserver( new VoidFunction1<EnergyChunk>() {
            public void apply( final EnergyChunk addedEnergyChunk ) {
                final PNode energyChunkNode = new EnergyChunkNode( addedEnergyChunk, mvt );
                addChild( energyChunkNode );
                burner.energyChunkList.addElementRemovedObserver( new VoidFunction1<EnergyChunk>() {
                    public void apply( EnergyChunk removedEnergyChunk ) {
                        if ( removedEnergyChunk == addedEnergyChunk ) {
                            removeChild( energyChunkNode );
                            burner.energyChunkList.removeElementRemovedObserver( this );
                        }
                    }
                } );
            }
        } );
    }
}
