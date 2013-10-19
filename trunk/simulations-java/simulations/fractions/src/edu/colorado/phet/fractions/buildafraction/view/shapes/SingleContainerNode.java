// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import fj.Effect;
import fj.F;
import fj.data.List;
import fj.function.Doubles;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.util.functionaljava.FJUtils;
import edu.colorado.phet.common.piccolophet.activities.AnimateToScale;
import edu.colorado.phet.common.piccolophet.activities.PActivityDelegateAdapter;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.DragEvent;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.SimSharingCanvasBoundedDragHandler;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeType;
import edu.colorado.phet.fractions.buildafraction.view.shapes.ShapeSceneNode.DropLocation;
import edu.colorado.phet.fractions.common.math.Fraction;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.ParameterKeys;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.math.vector.Vector2D.v;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain.chain;
import static edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeType.BAR;
import static edu.colorado.phet.fractions.buildafraction.view.shapes.ContainerShapeNode.circleDiameter;
import static edu.colorado.phet.fractions.buildafraction.view.shapes.PieceNode._toFraction;
import static edu.colorado.phet.fractions.common.math.Fraction.sum;
import static edu.colorado.phet.fractions.common.view.FNode.getChildren;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components.container;

/**
 * One circle or bar for adding shapes to.  This is a child of ContainerNode, and may have 0-2 siblings.
 * If the fraction can become larger than 1, then more of these are used.
 *
 * @author Sam Reid
 */
class SingleContainerNode extends PNode {
    //Function that gets the fraction value.
    public static final F<SingleContainerNode, Fraction> _getFractionValue = new F<SingleContainerNode, Fraction>() {
        @Override public Fraction f( final SingleContainerNode singleContainerNode ) {
            return singleContainerNode.getFractionValue();
        }
    };
    //Gets the number of pieces that have been dropped in this SingleContainerNode
    public static final F<SingleContainerNode, Integer> _getNumberPieces = new F<SingleContainerNode, Integer>() {
        @Override public Integer f( final SingleContainerNode singleContainerNode ) {
            return singleContainerNode.getPieces().length();
        }
    };
    //Effect that sends all pieces back to the toolbox.
    public static final Effect<SingleContainerNode> _undoAll = new Effect<SingleContainerNode>() {
        @Override public void e( final SingleContainerNode s ) {
            s.undoAll();
        }
    };
    public final ContainerNode parent;
    private final PNode dottedLineLayer;
    private final ContainerShapeNode shapeLayer;

    public SingleContainerNode( final ShapeType shapeType, final ContainerNode parent, final ObservableProperty<Integer> selectedPieceSize ) {
        this.parent = parent;
        dottedLineLayer = new PNode() {{
            setPickable( false );
            setChildrenPickable( false );
            selectedPieceSize.addObserver( new VoidFunction1<Integer>() {
                public void apply( final Integer number ) {
                    removeAllChildren();

                    if ( dottedLineLayer != null ) {
                        dottedLineLayer.moveToFront();
                    }
                    final BasicStroke stroke = new BasicStroke( 3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{10, 10}, 0 );
                    if ( shapeType == BAR ) {
                        final double pieceWidth = ContainerShapeNode.rectangleWidth / number;
                        double x = pieceWidth;
                        for ( int i = 0; i < number - 1; i++ ) {
                            addChild( new PhetPPath( new Line2D.Double( x, 0, x, ContainerShapeNode.rectangleHeight ), stroke, Color.lightGray ) );
                            x += pieceWidth;
                        }
                    }
                    else {
                        final double pieceAngle = Math.PI * 2 / number;
                        double angle = 0;
                        for ( int i = 0; i < number && number >= 2; i++ ) {
                            Vector2D delta = Vector2D.createPolar( circleDiameter / 2, angle );
                            Vector2D center = new Vector2D( circleDiameter / 2, circleDiameter / 2 );
                            addChild( new PhetPPath( center.lineTo( center.plus( delta ) ), stroke, Color.lightGray ) );
                            angle += pieceAngle;
                        }
                    }
                }
            } );
        }};
        shapeLayer = new ContainerShapeNode( selectedPieceSize.get(), shapeType ) {{
            //Thicker outer stroke
            addChild( new PhetPPath( shapeType == BAR ? new Rectangle2D.Double( 0, 0, rectangleWidth, rectangleHeight )
                                                      : new Ellipse2D.Double( 0, 0, circleDiameter, circleDiameter ), Color.white, new BasicStroke( 2 ), Color.black ) );

            addInputEventListener( new SimSharingCanvasBoundedDragHandler( chain( container, parent.hashCode() ), SingleContainerNode.this, false ) {
                @Override protected ParameterSet getParametersForAllEvents( final PInputEvent event ) {
                    return super.getParametersForAllEvents( event ).with( ParameterKeys.shapeType, shapeType.name() ).with( edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.value, getFractionValue().toDouble() );
                }

                @Override public void mousePressed( final PInputEvent event ) {
                    super.mousePressed( event );
                    parent.moveToFront();
                    final AnimateToScale activity = new AnimateToScale( parent.parent.getContainerScale(), parent, BuildAFractionModule.ANIMATION_TIME );
                    activity.setDelegate( new PActivityDelegateAdapter() {
                        @Override public void activityFinished( final PActivity activity ) {
                            parent.updateExpansionButtonsEnabled();
                        }
                    } );
                    addActivity( activity );

                    parent.context.startDrag( parent );
                }

                @Override protected void dragNode( final DragEvent event ) {
                    parent.translate( event.delta.width, event.delta.height );
                }

                @Override public void mouseReleased( final PInputEvent event ) {
                    super.mouseReleased( event );
                    parent.context.endDrag( parent );
                }
            } );
            addInputEventListener( new CursorHandler() );
        }};
        addChild( shapeLayer );

        addChild( dottedLineLayer );

    }

