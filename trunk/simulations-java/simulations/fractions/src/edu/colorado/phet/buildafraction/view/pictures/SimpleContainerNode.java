// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.buildafraction.view.pictures;

import fj.F;
import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.util.FJUtils;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;

/**
 * Some copied from NumberNode, may need to be remerged.
 *
 * @author Sam Reid
 */
public class SimpleContainerNode extends PNode {
    private double initialX;
    private double initialY;
    private int number;

    static final double scale = 1.7;
    public static final double width = 130 * scale;
    public static final double height = 55 * scale;
    private boolean inTargetCell = false;

    public SimpleContainerNode( final int number, final Color fill ) {
        this.number = number;
        final PNode content = new PNode() {{
            for ( int i = 0; i < number; i++ ) {
                final double pieceWidth = width / number;
                if ( fill != null ) {
                    addChild( new PhetPPath( new Rectangle2D.Double( pieceWidth * i, 0, pieceWidth, height ), fill, new BasicStroke( 1 ), Color.black ) );
                }
                else {
                    addChild( new PhetPPath( new Rectangle2D.Double( pieceWidth * i, 0, pieceWidth, height ), new BasicStroke( 1 ), Color.black ) );
                }
            }
            //Thicker outer stroke
            addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, width, height ), new BasicStroke( 2 ), Color.black ) );
        }};

        addChild( content );
    }

    public double getInitialX() { return initialX; }

    public double getInitialY() { return initialY; }

    public void animateHome() { animateToPositionScaleRotation( getInitialX(), getInitialY(), 1, 0, 200 ); }

    public void addPiece( final RectangularPiece piece ) {
        Point2D offset = piece.getGlobalTranslation();
        addChild( piece );
        piece.setGlobalTranslation( offset );
    }

    public static Rectangle2D.Double createRect( int number ) {
        final double pieceWidth = width / number;
        return new Rectangle2D.Double( pieceWidth * number, 0, pieceWidth, height );
    }

    //How far over should a new piece be added in?
    public double getPiecesWidth() {
        List<RectangularPiece> children = getChildPieces();
        return children.length() == 0 ? 0 :
               List.iterableList( children ).maximum( FJUtils.ord( new F<RectangularPiece, Double>() {
                   @Override public Double f( final RectangularPiece r ) {
                       return r.getFullBounds().getMaxX();
                   }
               } ) ).getFullBounds().getMaxX();
    }

    private List<RectangularPiece> getChildPieces() {
        ArrayList<RectangularPiece> children = new ArrayList<RectangularPiece>();
        for ( Object c : getChildrenReference() ) {
            if ( c instanceof RectangularPiece ) {
                children.add( (RectangularPiece) c );
            }
        }
        return List.iterableList( children );
    }

    public Fraction getFractionValue() {
        return Fraction.sum( getChildPieces().map( new F<RectangularPiece, Fraction>() {
            @Override public Fraction f( final RectangularPiece r ) {
                return r.toFraction();
            }
        } ) );
    }

    public static F<SimpleContainerNode, Fraction> _getFractionValue = new F<SimpleContainerNode, Fraction>() {
        @Override public Fraction f( final SimpleContainerNode containerNode ) {
            return containerNode.getFractionValue();
        }
    };

    public boolean isAtStartingLocation() { return getXOffset() == initialX && getYOffset() == initialY; }

    public Boolean isInTargetCell() {return inTargetCell;}

    public static F<SimpleContainerNode, Boolean> _isInTargetCell = new F<SimpleContainerNode, Boolean>() {
        @Override public Boolean f( final SimpleContainerNode containerNode ) {
            return containerNode.isInTargetCell();
        }
    };

    public void setInTargetCell( final boolean inTargetCell ) { this.inTargetCell = inTargetCell; }
}