// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.pictures;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeType;
import edu.colorado.phet.fractions.fractionsintro.intro.view.pieset.ShapeNode;

/**
 * @author Sam Reid
 */
public class BarPieceNode extends PieceNode {
    private PhetPPath barShadow;
    private ZeroOffsetNode barShadowNode;

    public BarPieceNode( final int pieceDenominator, final PictureSceneNode pictureSceneNode, final PhetPPath shape ) {
        super( pieceDenominator, pictureSceneNode, shape, ShapeType.BAR );

        barShadow = new PhetPPath( this.pathNode.getPathReference(), ShapeNode.SHADOW_PAINT );
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

    @Override protected void dragEnded() {
        hideShadow();
    }
}