    //Returns true if the parent is in the toolbox.
    public boolean isInToolbox() { return parent.isInToolbox(); }

    //Return true if the piece would overflow this container
    public boolean willOverflow( final PieceNode piece ) {
        final Fraction sum = getFractionValue().plus( piece.toFraction() );
        return sum.numerator > sum.denominator;
    }

    //Get the value just in this SingleContainerNode
    Fraction getFractionValue() { return sum( getPieces().map( _toFraction ) ); }

    //Gets a list of all the pieces that have been dropped in this SingleContainerNode
    private List<PieceNode> getPieces() {return getChildren( this, PieceNode.class );}

    //How far over should a new piece be added in?
    double getPiecesWidthUnscaled() {
        List<PieceNode> children = getPieces();
        return children.length() == 0 ? 0 :
               fj.data.List.iterableList( children ).maximum( FJUtils.ord( new F<PieceNode, Double>() {
                   @Override public Double f( final PieceNode r ) {
                       return r.getFullBounds().getMaxX();
                   }
               } ) ).getFullBounds().getMaxX();
    }

    //Assumes the piece is already in the right spot.
    public void addPiece( final PieceNode piece ) {
        Point2D offset = piece.getGlobalTranslation();
        addChild( piece );
        piece.setGlobalTranslation( offset );
        parent.pieceAdded();
        dottedLineLayer.moveToFront();
        piece.setAllPickable( false );
        piece.setInContainer( this );
    }

    //Send all pieces back to the toolbox.
    void undoAll() {
        for ( PieceNode child : getPieces() ) {
            parent.parent.undoPieceFromContainer( child );
        }
    }

    //Gets the location where a PieceNode should be dropped within this SingleContainerNode.
    public DropLocation getDropLocation( final PieceNode piece, final ShapeType shapeType ) {
        if ( shapeType == ShapeType.BAR ) {
            Vector2D strokeInsets = v( 1, 1 );
            return new DropLocation( v( getPiecesWidthUnscaled() * parent.parent.getContainerScale(), 0 ).minus( strokeInsets ), 0 );
        }
        else {

            Rectangle2D bounds = shapeLayer.getGlobalFullBounds();
            bounds = piece.globalToLocal( bounds );
            bounds = piece.localToParent( bounds );

            List<Double> pieceAngleExtents = getPieces().map( new F<PieceNode, Double>() {
                @Override public Double f( final PieceNode pieceNode ) {
                    return Math.PI * 2 / pieceNode.pieceSize;
                }
            } );
            double sumAngle = pieceAngleExtents.foldLeft( Doubles.add, 0.0 );

            //+1 offset fixes an alignment problem probably caused by stroke widths
            return new DropLocation( new Vector2D( bounds.getX() + 1, bounds.getY() ), sumAngle );
        }
    }

    //The user has dropped the parent ContainerNode in the collection box, so the dotted lines should be shown.
    public void setInCollectionBox() {
        dottedLineLayer.setVisible( true );
        dottedLineLayer.moveToFront();
    }

    //For undo, see if this container has a piece.  This is needed because the user could have added or removed more containers and hence dismissed pieces back to the toolbox.
    public boolean containsPiece() { return getFractionValue().numerator > 0; }

    //Undo the last dropped piece, by sending it back to the toolbox.
    public void undoLast() {
        parent.parent.undoPieceFromContainer( getPieces().sort( FJUtils.ord( new F<PieceNode, Double>() {
            @Override public Double f( final PieceNode pieceNode ) {
                return pieceNode.attachmentTime + 0.0;
            }
        } ) ).last() );
    }

    //Store the location in the drop list for "undo"
    public void addDropLocationToUndoList() { parent.addDropLocation( SingleContainerNode.this ); }

    //Fix the z-ordering after pieces have been added to its layer.
    public void moveDottedLineToFront() { dottedLineLayer.moveToFront(); }
}