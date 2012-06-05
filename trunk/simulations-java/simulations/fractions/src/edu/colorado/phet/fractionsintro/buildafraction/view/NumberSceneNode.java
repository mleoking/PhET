package edu.colorado.phet.fractionsintro.buildafraction.view;

import fj.F;
import fj.Ord;
import fj.data.List;
import lombok.Data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.FilledPattern;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.PatternNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.fractions.FractionsResources.Strings.MY_FRACTIONS;

/**
 * @author Sam Reid
 */
public class NumberSceneNode extends PNode implements DragContext {
    private final FractionGraphic emptyFractionGraphic;
    private final PNode rootNode;
    private final BuildAFractionModel model;

    @Data class Pair {
        public final PNode targetCell;
        public final PNode patternNode;
    }

    public NumberSceneNode( final PNode rootNode, final BuildAFractionModel model ) {
        this.rootNode = rootNode;
        this.model = model;
        final PhetPText title = new PhetPText( MY_FRACTIONS, AbstractFractionsCanvas.CONTROL_FONT );

        //Create the scoring cells with target patterns
        ArrayList<Pair> pairs = new ArrayList<Pair>();
        for ( int i = 0; i < 3; i++ ) {
            final int numerator = i + 1;
            final PatternNode patternNode = new PatternNode( FilledPattern.sequentialFill( Pattern.sixFlower( 18 ), numerator ), Color.red );
            PNode boxNode = new ScoreBoxNode( numerator, 6, model.createdFractions );
            pairs.add( new Pair( new ZeroOffsetNode( boxNode ), new ZeroOffsetNode( patternNode ) ) );
        }
        List<Pair> p = List.iterableList( pairs );
        List<PNode> patterns = p.map( new F<Pair, PNode>() {
            @Override public PNode f( final Pair pair ) {
                return pair.patternNode;
            }
        } );
        double maxWidth = patterns.map( new F<PNode, Double>() {
            @Override public Double f( final PNode pNode ) {
                return pNode.getFullBounds().getWidth();
            }
        } ).maximum( Ord.doubleOrd );
        double maxHeight = patterns.map( new F<PNode, Double>() {
            @Override public Double f( final PNode pNode ) {
                return pNode.getFullBounds().getHeight();
            }
        } ).maximum( Ord.doubleOrd );

        //Layout for the scoring cells and target patterns
        double separation = 5;
        double rightInset = 10;
        final PBounds targetCellBounds = pairs.get( 0 ).getTargetCell().getFullBounds();
        double offsetX = AbstractFractionsCanvas.STAGE_SIZE.width - maxWidth - separation - targetCellBounds.getWidth() - rightInset;
        double offsetY = title.getFullHeight() + 5;
        double insetY = 5;
        addChild( title );
        for ( Pair pair : pairs ) {

            pair.targetCell.setOffset( offsetX, offsetY );
            pair.patternNode.setOffset( offsetX + targetCellBounds.getWidth() + separation, offsetY + targetCellBounds.getHeight() / 2 - maxHeight / 2 );
            addChild( pair.targetCell );
            addChild( pair.patternNode );

            offsetY += Math.max( maxHeight, targetCellBounds.getHeight() ) + insetY;
        }

        //Center title above the "my fractions" scoring cell boxes
        title.setOffset( pairs.get( 0 ).getTargetCell().getFullBounds().getCenterX() - title.getFullWidth() / 2, pairs.get( 0 ).getTargetCell().getFullBounds().getY() - title.getFullHeight() );

        //Add a piece container toolbox the user can use to get containers
        final RichPNode toolboxNode = new RichPNode() {{
            final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( 0, 0, 700, 160, 30, 30 ), edu.colorado.phet.fractionsintro.buildafraction_functional.view.BuildAFractionCanvas.CONTROL_PANEL_BACKGROUND, BuildAFractionCanvas.controlPanelStroke, Color.darkGray );
            addChild( border );
            setOffset( ( AbstractFractionsCanvas.STAGE_SIZE.width - 150 ) / 2 - this.getFullWidth() / 2, AbstractFractionsCanvas.STAGE_SIZE.height - AbstractFractionsCanvas.INSET - this.getFullHeight() );
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
        final PhetPPath topBox = emptyFractionGraphic.topBox;
        final PhetPPath bottomBox = emptyFractionGraphic.bottomBox;
        if ( numberNode.getGlobalFullBounds().intersects( topBox.getGlobalFullBounds() ) && topBox.getVisible() ) {
            droppedInto( numberNode, topBox );
        }
        else if ( numberNode.getGlobalFullBounds().intersects( bottomBox.getGlobalFullBounds() ) && bottomBox.getVisible() ) {
            droppedInto( numberNode, bottomBox );
        }
    }

    private void droppedInto( final NumberNode numberNode, final PhetPPath box ) {
        centerOnBox( numberNode, box );
        box.setVisible( false );
        numberNode.setPickable( false );
        numberNode.setChildrenPickable( false );
        emptyFractionGraphic.splitButton.setVisible( true );
        emptyFractionGraphic.setTarget( box, numberNode );
        if ( emptyFractionGraphic.isComplete() ) {
            model.addCreatedValue( emptyFractionGraphic.getValue() );
            //create an invisible overlay that allows dragging all parts together
            PBounds topBounds = emptyFractionGraphic.getTopNumber().getFullBounds();
            PBounds bottomBounds = emptyFractionGraphic.getBottomNumber().getFullBounds();
            Rectangle2D divisorBounds = emptyFractionGraphic.localToParent( emptyFractionGraphic.divisorLine.getFullBounds() );
            Rectangle2D union = topBounds.createUnion( bottomBounds ).createUnion( divisorBounds );
            final PhetPPath path = new PhetPPath( RectangleUtils.expand( union, 2, 2 ), BuildAFractionCanvas.TRANSPARENT, new BasicStroke( 1 ), Color.yellow );
            path.addInputEventListener( new CursorHandler() );
            path.addInputEventListener( new SimSharingDragHandler( null, true ) {
                @Override protected void drag( final PInputEvent event ) {
                    final PDimension delta = event.getDeltaRelativeTo( rootNode );
                    emptyFractionGraphic.translateAll( delta );
                    path.translate( delta.getWidth(), delta.getHeight() );
                }
            } );
            addChild( path );
        }
    }

    private void centerOnBox( final NumberNode numberNode, final PhetPPath box ) {
        Rectangle2D bounds = box.getGlobalFullBounds();
        bounds = rootNode.globalToLocal( bounds );
        numberNode.centerFullBoundsOnPoint( bounds.getCenterX(), bounds.getCenterY() );
    }
}