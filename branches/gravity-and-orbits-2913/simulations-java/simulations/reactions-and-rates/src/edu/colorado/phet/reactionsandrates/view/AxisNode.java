// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.view;

import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * AxisNode
 * <p/>
 * A PNode with text and an arrow to designate an axis of the EnergyView
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class AxisNode extends PNode {
    private static final int TEXT_PADDING = 5;

    public static class VerticalAlignment {
        private VerticalAlignment() {
        }
    }

    public static final VerticalAlignment TOP = new VerticalAlignment();
    public static final VerticalAlignment BOTTOM = new VerticalAlignment();

    public static class Orientation {
        private Orientation() {
        }
    }

    public static final Orientation HORIZONTAL = new Orientation();
    public static final Orientation VERTICAL = new Orientation();


    public Font axisFont;

    public AxisNode( String label,
                     double length,
                     Color color,
                     Orientation orientation,
                     VerticalAlignment textVerticalAlignment ) {

        double arrowNodeWidth;

        if( orientation == HORIZONTAL ) {
            arrowNodeWidth = addLineNode( length, color );
        }
        else {
            arrowNodeWidth = addArrowNode( length, color, textVerticalAlignment );
        }

        axisFont = new PhetFont( PhetFont.getDefaultFontSize()+1,true );
        PText labelNode = new PText( label );
        labelNode.setFont( axisFont );
        labelNode.setTextPaint( color );
        double labelOffsetY = 0;
        if( textVerticalAlignment == BOTTOM ) {
            labelOffsetY = TEXT_PADDING;
        }
        if( textVerticalAlignment == TOP ) {
            labelOffsetY = -TEXT_PADDING - labelNode.getHeight();
        }
        labelNode.setOffset( ( arrowNodeWidth - labelNode.getFullBounds().getWidth() ) / 2, labelOffsetY );
        addChild( labelNode );

        if( orientation == VERTICAL ) {
            this.rotate( -Math.PI / 2 );
        }
    }

    private double addArrowNode( double length, Color color, VerticalAlignment textVerticalAlignment ) {
        Arrow arrow = new Arrow( new Point2D.Double( 0, 0 ),
                                 new Point2D.Double( length, 0 ),
                                 12, 8, 1 );

        PPath arrowNode = addAxisNode( arrow.getShape(), color );

        return arrowNode.getFullBounds().getWidth();
    }

    private double addLineNode( double length, Color color ) {
        Rectangle2D rect = new Rectangle2D.Double( 0, 0, length, 1 );

        return addAxisNode( rect, color ).getFullBounds().getWidth();
    }

    private PPath addAxisNode( Shape line, Color color ) {
        PPath arrowNode = new PPath( line );

        arrowNode.setPaint( color );
        arrowNode.setStroke( new BasicStroke( 1 ) );
        arrowNode.setStrokePaint( color );
        addChild( arrowNode );

        return arrowNode;
    }
}
