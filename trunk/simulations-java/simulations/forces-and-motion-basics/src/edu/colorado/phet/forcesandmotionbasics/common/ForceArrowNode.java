// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.common;

import fj.data.List;
import fj.data.Option;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.forcesandmotionbasics.common.AbstractForcesAndMotionBasicsCanvas.DEFAULT_FONT;
import static edu.colorado.phet.forcesandmotionbasics.common.AbstractForcesAndMotionBasicsCanvas.INSET;

/**
 * Draws a force arrow node, including an arrow and text for the label and optionally the value (such as 50N).
 * This class is immutable and is regenerated if any dependencies change.
 * It is non-interactive, just a display.
 *
 * @author Sam Reid
 */
public class ForceArrowNode extends PNode {

    public PhetPText nameNode;
    private PhetPText valueNode;

    public ForceArrowNode( final boolean transparent, final Vector2D tail, final double forceInNewtons, final String name, Color color, final TextLocation textLocation, final boolean showValues ) {
        this( transparent, tail, forceInNewtons, name, color, textLocation, showValues, Option.<ForceArrowNode>none() );
    }

    public ForceArrowNode( final boolean transparent, final Vector2D tail, final double forceInNewtons, final String name, Color color, final TextLocation textLocation, final boolean showValues, final Option<ForceArrowNode> other ) {

        //Choose a single scale factor that works in all of the tabs.
        final double value = forceInNewtons * 3.625;
        if ( value == 0 && textLocation == TextLocation.SIDE ) { return; }
        else if ( value == 0 && textLocation == TextLocation.TOP ) {
            showTextOnly( tail );
            return;
        }
        final double arrowScale = 1.2;
        final Point2D.Double tipLocation = tail.plus( value, 0 ).toPoint2D();
        final ArrowNode arrowNode = new ArrowNode( tail.toPoint2D(), tipLocation, 30 * arrowScale, 40 * arrowScale, 20 * arrowScale, 0.5, false );
        arrowNode.setPaint( transparent ? new Color( color.getRed(), color.getGreen(), color.getBlue(), 175 ) : color );
        arrowNode.setStroke( transparent ? new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[] { 6, 4 }, 0 ) : new BasicStroke( 1 ) );
        addChild( arrowNode );
        nameNode = new PhetPText( name, DEFAULT_FONT ) {{
            if ( textLocation == TextLocation.SIDE ) {
                if ( value > 0 ) {
                    setOffset( arrowNode.getFullBounds().getMaxX() + INSET, arrowNode.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
                }
                else {
                    setOffset( arrowNode.getFullBounds().getMinX() - getFullBounds().getWidth() - INSET, arrowNode.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
                }
            }
            else {
                setOffset( arrowNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, arrowNode.getFullBounds().getY() - getFullBounds().getHeight() - INSET - 2 );
            }
        }};
        addChild( nameNode );

        //If the text intersects, move the name down to avoid intersection.
        if ( intersectsAny( other ) ) {
            nameNode.translate( 0, 40 );
        }

        if ( showValues ) {
            final String text = new DecimalFormat( "0" ).format( Math.abs( forceInNewtons ) );
            valueNode = new PhetPText( text + "N", new PhetFont( 16, true ) ) {{
                centerFullBoundsOnPoint( arrowNode.getFullBounds().getCenter2D() );
                double dx = 2;
                translate( forceInNewtons < 0 ? dx :
                           forceInNewtons > 0 ? -dx :
                           0, 0 );

                //For single character text (9N and lower), show below the name
                if ( text.length() <= 1 ) {
                    setOffset( nameNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, nameNode.getFullBounds().getMaxY() - 3 );
                }
            }};
            addChild( valueNode );
        }
    }

    private boolean intersectsAny( final Option<ForceArrowNode> other ) {
        if ( !other.isSome() ) { return false; }
        boolean intersect = false;
        for ( Rectangle2D otherTextRectangle : other.some().getTextRectangles() ) {
            for ( Rectangle2D myTextRectangle : getTextRectangles() ) {
                if ( otherTextRectangle.intersects( myTextRectangle ) ) {
                    intersect = true;
                }
            }
        }
        return intersect;
    }

    private List<Rectangle2D> getTextRectangles() {
        ArrayList<Rectangle2D> list = new ArrayList<Rectangle2D>();
        if ( valueNode != null ) {
            list.add( valueNode.getGlobalFullBounds() );
        }
        if ( nameNode != null ) {
            list.add( nameNode.getGlobalFullBounds() );
        }
        return List.iterableList( list );
    }

    private void showTextOnly( final Vector2D tail ) {
        addChild( new PhetPText( "Sum of Forces = 0", DEFAULT_FONT ) {{
            centerBoundsOnPoint( tail.x, tail.y - 46 );//Fine tune location so that it shows at the same y location as the text would if there were an arrow
        }} );
    }
}
