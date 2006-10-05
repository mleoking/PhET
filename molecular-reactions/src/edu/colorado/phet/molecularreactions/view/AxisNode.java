/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.colorado.phet.common.view.graphics.Arrow;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.awt.*;
import java.util.Set;
import java.util.HashSet;

/**
 * EnergyProfilePanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class AxisNode extends PNode {
    public static class VerticalAlignment {
        private VerticalAlignment() {}
    }
    public static final VerticalAlignment TOP = new VerticalAlignment();
    public static final VerticalAlignment BOTTOM = new VerticalAlignment();

    public static class Orientation {
        private Orientation() {}
    }
    public static final Orientation HORIZONTAL = new Orientation();
    public static final Orientation VERTICAL = new Orientation();
    public static final Font axisFont = UIManager.getFont( "Label.font" );



    public AxisNode( String label,
                     double length,
                     Color color,
                     Orientation orientation,
                     VerticalAlignment textVerticalAlignment ) {

        Arrow arrow = new Arrow( new Point2D.Double( ),
                                 new Point2D.Double( length, 0),
                                 12, 8, 1 );
        PPath arrowNode = new PPath( arrow.getShape() );
        arrowNode.setPaint( color );
        arrowNode.setStroke( new BasicStroke( 1 ));
        arrowNode.setStrokePaint( color );
        addChild( arrowNode );

        PText labelNode = new PText( label );
        labelNode.setFont( axisFont );
        labelNode.setTextPaint( color );
        double labelOffsetY = 0;
        if( textVerticalAlignment == TOP ) {
            arrowNode.setOffset( 0, 20 );
        }
        else if( textVerticalAlignment == BOTTOM ) {
            labelOffsetY = 10;
        }
        labelNode.setOffset( (arrowNode.getFullBounds().getWidth() - labelNode.getFullBounds().getWidth()) / 2, labelOffsetY );
        addChild( labelNode );

        if( orientation == VERTICAL ) {
            this.rotate( -Math.PI / 2 );
        }
    }
}
