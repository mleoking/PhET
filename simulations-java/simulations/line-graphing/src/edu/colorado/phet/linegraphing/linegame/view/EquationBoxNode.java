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

    public EquationBoxNode( EquationNode equationNode, String title, PDimension boxSize ) {

        PPath boxNode = new PPath( new RoundRectangle2D.Double( 0, 0, boxSize.getWidth(), boxSize.getHeight(), 10, 10 ) );
        boxNode.setStroke( new BasicStroke( 1f ) );
        boxNode.setStrokePaint( Color.BLACK );
        boxNode.setPaint( new Color( 238, 238, 238 ) );

        PText titleNode = new PText( title );
        titleNode.setTextPaint( Color.BLACK );
        titleNode.setFont( new PhetFont( Font.BOLD, 24 ) );

        PNode zeroOffsetNode = new ZeroOffsetNode( equationNode );

        // rendering order
        addChild( boxNode );
        addChild( titleNode );
        addChild( zeroOffsetNode );

        // layout
        final double xMargin = 20;
        final double yMargin = 20;
        titleNode.setOffset( xMargin, yMargin );
        zeroOffsetNode.setOffset( xMargin, ( 0.65 * boxSize.getHeight() ) - ( zeroOffsetNode.getFullBoundsReference().getHeight() / 2 ) );
    }
}
