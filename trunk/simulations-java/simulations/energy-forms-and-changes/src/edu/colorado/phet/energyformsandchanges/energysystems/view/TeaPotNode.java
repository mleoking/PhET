// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.HeaterCoolerNode;
import edu.colorado.phet.energyformsandchanges.common.view.BurnerStandNode;
import edu.colorado.phet.energyformsandchanges.energysystems.model.TeaPot;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo node that represents a teapot with a burner beneath it in the view.
 *
 * @author John Blanco
 */
public class TeaPotNode extends PositionableFadableModelElementNode {

    public TeaPotNode( TeaPot teaPot, final ModelViewTransform mvt ) {
        super( teaPot, mvt );

        // Create the burner.
        // TODO: i18n
        PNode heaterNode = new HeaterCoolerNode( teaPot.heatCoolAmount, "Heat", "Cool" ) {{
            setScale( 0.5 );
        }};

        // Create the tea pot.
        final PNode teaPotImageNode = new ModelElementImageNode( TeaPot.TEAPOT_IMAGE, mvt );

        // Create the burner stand.
        double burnerStandWidth = teaPotImageNode.getFullBoundsReference().getWidth() * 0.9;
        double burnerStandHeight = burnerStandWidth * 0.7;
        Rectangle2D burnerStandRect = new Rectangle2D.Double( teaPotImageNode.getFullBoundsReference().getCenterX() - burnerStandWidth / 2,
                                                              teaPotImageNode.getFullBoundsReference().getMaxY() - 35,
                                                              burnerStandWidth,
                                                              burnerStandHeight );
        PNode burnerStandNode = new BurnerStandNode( burnerStandRect, burnerStandWidth * 0.2 );

        // Do the layout.
        heaterNode.setOffset( burnerStandRect.getCenterX() - heaterNode.getFullBoundsReference().width / 2,
                              burnerStandRect.getMaxY() - heaterNode.getFullBoundsReference().getHeight() + 15 );

        // Add the nodes in the appropriate order for the desired layering affect.
        addChild( heaterNode );
        addChild( burnerStandNode );
        addChild( teaPotImageNode );
    }
}