package edu.colorado.phet.fractionsintro.buildafraction.view;

import fj.F;
import fj.Ord;
import fj.data.List;
import lombok.Data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
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
 * Node for the scene when the user is contructing fractions with numbers.
 *
 * @author Sam Reid
 */
public class NumberSceneNode extends PNode implements DragContext {
    private final ArrayList<FractionGraphic> fractionGraphics = new ArrayList<FractionGraphic>();
    private final PNode rootNode;
    private final BuildAFractionModel model;
    private final PDimension STAGE_SIZE;
    private final List<Pair> pairList;

    @Data class Pair {
        public final ScoreBoxNode targetCell;
        public final PNode patternNode;
    }

    public NumberSceneNode( final PNode rootNode, final BuildAFractionModel model, PDimension STAGE_SIZE ) {
        this.rootNode = rootNode;
        this.model = model;
        this.STAGE_SIZE = STAGE_SIZE;
        final PhetPText title = new PhetPText( MY_FRACTIONS, AbstractFractionsCanvas.CONTROL_FONT );

        //Create the scoring cells with target patterns
        ArrayList<Pair> pairs = new ArrayList<Pair>();
        for ( int i = 0; i < 3; i++ ) {
            final int numerator = i + 1;
            final PatternNode patternNode = new PatternNode( FilledPattern.sequentialFill( Pattern.sixFlower( 18 ), numerator ), Color.red );
            pairs.add( new Pair( new ScoreBoxNode( numerator, 6, model.createdFractions ), new ZeroOffsetNode( patternNode ) ) );
        }
        pairList = List.iterableList( pairs );
        List<PNode> patterns = pairList.map( new F<Pair, PNode>() {
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

        final FractionGraphic fractionGraphic = createDefaultFractionGraphic();
        addChild( fractionGraphic );
        fractionGraphics.add( fractionGraphic );

        int numCopies = 2;
        for ( int i = 0; i < 10; i++ ) {
            for ( int k = 0; k < numCopies; k++ ) {
                PNode numberNode = new NumberNode( i, this );
                numberNode.setOffset( toolboxNode.getFullBounds().getX() + toolboxNode.getFullWidth() * ( i + 1 ) / 11.0 - numberNode.getFullBounds().getWidth() / 2, toolboxNode.getCenterY() - numberNode.getFullBounds().getHeight() / 2 );
                addChild( numberNode );
            }
        }
    }

    private FractionGraphic createDefaultFractionGraphic() {
        return new FractionGraphic() {{
            setOffset( 300, 300 );
        }};
    }

    public void endDrag( final NumberNode numberNode, final PInputEvent event ) {
        for ( FractionGraphic fractionGraphic : fractionGraphics ) {
            final PhetPPath topBox = fractionGraphic.topBox;
            final PhetPPath bottomBox = fractionGraphic.bottomBox;
            if ( numberNode.getGlobalFullBounds().intersects( topBox.getGlobalFullBounds() ) && topBox.getVisible() ) {
                numberDroppedOnFraction( fractionGraphic, numberNode, topBox );
                break;
            }
            if ( numberNode.getGlobalFullBounds().intersects( bottomBox.getGlobalFullBounds() ) && bottomBox.getVisible() ) {
                numberDroppedOnFraction( fractionGraphic, numberNode, bottomBox );
                break;
            }
        }
    }

    private void numberDroppedOnFraction( final FractionGraphic fractionGraphic, final NumberNode numberNode, final PhetPPath box ) {
        centerOnBox( numberNode, box );
        box.setVisible( false );
        numberNode.setPickable( false );
        numberNode.setChildrenPickable( false );
        fractionGraphic.splitButton.setVisible( true );
        fractionGraphic.setTarget( box, numberNode );
        if ( fractionGraphic.isComplete() ) {
            model.addCreatedValue( fractionGraphic.getValue() );
            //create an invisible overlay that allows dragging all parts together
            PBounds topBounds = fractionGraphic.getTopNumber().getFullBounds();
            PBounds bottomBounds = fractionGraphic.getBottomNumber().getFullBounds();
            Rectangle2D divisorBounds = fractionGraphic.localToParent( fractionGraphic.divisorLine.getFullBounds() );
            Rectangle2D union = topBounds.createUnion( bottomBounds ).createUnion( divisorBounds );
            final PhetPPath path = new PhetPPath( RectangleUtils.expand( union, 2, 2 ), BuildAFractionCanvas.TRANSPARENT, new BasicStroke( 1 ), Color.yellow );
            path.addInputEventListener( new CursorHandler() );
            path.addInputEventListener( new SimSharingDragHandler( null, true ) {
                @Override protected void drag( final PInputEvent event ) {
                    super.drag( event );
                    final PDimension delta = event.getDeltaRelativeTo( rootNode );
                    fractionGraphic.translateAll( delta );
                    path.translate( delta.getWidth(), delta.getHeight() );
                }

                @Override protected void endDrag( final PInputEvent event ) {
                    super.endDrag( event );

                    //Snap to a scoring cell or go back to the play area.
                    List<ScoreBoxNode> scoreCells = pairList.map( new F<Pair, ScoreBoxNode>() {
                        @Override public ScoreBoxNode f( final Pair pair ) {
                            return pair.targetCell;
                        }
                    } );
                    for ( ScoreBoxNode scoreCell : scoreCells ) {
                        if ( path.getFullBounds().intersects( scoreCell.getFullBounds() ) && scoreCell.fraction.approxEquals( fractionGraphic.getValue() ) ) {
                            //Lock in target cell
                            Point2D center = path.getFullBounds().getCenter2D();
                            Point2D targetCenter = scoreCell.getFullBounds().getCenter2D();
                            Vector2D delta = new Vector2D( targetCenter, center );
                            fractionGraphic.translateAll( delta.toDimension() );
                            path.translate( delta.x, delta.y );

                            fractionGraphic.splitButton.setVisible( false );
                            removeChild( path );
                            fractionGraphic.setAllPickable( false );

                            scoreCell.completed();

                            //Add a new fraction skeleton when the previous one is completed
                            if ( !allTargetsComplete() ) {
                                final FractionGraphic fractionGraphic = createDefaultFractionGraphic();
                                addChild( fractionGraphic );
                                fractionGraphics.add( fractionGraphic );
                            }

                            //but if all filled up, then add a "next" button
                            else {
                                addChild( new VBox( new FaceNode( 300 ), new HTMLImageButtonNode( "Next", Color.orange ) {{
                                    addActionListener( new ActionListener() {
                                        public void actionPerformed( final ActionEvent e ) {
                                            goToNext();
                                        }
                                    } );
                                }}
                                ) {{setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2 - 100, STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 - 100 );}} );
                            }
                        }
                    }
                }
            } );
            addChild( path );
        }
    }

    private void goToNext() {

    }

    private boolean allTargetsComplete() {
        return pairList.map( new F<Pair, Boolean>() {
            @Override public Boolean f( final Pair pair ) {
                return pair.targetCell.isCompleted();
            }
        } ).filter( new F<Boolean, Boolean>() {
            @Override public Boolean f( final Boolean b ) {
                return b;
            }
        } ).length() == pairList.length();
    }

    private void centerOnBox( final NumberNode numberNode, final PhetPPath box ) {
        Rectangle2D bounds = box.getGlobalFullBounds();
        bounds = rootNode.globalToLocal( bounds );
        numberNode.centerFullBoundsOnPoint( bounds.getCenterX(), bounds.getCenterY() );
    }
}