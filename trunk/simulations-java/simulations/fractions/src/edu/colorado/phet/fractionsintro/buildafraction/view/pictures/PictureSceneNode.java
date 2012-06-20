package edu.colorado.phet.fractionsintro.buildafraction.view.pictures;

import fj.Equal;
import fj.F;
import fj.Ord;
import fj.data.List;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.Bucket;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.BucketView;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.util.FJUtils;
import edu.colorado.phet.fractions.view.SpinnerButtonNode;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.buildafraction.model.pictures.PictureLevel;
import edu.colorado.phet.fractionsintro.buildafraction.model.pictures.PictureTarget;
import edu.colorado.phet.fractionsintro.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.intro.view.FractionNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.fractions.FractionsResources.Images.*;
import static edu.colorado.phet.fractionsintro.buildafraction.view.pictures.ContainerNode._getFractionValue;
import static edu.colorado.phet.fractionsintro.buildafraction.view.pictures.ContainerNode._isInTargetCell;
import static fj.function.Booleans.not;

/**
 * Node for the scene when the user is constructing fractions with pictures (shapes).
 *
 * @author Sam Reid
 */
public class PictureSceneNode extends PNode implements ContainerContext, PieceContext {
    private final PNode rootNode;
    private final BuildAFractionModel model;
    private final PDimension STAGE_SIZE;
    private final List<Target> targetPairs;
    private final RichPNode toolboxNode;
    private final PNode frontLayer;
    private final VBox faceNodeDialog;
    public final int levelIndex;
    private final BucketView bucketView;

