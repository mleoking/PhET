// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.pictures;

import fj.Effect;
import fj.F;
import fj.data.List;
import fj.function.Doubles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeType;
import edu.colorado.phet.fractions.buildafraction.view.pictures.PictureSceneNode.DropLocation;
import edu.colorado.phet.fractions.common.util.FJUtils;
import edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.common.phetcommon.math.vector.Vector2D.v;
import static edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeType.BAR;
import static edu.colorado.phet.fractions.buildafraction.view.pictures.PieceNode._toFraction;
import static edu.colorado.phet.fractions.buildafraction.view.pictures.SimpleContainerNode.circleDiameter;
import static edu.colorado.phet.fractions.common.view.FNode.getChildren;
import static edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction.sum;

/**
 * @author Sam Reid
 */
public class SingleContainerNode extends PNode {
    public final ContainerNode parent;
    private final PNode dottedLineLayer;
    private final SimpleContainerNode shapeLayer;

    public SingleContainerNode( final ShapeType shapeType, final ContainerNode parent, final ObservableProperty<Integer> number ) {
        this.parent = parent;
        dottedLineLayer = new PNode() {{
            number.addObserver( new VoidFunction1<Integer>() {
                public void apply( final Integer number ) {
                    removeAllChildren();

                    if ( shapeType == BAR ) {
                        final double pieceWidth = SimpleContainerNode.rectangleWidth / number;
                        double x = pieceWidth;
                        for ( int i = 0; i < number - 1; i++ ) {
                            addChild( new PhetPPath( new Line2D.Double( x, 0, x, SimpleContainerNode.rectangleHeight ), new BasicStroke( 1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[] { 10, 10 }, 0 ), Color.lightGray ) );
                            x += pieceWidth;
                        }
                    }
                    else {
                        final double pieceAngle = Math.PI * 2 / number;
                        double angle = 0;
                        for ( int i = 0; i < number && number >= 2; i++ ) {
                            Vector2D delta = Vector2D.createPolar( circleDiameter / 2, angle );
                            Vector2D center = new Vector2D( circleDiameter / 2, circleDiameter / 2 );
                            addChild( new PhetPPath( center.lineTo( center.plus( delta ) ), new BasicStroke( 1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[] { 10, 10 }, 0 ), Color.lightGray ) );
                            angle += pieceAngle;
                        }
                    }
                }
            } );
        }};
        shapeLayer = new SimpleContainerNode( number.get(), Color.white, shapeType ) {{
            //Thicker outer stroke
            addChild( new PhetPPath( shapeType == BAR ? new Rectangle2D.Double( 0, 0, rectangleWidth, rectangleHeight )
                                                      : new Ellipse2D.Double( 0, 0, circleDiameter, circleDiameter ), Color.white, new BasicStroke( 2 ), Color.black ) );

            addInputEventListener( new SimSharingDragHandler( null, true ) {
                @Override protected void startDrag( final PInputEvent event ) {
                    super.startDrag( event );
                    parent.moveToFront();
                    addActivity( new AnimateToScale( parent, 200 ) );
                    parent.notifyListeners();
                }

                @Override protected void drag( final PInputEvent event ) {
                    super.drag( event );
                    final PDimension delta = event.getDeltaRelativeTo( getParent() );
                    parent.translate( delta.width, delta.height );
                    parent.notifyListeners();
                }

                @Override protected void endDrag( final PInputEvent event ) {
                    super.endDrag( event );
                    parent.context.endDrag( parent, event );
                    parent.notifyListeners();
                }
            } );
            addInputEventListener( new CursorHandler() );
        }};
        addChild( shapeLayer );

        addChild( dottedLineLayer );

    }

    public boolean isInToolbox() { return parent.isInToolbox(); }

    //Return true if the piece would overflow this container
    public boolean willOverflow( final PieceNode piece ) {
        final Fraction sum = getFractionValue().plus( piece.toFraction() );
        return sum.numerator > sum.denominator;
    }

    public Fraction getFractionValue() { return sum( getPieces().map( _toFraction ) ); }

    public static F<SingleContainerNode, Fraction> _getFractionValue = new F<SingleContainerNode, Fraction>() {
        @Override public Fraction f( final SingleContainerNode singleContainerNode ) {
            return singleContainerNode.getFractionValue();
        }
    };

    private List<PieceNode> getPieces() {return getChildren( this, PieceNode.class );}

    //How far over should a new piece be added in?
    public double getPiecesWidth() {
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
        parent.pieceAdded( piece );
        dottedLineLayer.moveToFront();
        piece.setAllPickable( false );
    }

    public void splitAll() {
        for ( PieceNode child : getPieces() ) {
            parent.parent.splitPieceFromContainer( child );
        }
    }

    public static final Effect<SingleContainerNode> _splitAll = new Effect<SingleContainerNode>() {
        @Override public void e( final SingleContainerNode s ) {
            s.splitAll();
        }
    };

    //TODO: Refactor to subclasses
    public DropLocation getDropLocation( final PieceNode piece, final ShapeType shapeType ) {
        if ( shapeType == ShapeType.BAR ) {
            Vector2D strokeInsets = v( 1, 1 );
            return new DropLocation( v( getPiecesWidth(), 0 ).minus( strokeInsets ), 0 );
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

    public void setInTargetCell( final boolean inTargetCell ) {
        dottedLineLayer.setVisible( !inTargetCell );
    }
}