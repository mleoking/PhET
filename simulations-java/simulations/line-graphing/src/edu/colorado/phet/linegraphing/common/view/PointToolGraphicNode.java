// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGResources.Images;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * The graphics portion of the point tool, independent of the model.
 * This was needed so that we could easily generate images of the point tool, for inclusion in the game reward.
 */
public class PointToolGraphicNode extends PhetPNode {

    protected static final NumberFormat COORDINATES_FORMAT = new DefaultDecimalFormat( "0" );
    private static final double COORDINATES_Y_CENTER = 21; // center of the display area, measured from the top of the unscaled image file

    private final PNode bodyNode;
    private final PPath backgroundNode;
    private final PText coordinatesNode;

    public PointToolGraphicNode( ImmutableVector2D point, Color background ) {

        // tool body
        bodyNode = new PImage( Images.POINT_TOOL );
        bodyNode.setOffset( -bodyNode.getFullBoundsReference().getWidth() / 2, -bodyNode.getFullBoundsReference().getHeight() );

        // coordinate display
        coordinatesNode = new PText();
        coordinatesNode.setFont( new PhetFont( Font.BOLD, 15 ) );

        // display background, shows through a transparent hole in the display area portion of the body image
        backgroundNode = new PPath( new Rectangle2D.Double( 5, 5,
                                                            bodyNode.getFullBoundsReference().getWidth() - 10,
                                                            0.55 * bodyNode.getFullBoundsReference().getHeight() ) );
        backgroundNode.setStroke( null );
        backgroundNode.setOffset( bodyNode.getOffset() );

        // rendering order
        addChild( backgroundNode );
        addChild( bodyNode );
        addChild( coordinatesNode );

        // default state
        setCoordinates( point );
        setBackground( background );
    }

    protected void setCoordinates( ImmutableVector2D point ) {
        setCoordinates( MessageFormat.format( Strings.POINT_XY, COORDINATES_FORMAT.format( point.getX() ), COORDINATES_FORMAT.format( point.getY() ) ) );
    }

    protected void setCoordinates( String s ) {
        coordinatesNode.setText( s );
        // horizontally centered
        coordinatesNode.setOffset( bodyNode.getFullBoundsReference().getCenterX() - ( coordinatesNode.getFullBoundsReference().getWidth() / 2 ),
                                   bodyNode.getFullBoundsReference().getMinY() + COORDINATES_Y_CENTER - ( coordinatesNode.getFullBoundsReference().getHeight() / 2 ) );
    }

    protected void setForeground( Color color ) {
        coordinatesNode.setTextPaint( color );
    }

    protected void setBackground( Color color ) {
        backgroundNode.setPaint( color );
    }
}