    public PictureSceneNode( int levelIndex, final PNode rootNode, final BuildAFractionModel model, final PDimension STAGE_SIZE, final PictureSceneContext context ) {
        this.rootNode = rootNode;
        this.model = model;
        this.levelIndex = levelIndex;
        this.STAGE_SIZE = STAGE_SIZE;
//        final PhetPText title = new PhetPText( MY_FRACTIONS, AbstractFractionsCanvas.CONTROL_FONT );

        //Create the scoring cells with target patterns
        ArrayList<Target> pairList = new ArrayList<Target>();
        for ( int i = 0; i < 3; i++ ) {
            PictureTarget target = model.getPictureLevel( levelIndex ).getTarget( i );

            FractionNode f = new FractionNode( target.fraction, 0.33 );
            final PictureScoreBoxNode cell = new PictureScoreBoxNode( target.fraction.numerator, target.fraction.denominator,
                                                                      model.getPictureCreatedFractions( levelIndex ), this,
                                                                      model.getNumberLevel( levelIndex ).flashTargetCellOnMatch );
            pairList.add( new Target( cell, new ZeroOffsetNode( f ), target.fraction ) );
        }
        this.targetPairs = List.iterableList( pairList );

        List<PNode> patterns = this.targetPairs.map( new F<Target, PNode>() {
            @Override public PNode f( final Target pair ) {
                return pair.node;
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
        final PBounds targetCellBounds = pairList.get( 0 ).getCell().getFullBounds();
        double offsetX = AbstractFractionsCanvas.STAGE_SIZE.width - maxWidth - separation - targetCellBounds.getWidth() - rightInset;
        double offsetY = AbstractFractionsCanvas.INSET;
        double insetY = 5;
//        addChild( title );
        for ( Target pair : pairList ) {

            pair.cell.setOffset( offsetX, offsetY );
            pair.node.setOffset( offsetX + targetCellBounds.getWidth() + separation, offsetY + targetCellBounds.getHeight() / 2 - maxHeight / 2 );
            addChild( pair.cell );
            addChild( pair.node );

            offsetY += Math.max( maxHeight, targetCellBounds.getHeight() ) + insetY;
        }

        //Center title above the "my fractions" scoring cell boxes
//        title.setOffset( pairs.get( 0 ).getTargetCell().getFullBounds().getCenterX() - title.getFullWidth() / 2, pairs.get( 0 ).getTargetCell().getFullBounds().getY() - title.getFullHeight() );

        //Add a piece container toolbox the user can use to get containers
        toolboxNode = new RichPNode() {{
            final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( 0, 0, 450, 160, 30, 30 ), BuildAFractionCanvas.CONTROL_PANEL_BACKGROUND, BuildAFractionCanvas.controlPanelStroke, Color.darkGray );
            addChild( border );
            setOffset( AbstractFractionsCanvas.INSET, AbstractFractionsCanvas.STAGE_SIZE.height - AbstractFractionsCanvas.INSET - this.getFullHeight() );
        }};
        addChild( toolboxNode );

        PictureLevel level = model.getPictureLevel( levelIndex );
        for ( Integer denominator : level.containers ) {
            ContainerNode containerNode = new ContainerNode( this, denominator, this );
            int row = ( denominator - 1 ) / 3;
            int column = ( denominator - 1 ) % 3;
            containerNode.setInitialPosition( toolboxNode.getFullBounds().getX() + AbstractFractionsCanvas.INSET + column * 150,
                                              toolboxNode.getFullBounds().getY() + row * 80 + AbstractFractionsCanvas.INSET );
            PictureSceneNode.this.addChild( containerNode );
        }

        //Create the bucket
        Dimension2DDouble littleBucket = new Dimension2DDouble( 250, 100 );
        Bucket bucket = new Bucket( ( AbstractFractionsCanvas.STAGE_SIZE.width ) / 2 + 100, -STAGE_SIZE.getHeight() + littleBucket.getHeight(), littleBucket, Color.green, "" );
        bucketView = new BucketView( bucket, ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( 0, 0 ), 1 ) );
        addChild( bucketView.getHoleNode() );

        final IntegerProperty selectedPieceSize = new IntegerProperty( 1 );

        //Pieces in the bucket
        //Pieces always in front of the containers--could be awkward if a container is moved across a container that already has pieces in it.
        List<List<Integer>> groups = level.pieces.group( Equal.intEqual );
        for ( List<Integer> group : groups ) {
            int numInGroup = group.length();
            int index = 0;
            for ( final Integer pieceDenominator : group ) {
                double dx = 4;
                double totalHorizontalSpacing = dx * ( numInGroup - 1 );
                LinearFunction offset = new LinearFunction( 0, numInGroup - 1, -totalHorizontalSpacing / 2, +totalHorizontalSpacing / 2 );
                final RectangularPiece piece = new RectangularPiece( pieceDenominator, PictureSceneNode.this );
                final double delta = numInGroup == 1 ? 0 : offset.evaluate( index );
                piece.setInitialPosition( bucketView.getHoleNode().getFullBounds().getCenterX() - piece.getFullBounds().getWidth() / 2 + delta,
                                          bucketView.getHoleNode().getFullBounds().getCenterY() - piece.getFullBounds().getHeight() / 2 + delta );
                PictureSceneNode.this.addChild( piece );
                selectedPieceSize.addObserver( new VoidFunction1<Integer>() {
                    public void apply( final Integer selectedPieceSize ) {
                        boolean inInitialPosition = piece.isAtInitialPosition();
                        boolean matchesSelection = selectedPieceSize.equals( pieceDenominator );
                        final boolean visible = !inInitialPosition || matchesSelection;
                        piece.setVisible( visible );
                        piece.setPickable( visible );
                        piece.setChildrenPickable( visible );
                    }
                } );
                index++;
            }
        }

        frontLayer = new PNode() {{
            addChild( bucketView.getFrontNode() );

            final double buttonInset = 20;
            final VoidFunction1<Boolean> decrement = new VoidFunction1<Boolean>() {
                public void apply( final Boolean autoSpinning ) {
                    selectedPieceSize.decrement();
                }
            };
            final PBounds bucketBounds = bucketView.getFrontNode().getFullBounds();
            addChild( new SpinnerButtonNode( spinnerImage( LEFT_BUTTON_UP ), spinnerImage( LEFT_BUTTON_PRESSED ), spinnerImage( LEFT_BUTTON_GRAY ), decrement, selectedPieceSize.greaterThan( 1 ) ) {{
                setOffset( bucketBounds.getMinX() + buttonInset, bucketBounds.getCenterY() - getFullBounds().getHeight() / 2 );
            }} );
            final VoidFunction1<Boolean> increment = new VoidFunction1<Boolean>() {
                public void apply( final Boolean autoSpinning ) {
                    selectedPieceSize.increment();
                }
            };
            addChild( new SpinnerButtonNode( spinnerImage( RIGHT_BUTTON_UP ), spinnerImage( RIGHT_BUTTON_PRESSED ), spinnerImage( RIGHT_BUTTON_GRAY ), increment, selectedPieceSize.lessThan( 6 ) ) {{
                setOffset( bucketBounds.getMaxX() - getFullBounds().getWidth() - buttonInset, bucketBounds.getCenterY() - getFullBounds().getHeight() / 2 );
            }} );

            addChild( new PNode() {{
                selectedPieceSize.addObserver( new VoidFunction1<Integer>() {
                    public void apply( final Integer selectedSize ) {
                        removeAllChildren();
                        addChild( new PieceIconNode( selectedSize ) );
                        centerFullBoundsOnPoint( bucketBounds.getCenterX(), bucketBounds.getCenterY() );
                    }
                } );
            }} );
        }};
        addChild( frontLayer );

        faceNodeDialog = new VBox( new FaceNode( 300 ), new HTMLImageButtonNode( "Next", new PhetFont( 20, true ), Color.orange ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    context.nextPictureLevel();
                }
            } );
        }} ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2 - 100, STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 - 100 );
        }};

        faceNodeDialog.setTransparency( 0 );
        faceNodeDialog.setVisible( false );
        faceNodeDialog.setPickable( false );
        faceNodeDialog.setChildrenPickable( false );

        addChild( faceNodeDialog );
    }

    private List<RectangularPiece> getPieceNodes() {
        ArrayList<RectangularPiece> children = new ArrayList<RectangularPiece>();
        for ( Object child : this.getChildrenReference() ) {
            if ( child instanceof RectangularPiece ) {
                children.add( (RectangularPiece) child );
            }
        }
        return List.iterableList( children );
    }

    public static BufferedImage spinnerImage( final BufferedImage image ) {
        return BufferedImageUtils.multiScaleToWidth( image, 50 );
    }

    public void endDrag( final ContainerNode containerNode, final PInputEvent event ) {
        //See if it hits any matches
        List<Target> pairs = targetPairs.sort( FJUtils.ord( new F<Target, Double>() {
            @Override public Double f( final Target targetPair ) {
                return targetPair.cell.getGlobalFullBounds().getCenter2D().distance( containerNode.getGlobalFullBounds().getCenter2D() );
            }
        } ) );
        boolean hit = false;
        for ( Target pair : pairs ) {
            final boolean intersects = pair.cell.getGlobalFullBounds().intersects( containerNode.getGlobalFullBounds() );
            final boolean matchesValue = containerNode.getFractionValue().approxEquals( pair.value );
            if ( intersects && matchesValue ) {
                final double scale = 0.5;
                containerNode.removeSplitButton();
                containerNode.animateToPositionScaleRotation( pair.cell.getFullBounds().getCenterX() - containerNode.getFullBounds().getWidth() / 2 * scale,
                                                              pair.cell.getFullBounds().getCenterY() - containerNode.getFullBounds().getHeight() / 2 * scale, scale, 0, 200 );
                pair.cell.setCompletedFraction( containerNode );
                containerNode.setAllPickable( false );
                containerNode.setInTargetCell( true );
                hit = true;
                break;
            }
        }
        if ( !hit && containerNode.getGlobalFullBounds().intersects( toolboxNode.getGlobalFullBounds() ) ) {
            containerNode.animateHome();
        }

        if ( allTargetsComplete() ) {
            faceNodeDialog.setVisible( true );
            faceNodeDialog.animateToTransparency( 1f, 200 );
            faceNodeDialog.setPickable( true );
            faceNodeDialog.setChildrenPickable( true );
            faceNodeDialog.moveToFront();
            model.numberScore.add( 1 );
        }

        syncModelFractions();
    }

    public void endDrag( final RectangularPiece piece, final PInputEvent event ) {
        boolean droppedInto = false;
        List<ContainerNode> containerNodes = getContainerNodes().sort( FJUtils.ord( new F<ContainerNode, Double>() {
            @Override public Double f( final ContainerNode c ) {
                return c.getGlobalFullBounds().getCenter2D().distance( piece.getGlobalFullBounds().getCenter2D() );
            }
        } ) );
        for ( ContainerNode containerNode : containerNodes ) {
            if ( containerNode.getGlobalFullBounds().intersects( piece.getGlobalFullBounds() ) && !containerNode.isAtStartingLocation() ) {
                dropInto( piece, containerNode );
                droppedInto = true;
                break;
            }
        }

        //If didn't intersect a container, see if it should go back to the bucket
        if ( !droppedInto &&
             ( piece.getGlobalFullBounds().intersects( bucketView.getHoleNode().getGlobalFullBounds() ) ||
               piece.getGlobalFullBounds().intersects( bucketView.getFrontNode().getGlobalFullBounds() ) ) ) {
            piece.animateHome();
            frontLayer.moveToFront();
        }
        else if ( !droppedInto && piece.getGlobalFullBounds().intersects( toolboxNode.getGlobalFullBounds() ) ) {
            piece.animateToPositionScaleRotation( piece.getXOffset(), piece.getYOffset() - 100, 1, 0, 200 );
        }
    }

    private void dropInto( final RectangularPiece piece, final ContainerNode containerNode ) {
        PTransformActivity activity = piece.animateToPositionScaleRotation( containerNode.getOffset().getX() + containerNode.getPiecesWidth(), containerNode.getOffset().getY(), 1, 0, 200 );
        piece.setPickable( false );
        piece.setChildrenPickable( false );
        activity.setDelegate( new PActivityDelegate() {
            public void activityStarted( final PActivity activity ) {
            }

            public void activityStepped( final PActivity activity ) {
            }

            public void activityFinished( final PActivity activity ) {
                containerNode.addPiece( piece );
                syncModelFractions();
            }
        } );
    }

    public void syncModelFractions() { model.getPictureCreatedFractions( model.pictureLevel.get() ).set( getUserCreatedFractions() ); }

    //TODO: when we have multiple containers, this will have to be modified
    private List<Fraction> getUserCreatedFractions() { return getContainerNodes().filter( not( _isInTargetCell ) ).map( _getFractionValue ); }

    public void splitPieceFromContainer( final RectangularPiece piece, final ContainerNode containerNode, final double relativeXOffset ) {
        Point2D offset = piece.getGlobalTranslation();
        addChild( piece );
        piece.setGlobalTranslation( offset );
        piece.setPickable( true );
        piece.setChildrenPickable( true );
        piece.animateToPositionScaleRotation( piece.getXOffset() + relativeXOffset, piece.getYOffset() + containerNode.getFullBounds().getHeight() + 5, 1, 0, 200 );
    }

    private List<ContainerNode> getContainerNodes() {
        ArrayList<ContainerNode> children = new ArrayList<ContainerNode>();
        for ( Object child : this.getChildrenReference() ) {
            if ( child instanceof ContainerNode ) {
                children.add( (ContainerNode) child );
            }
        }
        return List.iterableList( children );
    }

    private boolean allTargetsComplete() {
        return targetPairs.map( new F<Target, Boolean>() {
            @Override public Boolean f( final Target pair ) {
                return pair.cell.isCompleted();
            }
        } ).filter( new F<Boolean, Boolean>() {
            @Override public Boolean f( final Boolean b ) {
                return b;
            }
        } ).length() == targetPairs.length();
    }

    public void hideFace() {
        //Only subtract from the score if the face dialog was showing.  Otherwise you can get a negative score by removing an item from the target container since this method is called
        //each time.
        if ( faceNodeDialog.getPickable() ) {
            model.numberScore.subtract( 1 );
        }
        faceNodeDialog.animateToTransparency( 0.0f, 200 );
        faceNodeDialog.setPickable( false );
        faceNodeDialog.setChildrenPickable( false );
    }

}