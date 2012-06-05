package edu.colorado.phet.fractionsintro.buildafraction.view;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.fractions.FractionsResources.Strings.MY_FRACTIONS;
import static edu.colorado.phet.fractionsintro.buildafraction_functional.view.BuildAFractionCanvas.controlPanelStroke;

/**
 * Canvas for the build a fraction tab.
 *
 * @author Sam Reid
 */
public class BuildAFractionCanvas extends AbstractFractionsCanvas implements DragContext {
    public static final Paint TRANSPARENT = new Color( 0, 0, 0, 0 );
    private final FractionGraphic emptyFractionGraphic;

    public BuildAFractionCanvas( final BuildAFractionModel model ) {
//        scoreBoxes = model.state.get().targetCells.map( new F<TargetCell, ScoreBoxNode>() {
//            @Override public ScoreBoxNode f( final TargetCell targetCell ) {
//
//                //If these representationBox are all the same size, then 2-column layout will work properly
//                final int numerator = targetCell.index + 1;
//                PNode representationBox = new PatternNode( FilledPattern.sequentialFill( Pattern.sixFlower( 18 ), numerator ), Color.red );
//                return new ScoreBoxNode( numerator, 6, representationBox, model, targetCell );
//            }
//        } );
//        final Collection<ScoreBoxNode> nodes = scoreBoxes.toCollection();
        final VBox rightControlPanel = new VBox( new PhetPText( MY_FRACTIONS, AbstractFractionsCanvas.CONTROL_FONT ) ) {{
            setOffset( AbstractFractionsCanvas.STAGE_SIZE.width - getFullWidth() - AbstractFractionsCanvas.INSET, AbstractFractionsCanvas.INSET );
        }};
        addChild( rightControlPanel );

        //Add a piece container toolbox the user can use to get containers
        final RichPNode toolboxNode = new RichPNode() {{
            final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( 0, 0, 700, 160, 30, 30 ), edu.colorado.phet.fractionsintro.buildafraction_functional.view.BuildAFractionCanvas.CONTROL_PANEL_BACKGROUND, controlPanelStroke, Color.darkGray );
            addChild( border );
            setOffset( ( AbstractFractionsCanvas.STAGE_SIZE.width - rightControlPanel.getFullWidth() ) / 2 - this.getFullWidth() / 2, AbstractFractionsCanvas.STAGE_SIZE.height - AbstractFractionsCanvas.INSET - this.getFullHeight() );
        }};
        addChild( toolboxNode );

        emptyFractionGraphic = new FractionGraphic() {{
            setOffset( 300, 300 );
        }};
        addChild( emptyFractionGraphic );

        int numCopies = 1;
        for ( int i = 0; i < 10; i++ ) {
            for ( int k = 0; k < numCopies; k++ ) {
                PNode numberNode = new NumberNode( i, this );
                numberNode.setOffset( toolboxNode.getFullBounds().getX() + toolboxNode.getFullWidth() * ( i + 1 ) / 11.0 - numberNode.getFullBounds().getWidth() / 2, toolboxNode.getCenterY() - numberNode.getFullBounds().getHeight() / 2 );
                addChild( numberNode );
            }
        }
    }

    public void endDrag( final NumberNode numberNode, final PInputEvent event ) {
        if ( numberNode.getGlobalFullBounds().intersects( emptyFractionGraphic.topBox.getGlobalFullBounds() ) && emptyFractionGraphic.topBox.getVisible() ) {
            centerOnBox( numberNode, emptyFractionGraphic.topBox );
            emptyFractionGraphic.topBox.setVisible( false );
            numberNode.setPickable( false );
            numberNode.setChildrenPickable( false );
            emptyFractionGraphic.splitButton.setVisible( true );
        }
        else if ( numberNode.getGlobalFullBounds().intersects( emptyFractionGraphic.bottomBox.getGlobalFullBounds() ) && emptyFractionGraphic.bottomBox.getVisible() ) {
            centerOnBox( numberNode, emptyFractionGraphic.bottomBox );
            emptyFractionGraphic.bottomBox.setVisible( false );
            numberNode.setPickable( false );
            numberNode.setChildrenPickable( false );
            emptyFractionGraphic.splitButton.setVisible( true );
        }
    }

    private void centerOnBox( final NumberNode numberNode, final PhetPPath box ) {
        Rectangle2D bounds = box.getGlobalFullBounds();
        bounds = rootNode.globalToLocal( bounds );
        numberNode.centerFullBoundsOnPoint( bounds.getCenterX(), bounds.getCenterY() );
    }
}