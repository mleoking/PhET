// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.nodes.ArrowButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.energysystems.model.Carousel;
import edu.umd.cs.piccolo.PNode;

/**
 * PNode that allows the user to move through the elements contained in a
 * carousel.
 *
 * @author John Blanco
 */
public class CarouselElementSelectorNode extends PNode {
    private static final Dimension2D SIZE = new Dimension2DDouble( 40, 75 );
    private static final double ROUNDED_AMOUNT = SIZE.getWidth();
    private static final double BUTTON_WIDTH = SIZE.getWidth() * 0.8;
    private static final Color BACKGROUND_COLOR = new Color( 158, 187, 45 );
    private static final Color BUTTON_BASE_COLOR = new Color( 80, 201, 243 );

    private final ArrowButtonNode previousElementButton;
    private final ArrowButtonNode nextElementButton;

    public CarouselElementSelectorNode( Carousel carousel ) {
        PNode outline = new PhetPPath( new RoundRectangle2D.Double( 0, 0, SIZE.getWidth(), SIZE.getHeight(), ROUNDED_AMOUNT, ROUNDED_AMOUNT ),
                                       BACKGROUND_COLOR,
                                       new BasicStroke( 2 ),
                                       ColorUtils.darkerColor( BACKGROUND_COLOR, 0.25 ) );
        addChild( outline );
        double buttonInset = ( SIZE.getWidth() - BUTTON_WIDTH ) / 2;
        previousElementButton = new ArrowButtonNode( ArrowButtonNode.Orientation.UP, new ArrowButtonNode.ColorScheme( BUTTON_BASE_COLOR ) );
        previousElementButton.setScale( BUTTON_WIDTH / previousElementButton.getFullBoundsReference().getWidth() );
        previousElementButton.setEnabled( true );
        previousElementButton.centerFullBoundsOnPoint( SIZE.getWidth() / 2, previousElementButton.getFullBoundsReference().getHeight() / 2 + buttonInset );
        addChild( previousElementButton );
        nextElementButton = new ArrowButtonNode( ArrowButtonNode.Orientation.DOWN, new ArrowButtonNode.ColorScheme( BUTTON_BASE_COLOR ) );
        nextElementButton.setScale( BUTTON_WIDTH / nextElementButton.getFullBoundsReference().getWidth() );
        nextElementButton.setEnabled( true );
        nextElementButton.centerFullBoundsOnPoint( SIZE.getWidth() / 2, SIZE.getHeight() - nextElementButton.getFullBoundsReference().getHeight() / 2 - buttonInset );
        addChild( nextElementButton );
    }

}
