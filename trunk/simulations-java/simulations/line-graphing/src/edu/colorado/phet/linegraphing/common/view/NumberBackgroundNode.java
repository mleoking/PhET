// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * An integer value displayed on a background with rounded corners.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class NumberBackgroundNode extends PComposite {

    public NumberBackgroundNode( double value, NumberFormat format,
                                 PhetFont font, Color textColor, Color backgroundColor,
                                 double xMargin, double yMargin, double cornerRadius ) {

        PNode textNode = new PhetPText( format.format( value ), font, textColor );

        // background for the label
        final RoundRectangle2D.Double backgroundShape = new RoundRectangle2D.Double( 0, 0,
                                                                                     textNode.getFullBoundsReference().getWidth() + ( 2 * xMargin ),
                                                                                     textNode.getFullBoundsReference().getHeight() + ( 2 * yMargin ),
                                                                                     cornerRadius, cornerRadius );

        // Put an opaque background behind a translucent background, so that we can vary the saturation of the slope color using the alpha channel.
        PPath opaqueBackgroundNode = new PPath( backgroundShape );
        opaqueBackgroundNode.setPaint( Color.WHITE );
        opaqueBackgroundNode.setStroke( null );

        PPath translucentBackgroundNode = new PPath( backgroundShape );
        translucentBackgroundNode.setPaint( backgroundColor );
        translucentBackgroundNode.setStroke( null );

        // rendering order
        addChild( opaqueBackgroundNode );
        addChild( translucentBackgroundNode );
        addChild( textNode );

        // layout
        textNode.setOffset( xMargin, yMargin );
    }
}
