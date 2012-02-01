// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.Frame;

import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawModel;
import edu.colorado.phet.beerslawlab.common.view.BLLCanvas;
import edu.colorado.phet.beerslawlab.common.view.SoluteChoiceNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Beer's Law" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeersLawCanvas extends BLLCanvas {

    public BeersLawCanvas( final BeersLawModel model, Frame parentFrame ) {

        // Nodes
        PNode soluteChoiceNode = new SoluteChoiceNode( model.getSolutes(), model.solute );

        // Rendering order
        {
            addChild( soluteChoiceNode );
        }

        // layout
        {
            final double xMargin = 20;
            final double yMargin = 20;
            // solution combo box at top center
            soluteChoiceNode.setOffset( ( getStageSize().getWidth() - soluteChoiceNode.getFullBoundsReference().getWidth() ) / 2,
                                        yMargin );
        }
    }
}
