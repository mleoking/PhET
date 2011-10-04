// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.MessengerRna;
import edu.umd.cs.piccolo.PNode;

/**
 * View representation for messenger RNA.  This is done differently from most
 * if not all of the other mobile biomolecules because it is represented as an
 * unclosed shape.
 *
 * @author John Blanco
 */
public class MessengerRnaNode extends MobileBiomoleculeNode {

    /**
     * Constructor.
     *
     * @param mvt
     * @param messengerRna
     */
    public MessengerRnaNode( final ModelViewTransform mvt, final MessengerRna messengerRna ) {
        super( mvt, messengerRna, new BasicStroke( 2 ) );
        addChild( new PlacementHintNode( mvt, messengerRna.ribosomePlacementHint ) );
    }

    // TODO: Started on this, decided not to use it for now, delete if never used.  It is intended for debugging the mRNA shape.
    private static class PointMassNode {
        private static final int DIAMETER = 5;
        private static final Shape SHAPE = new Ellipse2D.Double( -DIAMETER / 2, -DIAMETER / 2, DIAMETER, DIAMETER );
        private static final Color COLOR = Color.RED;
        public final MessengerRna.PointMass pointMass;
        private final ModelViewTransform mvt;
        private final PNode representation;

        public PointMassNode( MessengerRna.PointMass pointMass, ModelViewTransform mvt ) {
            this.pointMass = pointMass;
            this.mvt = mvt;
            representation = new PhetPPath( SHAPE, COLOR );
            updatePosition();
        }

        public void updatePosition() {
            representation.setOffset( mvt.modelToView( pointMass.getPosition() ) );
        }
    }
}
