// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.platetectonics.PlateTectonicsResources;
import edu.colorado.phet.platetectonics.view.ColorMode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.platetectonics.PlateTectonicsConstants.PANEL_TITLE_FONT;

/**
 * Displays a legend for a particular coloring method of the earth, with labels
 */
public class LegendPanel extends PNode {
    public LegendPanel( ColorMode colorMode ) {
        final PNode title = new PText( PlateTectonicsResources.Strings.LEGEND ) {{
            setFont( PANEL_TITLE_FONT );
        }};
        addChild( title );

        Color minColor = colorMode.getMaterial().getMinColor();
        Color maxColor = colorMode.getMaterial().getMaxColor();

        final PhetPPath gradient = new PhetPPath( new Rectangle2D.Double( 0, 0, 100, 10 ), new GradientPaint( 0, 0, minColor, 100, 0, maxColor ), new BasicStroke( 1 ), Color.BLACK );
        addChild( gradient );

        final PText minLabel = new PText( colorMode.getMinString() );
        addChild( minLabel );
        final PText maxLabel = new PText( colorMode.getMaxString() );
        addChild( maxLabel );

        double verticalPadding = 2;
        double horizontalPadding = 5;

        double yOffset = title.getFullBounds().getHeight() + verticalPadding;
        double height = Math.max( Math.max( minLabel.getFullBounds().getHeight(), maxLabel.getFullBounds().getHeight() ), gradient.getFullBounds().getHeight() );
        minLabel.setOffset( 0, yOffset + ( height - minLabel.getFullBounds().getHeight() ) / 2 );
        gradient.setOffset( minLabel.getFullBounds().getMaxX() + horizontalPadding, yOffset + ( height - gradient.getFullBounds().getHeight() ) / 2 );
        maxLabel.setOffset( gradient.getFullBounds().getMaxX() + horizontalPadding, yOffset + ( height - maxLabel.getFullBounds().getHeight() ) / 2 );

        title.setOffset( ( maxLabel.getFullBounds().getMaxX() - title.getFullBounds().getWidth() ) / 2, 0 );
    }
}
