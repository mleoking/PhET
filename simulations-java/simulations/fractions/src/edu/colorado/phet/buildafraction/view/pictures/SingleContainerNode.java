package edu.colorado.phet.buildafraction.view.pictures;

import fj.Effect;
import fj.F;
import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fractions.util.FJUtils;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.buildafraction.view.pictures.RectangularPiece._toFraction;
import static edu.colorado.phet.fractions.view.FNode.getChildren;
import static edu.colorado.phet.fractionsintro.intro.model.Fraction.sum;

/**
 * @author Sam Reid
 */
public class SingleContainerNode extends PNode {
    public final ContainerNode parent;

    SingleContainerNode( final ContainerNode parent, final ObservableProperty<Integer> number ) {
        this.parent = parent;
        final PNode dottedLineLayer = new PNode() {{
            number.addObserver( new VoidFunction1<Integer>() {
                public void apply( final Integer number ) {
                    removeAllChildren();
                    final double pieceWidth = SimpleContainerNode.width / number;
                    double x = pieceWidth;
                    for ( int i = 0; i < number - 1; i++ ) {
                        addChild( new PhetPPath( new Line2D.Double( x, 0, x, SimpleContainerNode.height ), new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[] { 10, 10 }, 0 ), Color.lightGray ) );
                        x += pieceWidth;
                    }
                }
            } );
        }};
        SimpleContainerNode node = new SimpleContainerNode( number.get(), Color.white ) {{
            //Thicker outer stroke
            addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, width, height ), Color.white, new BasicStroke( 2 ), Color.black ) );

            addChild( dottedLineLayer );

            addInputEventListener( new SimSharingDragHandler( null, true ) {
                @Override protected void startDrag( final PInputEvent event ) {
                    super.startDrag( event );
                    parent.moveToFront();
                    addActivity( new AnimateToScale( parent, 1.0, 200 ) );
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
        addChild( node );

    }

    public boolean isInToolbox() { return parent.isInToolbox(); }

    //Return true if the piece would overflow this container
    public boolean willOverflow( final RectangularPiece piece ) {
        final Fraction sum = getFractionValue().plus( piece.toFraction() );
        return sum.numerator > sum.denominator;
    }

    public Fraction getFractionValue() { return sum( getRectangularPieces().map( _toFraction ) ); }

    public static F<SingleContainerNode, Fraction> _getFractionValue = new F<SingleContainerNode, Fraction>() {
        @Override public Fraction f( final SingleContainerNode singleContainerNode ) {
            return singleContainerNode.getFractionValue();
        }
    };

    private List<RectangularPiece> getRectangularPieces() {return getChildren( this, RectangularPiece.class );}

    //How far over should a new piece be added in?
    public double getPiecesWidth() {
        List<RectangularPiece> children = getRectangularPieces();
        return children.length() == 0 ? 0 :
               fj.data.List.iterableList( children ).maximum( FJUtils.ord( new F<RectangularPiece, Double>() {
                   @Override public Double f( final RectangularPiece r ) {
                       return r.getFullBounds().getMaxX();
                   }
               } ) ).getFullBounds().getMaxX();
    }

    //Assumes the piece is already in the right spot.
    public void addPiece( final RectangularPiece piece ) {
        Point2D offset = piece.getGlobalTranslation();
        addChild( piece );
        piece.setGlobalTranslation( offset );
        parent.pieceAdded( piece );
    }

    public void splitAll() {
        int numPieces = getRectangularPieces().length();
        double separationBetweenPieces = 4;
        double totalDeltaSpacing = separationBetweenPieces * ( numPieces - 1 );
        int index = 0;
        LinearFunction f = new LinearFunction( 0, numPieces - 1, -totalDeltaSpacing / 2, totalDeltaSpacing / 2 );
        for ( RectangularPiece child : getRectangularPieces() ) {
            parent.parent.splitPieceFromContainer( child );
        }
    }

    public static final Effect<SingleContainerNode> _splitAll = new Effect<SingleContainerNode>() {
        @Override public void e( final SingleContainerNode s ) {
            s.splitAll();
        }
    };
}