package edu.colorado.phet.fractionsintro.buildafraction.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_MITER;
import static java.awt.Color.black;

/**
 * Node for the fraction that starts empty and gets numbers added to numerator and denominator, and is moved to the scoring cells.
 *
 * @author Sam Reid
 */
public class FractionGraphic extends PNode {

    public final PhetPPath topBox;
    public final PhetPPath bottomBox;
    public final PhetPPath divisorLine;
    public final PImage splitButton;
    private NumberNode topTarget;
    private NumberNode bottomTarget;

    public FractionGraphic() {
        topBox = box( true );
        bottomBox = box( true );
        divisorLine = new PhetPPath( new Line2D.Double( 0, 0, 50, 0 ), new BasicStroke( 4, CAP_ROUND, JOIN_MITER ), black );

        splitButton = new PImage( Images.SPLIT_BLUE );
        final VBox box = new VBox( topBox, divisorLine, bottomBox );

        //Show a background behind it to make the entire shape draggable
        addChild( new RichPNode( new PhetPPath( RectangleUtils.expand( box.getFullBounds(), 5, 5 ), BuildAFractionCanvas.TRANSPARENT ), box ) );

        Rectangle2D bounds = divisorLine.getFullBounds();
        bounds = box.localToParent( bounds );
        splitButton.setOffset( bounds.getMaxX() + 2, bounds.getCenterY() - splitButton.getFullBounds().getHeight() / 2 );
        splitButton.addInputEventListener( new CursorHandler() );
        splitButton.setVisible( false );
        addChild( splitButton );
    }

    private static PhetPPath box( boolean showOutline ) {
        return new PhetPPath( new Rectangle2D.Double( 0, 0, 40, 50 ), new BasicStroke( 2, BasicStroke.CAP_SQUARE, JOIN_MITER, 1, new float[] { 10, 6 }, 0 ), showOutline ? Color.red : BuildAFractionCanvas.TRANSPARENT );
    }

    public void setTarget( final PhetPPath box, final NumberNode numberNode ) {
        if ( box == topBox ) {
            topTarget = numberNode;
        }
        else if ( box == bottomBox ) {
            bottomTarget = numberNode;
        }
        else { throw new RuntimeException( "No such box!" ); }
    }

    public boolean isComplete() { return topTarget != null && bottomTarget != null; }

    public Fraction getValue() { return new Fraction( topTarget.number, bottomTarget.number ); }

    public PNode getTopNumber() {return topTarget;}

    public PNode getBottomNumber() {return bottomTarget;}

    public void translateAll( final PDimension delta ) {
        translate( delta.getWidth(), delta.getHeight() );
        if ( topTarget != null ) { topTarget.translate( delta.getWidth(), delta.getHeight() ); }
        if ( bottomTarget != null ) { bottomTarget.translate( delta.getWidth(), delta.getHeight() ); }
    }

    public void setAllPickable( final boolean b ) {
        setPickable( b );
        setChildrenPickable( b );
        if ( topTarget != null ) { topTarget.setAllPickable( b );}
        if ( bottomTarget != null ) { bottomTarget.setAllPickable( b );}
    }
}