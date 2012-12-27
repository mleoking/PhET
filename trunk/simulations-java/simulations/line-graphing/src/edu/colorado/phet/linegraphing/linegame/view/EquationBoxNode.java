// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.linegraphing.common.LGResources.Images;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Box around an equation in the "Line Game".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EquationBoxNode extends PNode {

    private static final int X_MARGIN = 20;
    private static final int Y_MARGIN = 10;

    private final PNode correctIconNode, incorrectIconNode;

    public EquationBoxNode( String title, Color titleColor, PDimension boxSize, PNode equationNode ) {

        PText titleNode = new PText( title );
        titleNode.setTextPaint( titleColor );
        titleNode.setFont( new PhetFont( Font.BOLD, 24 ) );
        final double maxTitleWidth = boxSize.getWidth() - ( 2 * X_MARGIN );
        // If title is too wide to fit in box, then scale it.
        if ( titleNode.getFullBoundsReference().getWidth() > maxTitleWidth ) {
            titleNode.scale( maxTitleWidth / titleNode.getFullBoundsReference().getWidth() );
        }

        PPath boxNode = new PPath( new RoundRectangle2D.Double( 0, 0, boxSize.getWidth(), boxSize.getHeight(), 20, 20 ) );
        boxNode.setStroke( new BasicStroke( 1f ) );
        boxNode.setStrokePaint( Color.BLACK );
        boxNode.setPaint( new Color( 238, 238, 238 ) );

        correctIconNode = new PImage( Images.CHECK_MARK );
        incorrectIconNode = new PImage( Images.X_MARK );

        PNode equationWrapperNode = new ZeroOffsetNode( equationNode );

        // rendering order
        addChild( boxNode );
        addChild( titleNode );
        addChild( equationWrapperNode );
        addChild( correctIconNode );
        addChild( incorrectIconNode );

        // layout
        {
            // title in upper left
            titleNode.setOffset( X_MARGIN, Y_MARGIN );
            // equation left-justified, vertically centered in space below title
            final double equationCenterY = titleNode.getFullBoundsReference().getMaxY() + ( ( boxSize.getHeight() - titleNode.getFullBoundsReference().getMaxY() ) / 2 );
            equationWrapperNode.setOffset( X_MARGIN, equationCenterY - ( equationWrapperNode.getFullBoundsReference().getHeight() / 2 ) );
            // icons in upper-right corner
            final int iconXMargin = 10;
            final int iconYMargin = 5;
            correctIconNode.setOffset( boxNode.getFullBoundsReference().getMaxX() - correctIconNode.getFullBoundsReference().getWidth() - iconXMargin,
                                       boxNode.getFullBoundsReference().getMinY() + iconYMargin );
            incorrectIconNode.setOffset( boxNode.getFullBoundsReference().getMaxX() - incorrectIconNode.getFullBoundsReference().getWidth() - iconXMargin,
                                         boxNode.getFullBoundsReference().getMinY() + iconYMargin );
        }

        // icons are initially hidden
        correctIconNode.setVisible( false );
        incorrectIconNode.setVisible( false );
    }

    public void setCorrectIconVisible( boolean visible ) {
        correctIconNode.setVisible( visible );
    }

    public void setIncorrectIconVisible( boolean visible ) {
        incorrectIconNode.setVisible( visible );
    }
}
