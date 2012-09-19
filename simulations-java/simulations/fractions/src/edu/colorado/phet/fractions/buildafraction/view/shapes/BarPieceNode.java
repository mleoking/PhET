// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractions.fractionsintro.intro.view.pieset.ShapeNode;

/**
 * Piccolo node for a horizontal bar piece.
 *
 * @author Sam Reid
 */
public class BarPieceNode extends PieceNode {
    private final ZeroOffsetNode barShadowNode;
    private final int pieceDenominator;
    private final ShapeSceneNode shapeSceneNode;
    private final PhetPPath shape;

    public BarPieceNode( final int pieceDenominator, final ShapeSceneNode shapeSceneNode, final PhetPPath shape ) {
        super( pieceDenominator, shapeSceneNode, shape );
        this.pieceDenominator = pieceDenominator;
        this.shapeSceneNode = shapeSceneNode;
        this.shape = shape;

        final PhetPPath barShadow = new PhetPPath( this.pathNode.getPathReference(), ShapeNode.SHADOW_PAINT );
        barShadowNode = new ZeroOffsetNode( barShadow ) {{
            setOffset( getShadowOffset().getTranslateX(), getShadowOffset().getTranslateY() );
        }};
        addChild( new ZeroOffsetNode( this.pathNode ) );

        installInputListeners();
    }

    @Override protected void dragStarted() {
        showShadow();
    }

    @Override protected void hideShadow() {
        while ( getChildrenReference().contains( barShadowNode ) ) {
            removeChild( barShadowNode );
        }
    }

    @Override protected void showShadow() {
        hideShadow();
        addChild( 0, barShadowNode );
    }

    //For the "Fractions Lab" tab, creates a copy in the toolbox to emulate a seemingly endless supply of bar piece cards.
    @SuppressWarnings("unchecked") @Override public PieceNode copy() {
        BarPieceNode copy = new BarPieceNode( pieceDenominator, shapeSceneNode, PiePieceNode.copy( shape ) );
        copy.setInitialScale( initialScale );
        copy.setStack( stack ); //unchecked warning
        stack.cards = stack.cards.snoc( copy ); //unchecked warning
        copy.setPositionInStack( getPositionInStack() ); //unchecked warning
        return copy;
    }

    @Override protected void dragEnded() {
        hideShadow();
    }
}