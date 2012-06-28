package edu.colorado.phet.buildafraction.view.pictures;

import fj.Equal;
import fj.F;
import fj.data.List;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.buildafraction.model.pictures.PictureLevel;
import edu.colorado.phet.buildafraction.model.pictures.PictureTarget;
import edu.colorado.phet.buildafraction.view.BackButton;
import edu.colorado.phet.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
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

import static edu.colorado.phet.buildafraction.view.pictures.ContainerNode._isAtStartingLocation;
import static edu.colorado.phet.buildafraction.view.pictures.PieceIconNode.TINY_SCALE;
import static edu.colorado.phet.fractions.util.FJUtils.ord;
import static edu.colorado.phet.fractions.view.FNode._maxX;
import static edu.colorado.phet.fractions.view.FNode._minX;
import static edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas.INSET;
import static fj.Ord.doubleOrd;
import static fj.function.Booleans.not;

/**
 * Node for the scene when the user is constructing fractions with pictures (shapes).
 *
 * @author Sam Reid
 */
public class PictureSceneNode extends PNode implements ContainerContext, PieceContext {
    private final BuildAFractionModel model;
    private final List<Target> targetPairs;
    private final RichPNode toolboxNode;
    private final VBox faceNodeDialog;
    public final int levelIndex;

    public PictureSceneNode( final int levelIndex, final BuildAFractionModel model, final PDimension STAGE_SIZE, final SceneContext context ) {
        this.model = model;
        this.levelIndex = levelIndex;

        final BackButton backButton = new BackButton( new VoidFunction0() {
            public void apply() {
                context.goToLevelSelectionScreen();
            }
        } ) {{
            setOffset( INSET, INSET );
        }};
        addChild( backButton );

        //Create the scoring cells with target patterns
        ArrayList<Target> pairList = new ArrayList<Target>();
        for ( int i = 0; i < model.getPictureLevel( levelIndex ).targets.length(); i++ ) {
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
        } ).maximum( doubleOrd );
        double maxHeight = patterns.map( new F<PNode, Double>() {
            @Override public Double f( final PNode pNode ) {
                return pNode.getFullBounds().getHeight();
            }
        } ).maximum( doubleOrd );

        //Layout for the scoring cells and target patterns
        double separation = 5;
        double rightInset = 10;
        final PBounds targetCellBounds = pairList.get( 0 ).getCell().getFullBounds();
        double offsetX = AbstractFractionsCanvas.STAGE_SIZE.width - maxWidth - separation - targetCellBounds.getWidth() - rightInset;
        double offsetY = INSET;
        double insetY = 5;
//        addChild( title );
        for ( Target pair : pairList ) {

            pair.cell.setOffset( offsetX, offsetY );
            pair.node.setOffset( offsetX + targetCellBounds.getWidth() + separation, offsetY + targetCellBounds.getHeight() / 2 - maxHeight / 2 );
            addChild( pair.cell );
            addChild( pair.node );

            offsetY += Math.max( maxHeight, targetCellBounds.getHeight() ) + insetY;
        }

        final PictureLevel level = model.getPictureLevel( levelIndex );

        ContainerNode firstContainerNode = new ContainerNode( this, this, level.hasValuesGreaterThanOne() ) {{
            setInitialPosition( 285, 200 );
        }};
        addChild( firstContainerNode );
        addChild( new ContainerFrontNode( firstContainerNode ) );

        //Pieces in the bucket
        //Pieces always in front of the containers--could be awkward if a container is moved across a container that already has pieces in it.
        List<List<Integer>> groups = level.pieces.group( Equal.intEqual );
        int numGroups = groups.length();
        int groupIndex = 0;
        final int spacing = 140;
        final int layoutXOffset = ( 6 - numGroups ) * spacing / 4;
        for ( List<Integer> group : groups ) {
            int numInGroup = group.length();
            int pieceIndex = 0;

            Rectangle2D bounds = null;
            for ( final Integer pieceDenominator : group ) {
                double dx = 4;
                double totalHorizontalSpacing = dx * ( numInGroup - 1 );
                LinearFunction offset = new LinearFunction( 0, numInGroup - 1, -totalHorizontalSpacing / 2, +totalHorizontalSpacing / 2 );
                final RectangularPiece piece = new RectangularPiece( pieceDenominator, PictureSceneNode.this );
                final double delta = numInGroup == 1 ? 0 : offset.evaluate( pieceIndex );
                piece.setInitialState( layoutXOffset + INSET + 20 + delta + groupIndex * spacing,
                                       STAGE_SIZE.height - INSET - 127 + 20 + delta, TINY_SCALE );
                PictureSceneNode.this.addChild( piece );
                pieceIndex++;
                if ( bounds == null ) { bounds = piece.getFullBounds(); }
                else {
                    bounds = bounds.createUnion( piece.getFullBounds() );
                }
            }

            final PieceIconNode child = new PieceIconNode( group.head() );
            child.centerFullBoundsOnPoint( bounds.getCenterX(), bounds.getMaxY() + 34 );
            addChild( child );
            groupIndex++;
        }

