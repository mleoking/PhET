/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * TotalChargeNode displays the total charge on the bead.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TotalChargeNode extends AbstractChargeNode {

    public TotalChargeNode( Bead bead, Laser laser, ModelViewTransform modelViewTransform ) {
        super( bead, laser, modelViewTransform );
    }
    
    protected void initialize() {
        PText textNode = new PText( "T" );
        addChild( textNode );
        final double x = -textNode.getFullBoundsReference().getWidth() / 2;
        final double y = -textNode.getFullBoundsReference().getHeight() / 2;
        textNode.setOffset( x, y );
    }
    
}
