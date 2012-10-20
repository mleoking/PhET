// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Box around an equation in the "Line Game".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EquationBoxNode extends PNode {

    public EquationBoxNode( String title, PDimension boxSize, EquationNode equationNode ) {

        PText titleNode = new PText( title );
        titleNode.setTextPaint( Color.BLACK );
        titleNode.setFont( new PhetFont( Font.BOLD, 24 ) );

        PPath boxNode = new PPath( new RoundRectangle2D.Double( 0, 0, boxSize.getWidth(), boxSize.getHeight(), 20, 20 ) );
        boxNode.setStroke( new BasicStroke( 1f ) );
        boxNode.setStrokePaint( Color.BLACK );
        boxNode.setPaint( new Color( 238, 238, 238 ) );

        PNode equationWrapperNode = new ZeroOffsetNode( equationNode );

        // rendering order
        addChild( boxNode );
        addChild( titleNode );
        addChild( equationWrapperNode );

        // layout
        {
            final double xMargin = 20;
            final double yMargin = 10;
            // title in upper left
            titleNode.setOffset( xMargin, yMargin );
            // equation left-justified, vertically centered in space below title
            final double equationCenterY = titleNode.getFullBoundsReference().getMaxY() + ( ( boxSize.getHeight() - titleNode.getFullBoundsReference().getMaxY() ) / 2 );
            equationWrapperNode.setOffset( xMargin, equationCenterY - ( equationWrapperNode.getFullBoundsReference().getHeight() / 2 ) );
        }
    }
}
