// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration;

import java.awt.Color;
import java.awt.Frame;

import edu.colorado.phet.beerslawlab.control.SoluteControlNode;
import edu.colorado.phet.beerslawlab.view.BLLCanvas;
import edu.colorado.phet.beerslawlab.view.DropperNode;
import edu.colorado.phet.beerslawlab.view.ShakerNode;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;

/**
 * Canvas for the "Concentration" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationCanvas extends BLLCanvas {

    public ConcentrationCanvas( final ConcentrationModel model, Frame parentFrame ) {

        // Solute controls
        SoluteControlNode soluteControlNode = new SoluteControlNode( model.getSolutes(), model.solution.solute, model.soluteForm );
        // Shaker
        ShakerNode shakerNode = new ShakerNode( model.solution.solute, model.soluteForm );
        // Dropper
        DropperNode dropperNode = new DropperNode( model.solution.solute, model.soluteForm );
        // Reset All button
        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( model, parentFrame, 18, Color.BLACK, Color.ORANGE ) {{
            setConfirmationEnabled( false );
        }};

        // rendering order
        {
            addChild( shakerNode );
            addChild( dropperNode );
            addChild( resetAllButtonNode );
            addChild( soluteControlNode ); // on top, because it has a combo box popup
        }

        // layout
        {
            final double xMargin = 20;
            final double yMargin = 20;
            // upper right
            soluteControlNode.setOffset( getStageSize().getWidth() - soluteControlNode.getFullBoundsReference().getWidth() - xMargin, yMargin );
            // upper center
            shakerNode.setOffset( soluteControlNode.getFullBoundsReference().getMinX() - shakerNode.getFullBoundsReference().getWidth() - 40, yMargin );
            // upper center
            dropperNode.setOffset( soluteControlNode.getFullBoundsReference().getMinX() - dropperNode.getFullBoundsReference().getWidth() - 40, yMargin );
            // lower right
            resetAllButtonNode.setOffset( getStageSize().getWidth() - resetAllButtonNode.getFullBoundsReference().getWidth() - xMargin,
                                          getStageSize().getHeight() - resetAllButtonNode.getFullBoundsReference().getHeight() - yMargin );
        }
        //TODO enable this after more layout is fleshed out
//        scaleRootNodeToFitStage();
//        centerRootNodeOnStage();
    }
}
