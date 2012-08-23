// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.HeaterCoolerNode;
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

        // Add the burner.
        // TODO: i18n
        PNode heaterNode = new HeaterCoolerNode( teaPot.heatCoolAmount, "Heat", "Cool" ) {{
            setScale( 0.5 );
        }};
        addChild( heaterNode );

        // Add the tea pot.
        final PNode teaPotImageNode = new ModelElementImageNode( TeaPot.TEAPOT_IMAGE, mvt );
        addChild( teaPotImageNode );

        // Position the heater below the tea pot.
        heaterNode.setOffset( teaPotImageNode.getFullBoundsReference().getCenterX() - heaterNode.getFullBoundsReference().width / 2,
                              teaPotImageNode.getFullBoundsReference().getMaxY() + 30 );
    }
}