        final int finalGroupIndex = groupIndex;
        final int numInGroup = level.targets.length() - 1;
        for ( int i = 0; i < numInGroup; i++ ) {
            double dx = 4;
            double totalHorizontalSpacing = dx * ( numInGroup - 1 );
            LinearFunction offset = new LinearFunction( 0, numInGroup - 1, -totalHorizontalSpacing / 2, +totalHorizontalSpacing / 2 );
            final double delta = numInGroup == 1 ? 0 : offset.evaluate( i );
            final ContainerNode containerNode = new ContainerNode( this, this, level.hasValuesGreaterThanOne() ) {{
                this.setInitialState( layoutXOffset + INSET + 20 + delta + finalGroupIndex * spacing,
                                      STAGE_SIZE.height - INSET - 127 + 20 + delta, TINY_SCALE );
            }};
            addChild( containerNode );
            addChild( new ContainerFrontNode( containerNode ) );
        }

        //Add a piece container toolbox the user can use to get containers
        toolboxNode = new RichPNode() {{

            //Width is just before the first group to the last container node in the toolbox
            double min = getChildrenOfType( PieceIconNode.class ).map( _minX ).minimum( doubleOrd );
            double max = getChildrenOfType( ContainerNode.class ).map( _maxX ).maximum( doubleOrd );
            double width = max - min;
            final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( 0, 0, width + INSET * 6, 127, 30, 30 ), BuildAFractionCanvas.CONTROL_PANEL_BACKGROUND, BuildAFractionCanvas.controlPanelStroke, Color.darkGray );
            addChild( border );
            setOffset( min - INSET * 3, AbstractFractionsCanvas.STAGE_SIZE.height - INSET - this.getFullHeight() );
        }};
        addChild( toolboxNode );
        toolboxNode.moveToBack();

        faceNodeDialog = new VBox( new FaceNode( 300 ), new HTMLImageButtonNode( "Next", new PhetFont( 20, true ), Color.orange ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    context.goToNextPictureLevel( levelIndex + 1 );
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

        double minScoreCellX = List.iterableList( pairList ).map( new F<Target, Double>() {
            @Override public Double f( final Target target ) {
                return target.cell.getFullBounds().getMinX();
            }
        } ).minimum( doubleOrd );
        final PhetPText title = new PhetPText( "Level " + ( levelIndex + 1 ), new PhetFont( 32, true ) );
        title.setOffset( ( minScoreCellX - INSET ) / 2 - title.getFullWidth() / 2, backButton.getFullBounds().getCenterY() - title.getFullHeight() / 2 );
        addChild( title );

        addChild( new TextButtonNode( "Reset", AbstractFractionsCanvas.CONTROL_FONT, Color.orange ) {{
            setOffset( title.getMaxX() + 130, title.getCenterY() - getFullBounds().getHeight() / 2 );
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    reset();
                }
            } );
        }} );
    }

    private List<PNode> getChildrenOfType( final Class<? extends PNode> type ) {
        List<PNode> list = List.nil();
        for ( Object childO : getChildrenReference() ) {
            if ( type.isInstance( childO ) ) {
                list = list.snoc( (PNode) childO );
            }
        }
        return list;
    }

    private void reset() {
        //Eject everything from target containers
        //Split everything
        //Return everything home
        for ( Target targetPair : targetPairs ) {
            targetPair.cell.splitIt();
        }
        for ( ContainerNode containerNode : getContainerNodes() ) {
            containerNode.splitAll();
            containerNode.animateHome();
        }
        for ( RectangularPiece rectangularPiece : getPieceNodes() ) {
            rectangularPiece.animateHome();
        }
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

    public void endDrag( final ContainerNode containerNode, final PInputEvent event ) {
        //See if it hits any matches
        List<Target> pairs = targetPairs.sort( ord( new F<Target, Double>() {
            @Override public Double f( final Target t ) {
                return t.cell.getGlobalFullBounds().getCenter2D().distance( containerNode.getGlobalFullBounds().getCenter2D() );
            }
        } ) );
        boolean hit = false;
        for ( Target pair : pairs ) {
            final boolean intersects = pair.cell.getGlobalFullBounds().intersects( containerNode.getGlobalFullBounds() );
            final boolean matchesValue = containerNode.getFractionValue().approxEquals( pair.value );
            final boolean occupied = pair.getCell().isCompleted();
            if ( intersects && matchesValue && !occupied ) {
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

        //Add a new container when the previous one is completed
        if ( !allTargetsComplete() ) {

            //If no fraction skeleton in play area, move one there
            final List<ContainerNode> inPlayArea = getContainerNodes().filter( new F<ContainerNode, Boolean>() {
                @Override public Boolean f( final ContainerNode containerNode ) {
                    return !containerNode.isInTargetCell() && !containerNode.isAtStartingLocation();
                }
            } );
            final List<ContainerNode> inToolbox = getContainerNodes().filter( _isAtStartingLocation );
            if ( inPlayArea.length() == 0 && inToolbox.length() > 0 ) {
                ContainerNode g = inToolbox.head();
                g.animateToPositionScaleRotation( toolboxNode.getCenterX() - g.getFullBounds().getWidth() / 2, 300, 1, 0, 1000 );
            }
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
        moveContainerFrontsToFront();
    }

    private void moveContainerFrontsToFront() {
        for ( Object child : List.iterableList( getChildrenReference() ) ) {
            if ( child instanceof ContainerFrontNode ) {
                ContainerFrontNode c = (ContainerFrontNode) child;
                c.moveToFront();
            }
        }
    }

    private boolean allIncompleteContainersInToolbox() {
        return getContainerNodes().filter( not( ContainerNode._isInTargetCell ) ).forall( _isAtStartingLocation );
    }

    public void endDrag( final RectangularPiece piece, final PInputEvent event ) {
        boolean droppedInto = false;
        List<ContainerNode> containerNodes = getContainerNodes().sort( ord( new F<ContainerNode, Double>() {
            @Override public Double f( final ContainerNode c ) {
                return c.getGlobalFullBounds().getCenter2D().distance( piece.getGlobalFullBounds().getCenter2D() );
            }
        } ) );
        for ( ContainerNode containerNode : containerNodes ) {
            if ( containerNode.getGlobalFullBounds().intersects( piece.getGlobalFullBounds() ) && !containerNode.isInToolbox() ) {
                dropInto( piece, containerNode );
                droppedInto = true;
                break;
            }
        }

        //If didn't intersect a container, see if it should go back to the bucket
        if ( !droppedInto ) {
            piece.animateHome();
        }
        else if ( !droppedInto && piece.getGlobalFullBounds().intersects( toolboxNode.getGlobalFullBounds() ) ) {
            piece.animateToPositionScaleRotation( piece.getXOffset(), piece.getYOffset() - 100, 1, 0, 200 );
        }
        moveContainerFrontsToFront();
    }

    //Rectangular piece dropped into container
    private void dropInto( final RectangularPiece piece, final ContainerNode containerNode ) {
        PTransformActivity activity = piece.animateToPositionScaleRotation( containerNode.getOffset().getX() + containerNode.getPiecesWidth(), containerNode.getOffset().getY() + containerNode.getYOffsetForContainer(), 1, 0, 200 );
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
    private List<Fraction> getUserCreatedFractions() { return getContainerNodes().filter( not( ContainerNode._isInTargetCell ) ).map( ContainerNode._getFractionValue ); }

    public void splitPieceFromContainer( final RectangularPiece piece, final ContainerNode containerNode, final double relativeXOffset ) {
        Point2D offset = piece.getGlobalTranslation();
        addChild( piece );
        piece.setGlobalTranslation( offset );
        piece.setPickable( true );
        piece.setChildrenPickable( true );
        piece.animateHome();
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